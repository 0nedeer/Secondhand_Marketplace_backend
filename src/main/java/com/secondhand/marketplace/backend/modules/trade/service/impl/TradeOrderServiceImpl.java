package com.secondhand.marketplace.backend.modules.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.trade.dto.CancelOrderRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateLogisticsTraceRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateOrderItemRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateOrderRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateShipmentRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.PickupVerifyRequest;
import com.secondhand.marketplace.backend.modules.trade.entity.LogisticsTrace;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderItem;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderShipment;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderStatusLog;
import com.secondhand.marketplace.backend.modules.trade.entity.TradeOrder;
import com.secondhand.marketplace.backend.modules.trade.entity.TradeProduct;
import com.secondhand.marketplace.backend.modules.trade.mapper.LogisticsTraceMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.OrderShipmentMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.OrderItemMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.OrderStatusLogMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.ProductImageMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.TradeOrderMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.TradeProductMapper;
import com.secondhand.marketplace.backend.modules.trade.service.TradeOrderService;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderDetailVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderItemVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderListItemVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderPageVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderShipmentVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderStatusLogVO;
import com.secondhand.marketplace.backend.modules.trade.vo.LogisticsTraceVO;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeOrderServiceImpl implements TradeOrderService {

    private static final String STATUS_PENDING_PAYMENT = "pending_payment";
    private static final String STATUS_PAID_PENDING_SHIP = "paid_pending_ship";
    private static final String STATUS_SHIPPED = "shipped";
    private static final String STATUS_DELIVERED = "delivered";
    private static final String STATUS_COMPLETED = "completed";
    private static final String STATUS_CANCELLED = "cancelled";

    private static final String TRADE_MODE_SHIPPING = "shipping";
    private static final String TRADE_MODE_PICKUP = "pickup";

    private static final String SHIPMENT_STATUS_TO_SHIP = "to_ship";
    private static final String SHIPMENT_STATUS_IN_TRANSIT = "in_transit";
    private static final String SHIPMENT_STATUS_SIGNED = "signed";

    private static final Set<String> FINAL_ORDER_STATUSES = Set.of(STATUS_COMPLETED, STATUS_CANCELLED, "closed");

    private final TradeOrderMapper tradeOrderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;
    private final OrderShipmentMapper orderShipmentMapper;
    private final LogisticsTraceMapper logisticsTraceMapper;
    private final TradeProductMapper tradeProductMapper;
    private final ProductImageMapper ProductImageMapper;
    private final UserAccountMapper userAccountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long currentUserId, CreateOrderRequest request) {
        UserAccount buyer = requireUser(currentUserId);
        if (!isAdmin(buyer) && !Integer.valueOf(1).equals(buyer.getCanBuy())) {
            throw new BusinessException("当前账号无购买权限");
        }

        validateTradeModeFields(request);

        Map<Long, Integer> mergedItems = mergeOrderItems(request.getItems());
        if (mergedItems.isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        Long sellerId = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : mergedItems.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            TradeProduct product = tradeProductMapper.selectByIdForUpdate(productId);
            if (product == null) {
                throw new BusinessException("商品不存在: " + productId);
            }
            if (!"on_sale".equals(product.getPublishStatus())) {
                throw new BusinessException("商品当前不可下单: " + productId);
            }
            if (product.getStock() == null || product.getStock() < quantity) {
                throw new BusinessException("商品库存不足: " + productId);
            }

            if (sellerId == null) {
                sellerId = product.getSellerId();
            } else if (!sellerId.equals(product.getSellerId())) {
                throw new BusinessException("同一订单仅支持单卖家商品");
            }

            BigDecimal subtotal = product.getSellingPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setProductId(productId);
            item.setProductTitle(product.getTitle());
            item.setProductImageUrl(ProductImageMapper.selectCoverImageByProductId(productId));
            item.setUnitPrice(product.getSellingPrice());
            item.setQuantity(quantity);
            item.setSubtotalAmount(subtotal);
            orderItems.add(item);
        }

        if (request.getSellerId() != null && !request.getSellerId().equals(sellerId)) {
            throw new BusinessException("卖家与商品归属不一致");
        }

        BigDecimal freight = request.getFreightAmount() == null ? BigDecimal.ZERO : request.getFreightAmount();
        if (freight.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("运费金额不能为负数");
        }

        TradeOrder order = new TradeOrder();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(currentUserId);
        order.setSellerId(sellerId);
        order.setOrderStatus(STATUS_PENDING_PAYMENT);
        order.setTradeMode(request.getTradeMode());
        order.setTotalAmount(totalAmount);
        order.setFreightAmount(freight);
        order.setPayAmount(totalAmount.add(freight));
        order.setRemark(request.getRemark());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setPickupLocation(request.getPickupLocation());
        tradeOrderMapper.insert(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        for (Map.Entry<Long, Integer> entry : mergedItems.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            int affected = tradeProductMapper.decreaseStock(productId, quantity);
            if (affected == 0) {
                throw new BusinessException("商品库存更新失败，请重试");
            }

            TradeProduct afterUpdate = tradeProductMapper.selectByIdForUpdate(productId);
            if (afterUpdate != null && afterUpdate.getStock() != null && afterUpdate.getStock() <= 0) {
                tradeProductMapper.updatePublishStatus(productId, "reserved");
            }
        }

        insertStatusLog(order.getId(), null, STATUS_PENDING_PAYMENT, currentUserId, "创建订单");
        return order.getId();
    }

    @Override
    public OrderPageVO listOrders(Long currentUserId, String role, String orderStatus, long page, long pageSize) {
        UserAccount currentUser = requireUser(currentUserId);
        boolean admin = isAdmin(currentUser);

        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderStatus)) {
            wrapper.eq(TradeOrder::getOrderStatus, orderStatus);
        }

        if (!admin) {
            if ("buyer".equalsIgnoreCase(role)) {
                wrapper.eq(TradeOrder::getBuyerId, currentUserId);
            } else if ("seller".equalsIgnoreCase(role)) {
                wrapper.eq(TradeOrder::getSellerId, currentUserId);
            } else {
                wrapper.and(w -> w.eq(TradeOrder::getBuyerId, currentUserId).or().eq(TradeOrder::getSellerId, currentUserId));
            }
        } else if ("buyer".equalsIgnoreCase(role)) {
            wrapper.isNotNull(TradeOrder::getBuyerId);
        } else if ("seller".equalsIgnoreCase(role)) {
            wrapper.isNotNull(TradeOrder::getSellerId);
        }

        wrapper.orderByDesc(TradeOrder::getCreatedAt);
        Page<TradeOrder> orderPage = tradeOrderMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<Long> orderIds = orderPage.getRecords().stream().map(TradeOrder::getId).collect(Collectors.toList());
        Map<Long, List<OrderItemVO>> itemsByOrderId = buildItemsByOrderIds(orderIds);

        List<OrderListItemVO> list = orderPage.getRecords().stream().map(order -> {
            OrderListItemVO vo = new OrderListItemVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setBuyerId(order.getBuyerId());
            vo.setSellerId(order.getSellerId());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setTradeMode(order.getTradeMode());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setFreightAmount(order.getFreightAmount());
            vo.setPayAmount(order.getPayAmount());
            vo.setCancelReason(order.getCancelReason());
            vo.setPaidAt(order.getPaidAt());
            vo.setShippedAt(order.getShippedAt());
            vo.setDeliveredAt(order.getDeliveredAt());
            vo.setCompletedAt(order.getCompletedAt());
            vo.setCancelledAt(order.getCancelledAt());
            vo.setCreatedAt(order.getCreatedAt());
            vo.setItems(itemsByOrderId.getOrDefault(order.getId(), List.of()));
            return vo;
        }).collect(Collectors.toList());

        return new OrderPageVO(orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize(), list);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long currentUserId, Long orderId) {
        TradeOrder order = requireOrder(orderId);
        assertOrderReadable(currentUserId, order);

        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setTradeMode(order.getTradeMode());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setRemark(order.getRemark());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setPickupLocation(order.getPickupLocation());
        vo.setCancelReason(order.getCancelReason());
        vo.setPaidAt(order.getPaidAt());
        vo.setShippedAt(order.getShippedAt());
        vo.setDeliveredAt(order.getDeliveredAt());
        vo.setCompletedAt(order.getCompletedAt());
        vo.setCancelledAt(order.getCancelledAt());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setUpdatedAt(order.getUpdatedAt());
        vo.setItems(buildItemsByOrderId(order.getId()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long currentUserId, Long orderId, CancelOrderRequest request) {
        TradeOrder order = tradeOrderMapper.selectByIdForUpdate(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        UserAccount currentUser = requireUser(currentUserId);
        boolean admin = isAdmin(currentUser);
        if (!admin && !currentUserId.equals(order.getBuyerId()) && !currentUserId.equals(order.getSellerId())) {
            throw new BusinessException("无权限取消该订单");
        }

        if (!STATUS_PENDING_PAYMENT.equals(order.getOrderStatus()) && !STATUS_PAID_PENDING_SHIP.equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不允许取消");
        }

        String fromStatus = order.getOrderStatus();
        order.setOrderStatus(STATUS_CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason(request == null ? null : request.getCancelReason());
        tradeOrderMapper.updateById(order);

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            tradeProductMapper.increaseStock(item.getProductId(), item.getQuantity());
            tradeProductMapper.updatePublishStatus(item.getProductId(), "on_sale");
        }

        insertStatusLog(orderId, fromStatus, STATUS_CANCELLED, currentUserId, order.getCancelReason());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(Long currentUserId, Long orderId) {
        TradeOrder order = tradeOrderMapper.selectByIdForUpdate(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!currentUserId.equals(order.getBuyerId())) {
            throw new BusinessException("仅买家可确认收货");
        }
        if (!STATUS_SHIPPED.equals(order.getOrderStatus()) && !STATUS_DELIVERED.equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不允许确认收货");
        }

        String fromStatus = order.getOrderStatus();
        order.setOrderStatus(STATUS_COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        tradeOrderMapper.updateById(order);

        insertStatusLog(orderId, fromStatus, STATUS_COMPLETED, currentUserId, "买家确认收货");
    }

    @Override
    public List<OrderStatusLogVO> getOrderStatusLogs(Long currentUserId, Long orderId) {
        TradeOrder order = requireOrder(orderId);
        assertOrderReadable(currentUserId, order);
        return orderStatusLogMapper.selectByOrderId(orderId).stream().map(log -> {
            OrderStatusLogVO vo = new OrderStatusLogVO();
            vo.setId(log.getId());
            vo.setFromStatus(log.getFromStatus());
            vo.setToStatus(log.getToStatus());
            vo.setChangedBy(log.getChangedBy());
            vo.setChangeReason(log.getChangeReason());
            vo.setChangedAt(log.getChangedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createShipment(Long currentUserId, Long orderId, CreateShipmentRequest request) {
        TradeOrder order = tradeOrderMapper.selectByIdForUpdate(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        assertSellerWritable(currentUserId, order);
        if (!STATUS_PAID_PENDING_SHIP.equals(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单状态不允许创建发货记录");
        }

        OrderShipment exists = orderShipmentMapper.selectByOrderIdForUpdate(orderId);
        if (exists != null) {
            throw new BusinessException(400, "该订单已存在发货记录");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderShipment shipment = new OrderShipment();
        shipment.setOrderId(orderId);
        shipment.setShipmentType(order.getTradeMode());
        shipment.setShippedBy(currentUserId);
        shipment.setShippedAt(now);

        if (TRADE_MODE_SHIPPING.equals(order.getTradeMode())) {
            validateShippingShipmentRequest(request);
            shipment.setLogisticsCompany(request.getLogisticsCompany().trim());
            shipment.setTrackingNo(request.getTrackingNo().trim());
            shipment.setShipmentStatus(SHIPMENT_STATUS_IN_TRANSIT);
        } else if (TRADE_MODE_PICKUP.equals(order.getTradeMode())) {
            validatePickupShipmentRequest(request);
            shipment.setPickupCode(StringUtils.hasText(request.getPickupCode()) ? request.getPickupCode().trim() : generatePickupCode());
            shipment.setShipmentStatus(SHIPMENT_STATUS_TO_SHIP);
        } else {
            throw new BusinessException(400, "不支持的交易方式");
        }

        orderShipmentMapper.insert(shipment);

        String fromStatus = order.getOrderStatus();
        order.setOrderStatus(STATUS_SHIPPED);
        order.setShippedAt(now);
        tradeOrderMapper.updateById(order);
        insertStatusLog(orderId, fromStatus, STATUS_SHIPPED, currentUserId,
                TRADE_MODE_SHIPPING.equals(order.getTradeMode()) ? "卖家已发货" : "卖家已创建自提记录");

        insertShipmentTrace(
                shipment.getId(),
                now,
                TRADE_MODE_SHIPPING.equals(order.getTradeMode()) ? "已发货" : "待自提",
                TRADE_MODE_SHIPPING.equals(order.getTradeMode()) ? "卖家已发货，包裹已交由物流承运" : "卖家已备货完成，等待买家自提",
                TRADE_MODE_SHIPPING.equals(order.getTradeMode()) ? null : order.getPickupLocation()
        );
        return shipment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signShipment(Long currentUserId, Long orderId, Long shipmentId) {
        TradeOrder order = tradeOrderMapper.selectByIdForUpdate(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        assertBuyerReadable(currentUserId, order);

        OrderShipment shipment = requireShipmentForUpdate(orderId, shipmentId);
        if (!TRADE_MODE_SHIPPING.equals(shipment.getShipmentType())) {
            throw new BusinessException(400, "当前发货记录不支持签收操作");
        }
        if (SHIPMENT_STATUS_SIGNED.equals(shipment.getShipmentStatus())) {
            return;
        }
        if (!STATUS_SHIPPED.equals(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单状态不允许签收");
        }

        LocalDateTime now = LocalDateTime.now();
        shipment.setShipmentStatus(SHIPMENT_STATUS_SIGNED);
        shipment.setSignedAt(now);
        orderShipmentMapper.updateById(shipment);

        order.setOrderStatus(STATUS_DELIVERED);
        order.setDeliveredAt(now);
        tradeOrderMapper.updateById(order);
        insertStatusLog(orderId, STATUS_SHIPPED, STATUS_DELIVERED, currentUserId, "买家已签收");
        insertShipmentTrace(shipment.getId(), now, "已签收", "买家已完成签收", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyPickup(Long currentUserId, Long orderId, Long shipmentId, PickupVerifyRequest request) {
        TradeOrder order = tradeOrderMapper.selectByIdForUpdate(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        assertSellerWritable(currentUserId, order);

        OrderShipment shipment = requireShipmentForUpdate(orderId, shipmentId);
        if (!TRADE_MODE_PICKUP.equals(shipment.getShipmentType())) {
            throw new BusinessException(400, "当前发货记录不支持自提核销");
        }
        if (!StringUtils.hasText(shipment.getPickupCode())) {
            throw new BusinessException(400, "当前发货记录未生成自提码");
        }
        if (!shipment.getPickupCode().equals(request.getPickupCode().trim())) {
            throw new BusinessException(400, "自提码校验失败");
        }
        if (SHIPMENT_STATUS_SIGNED.equals(shipment.getShipmentStatus())) {
            return;
        }
        if (!STATUS_SHIPPED.equals(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单状态不允许自提核销");
        }

        LocalDateTime now = LocalDateTime.now();
        shipment.setShipmentStatus(SHIPMENT_STATUS_SIGNED);
        shipment.setSignedAt(now);
        shipment.setPickupVerifiedAt(now);
        orderShipmentMapper.updateById(shipment);

        order.setOrderStatus(STATUS_DELIVERED);
        order.setDeliveredAt(now);
        tradeOrderMapper.updateById(order);
        insertStatusLog(orderId, STATUS_SHIPPED, STATUS_DELIVERED, currentUserId, "卖家完成自提核销");
        insertShipmentTrace(shipment.getId(), now, "已自提", "卖家已完成自提核销", order.getPickupLocation());
    }

    @Override
    public OrderShipmentVO getShipmentDetail(Long currentUserId, Long orderId, Long shipmentId) {
        TradeOrder order = requireOrder(orderId);
        assertOrderReadable(currentUserId, order);
        return toOrderShipmentVO(requireShipment(orderId, shipmentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLogisticsTrace(Long currentUserId, Long orderId, Long shipmentId, CreateLogisticsTraceRequest request) {
        TradeOrder order = requireOrder(orderId);
        assertSellerWritable(currentUserId, order);
        OrderShipment shipment = requireShipment(orderId, shipmentId);
        if (FINAL_ORDER_STATUSES.contains(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单已结束，不能新增物流轨迹");
        }

        LogisticsTrace trace = new LogisticsTrace();
        trace.setShipmentId(shipmentId);
        trace.setTraceTime(request.getTraceTime() == null ? LocalDateTime.now() : request.getTraceTime());
        trace.setTraceStatus(request.getTraceStatus().trim());
        trace.setTraceDetail(request.getTraceDetail().trim());
        trace.setTraceLocation(StringUtils.hasText(request.getTraceLocation()) ? request.getTraceLocation().trim() : null);
        logisticsTraceMapper.insert(trace);
        return trace.getId();
    }

    @Override
    public List<LogisticsTraceVO> getLogisticsTraces(Long currentUserId, Long orderId, Long shipmentId) {
        TradeOrder order = requireOrder(orderId);
        assertOrderReadable(currentUserId, order);
        requireShipment(orderId, shipmentId);
        return logisticsTraceMapper.selectByShipmentId(shipmentId)
                .stream()
                .map(this::toLogisticsTraceVO)
                .collect(Collectors.toList());
    }

    private void validateTradeModeFields(CreateOrderRequest request) {
        if (TRADE_MODE_SHIPPING.equals(request.getTradeMode())) {
            if (!StringUtils.hasText(request.getReceiverName())
                    || !StringUtils.hasText(request.getReceiverPhone())
                    || !StringUtils.hasText(request.getReceiverAddress())) {
                throw new BusinessException("邮寄交易必须填写收货信息");
            }
        } else if (TRADE_MODE_PICKUP.equals(request.getTradeMode())) {
            if (!StringUtils.hasText(request.getPickupLocation())) {
                throw new BusinessException("自提交易必须填写自提地点");
            }
        } else {
            throw new BusinessException("不支持的交易方式");
        }
    }

    private Map<Long, Integer> mergeOrderItems(List<CreateOrderItemRequest> items) {
        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (CreateOrderItemRequest item : items) {
            merged.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }
        return merged;
    }

    private TradeOrder requireOrder(Long orderId) {
        TradeOrder order = tradeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private OrderShipment requireShipment(Long orderId, Long shipmentId) {
        OrderShipment shipment = orderShipmentMapper.selectByIdAndOrderId(shipmentId, orderId);
        if (shipment == null) {
            throw new BusinessException(404, "发货记录不存在");
        }
        return shipment;
    }

    private OrderShipment requireShipmentForUpdate(Long orderId, Long shipmentId) {
        OrderShipment shipment = orderShipmentMapper.selectByIdAndOrderIdForUpdate(shipmentId, orderId);
        if (shipment == null) {
            throw new BusinessException(404, "发货记录不存在");
        }
        return shipment;
    }

    private UserAccount requireUser(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private void assertOrderReadable(Long currentUserId, TradeOrder order) {
        UserAccount currentUser = requireUser(currentUserId);
        boolean admin = isAdmin(currentUser);
        if (!admin && !currentUserId.equals(order.getBuyerId()) && !currentUserId.equals(order.getSellerId())) {
            throw new BusinessException(403, "无权限访问该订单");
        }
    }

    private void assertSellerWritable(Long currentUserId, TradeOrder order) {
        UserAccount currentUser = requireUser(currentUserId);
        boolean admin = isAdmin(currentUser);
        if (!admin && !currentUserId.equals(order.getSellerId())) {
            throw new BusinessException(403, "仅订单卖家可执行该操作");
        }
    }

    private void assertBuyerReadable(Long currentUserId, TradeOrder order) {
        UserAccount currentUser = requireUser(currentUserId);
        boolean admin = isAdmin(currentUser);
        if (!admin && !currentUserId.equals(order.getBuyerId())) {
            throw new BusinessException(403, "仅订单买家可执行该操作");
        }
    }

    private boolean isAdmin(UserAccount user) {
        return user != null && Integer.valueOf(1).equals(user.getIsAdmin());
    }

    private List<OrderItemVO> buildItemsByOrderId(Long orderId) {
        return orderItemMapper.selectByOrderId(orderId).stream().map(item -> {
            OrderItemVO vo = new OrderItemVO();
            vo.setId(item.getId());
            vo.setProductId(item.getProductId());
            vo.setProductTitle(item.getProductTitle());
            vo.setProductImageUrl(item.getProductImageUrl());
            vo.setUnitPrice(item.getUnitPrice());
            vo.setQuantity(item.getQuantity());
            vo.setSubtotalAmount(item.getSubtotalAmount());
            return vo;
        }).collect(Collectors.toList());
    }

    private Map<Long, List<OrderItemVO>> buildItemsByOrderIds(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Map.of();
        }
        return orderItemMapper.selectByOrderIds(orderIds).stream().collect(Collectors.groupingBy(
                OrderItem::getOrderId,
                LinkedHashMap::new,
                Collectors.mapping(item -> {
                    OrderItemVO vo = new OrderItemVO();
                    vo.setId(item.getId());
                    vo.setProductId(item.getProductId());
                    vo.setProductTitle(item.getProductTitle());
                    vo.setProductImageUrl(item.getProductImageUrl());
                    vo.setUnitPrice(item.getUnitPrice());
                    vo.setQuantity(item.getQuantity());
                    vo.setSubtotalAmount(item.getSubtotalAmount());
                    return vo;
                }, Collectors.toList())
        ));
    }

    private void insertStatusLog(Long orderId, String fromStatus, String toStatus, Long changedBy, String reason) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setChangedBy(changedBy);
        log.setChangeReason(reason);
        orderStatusLogMapper.insert(log);
    }

    private void validateShippingShipmentRequest(CreateShipmentRequest request) {
        if (request == null || !StringUtils.hasText(request.getLogisticsCompany()) || !StringUtils.hasText(request.getTrackingNo())) {
            throw new BusinessException(400, "邮寄发货必须填写物流公司和物流单号");
        }
    }

    private void validatePickupShipmentRequest(CreateShipmentRequest request) {
        if (request != null && (StringUtils.hasText(request.getLogisticsCompany()) || StringUtils.hasText(request.getTrackingNo()))) {
            throw new BusinessException(400, "自提订单不应填写物流公司和物流单号");
        }
    }

    private void insertShipmentTrace(Long shipmentId,
                                     LocalDateTime traceTime,
                                     String traceStatus,
                                     String traceDetail,
                                     String traceLocation) {
        LogisticsTrace trace = new LogisticsTrace();
        trace.setShipmentId(shipmentId);
        trace.setTraceTime(traceTime);
        trace.setTraceStatus(traceStatus);
        trace.setTraceDetail(traceDetail);
        trace.setTraceLocation(traceLocation);
        logisticsTraceMapper.insert(trace);
    }

    private OrderShipmentVO toOrderShipmentVO(OrderShipment shipment) {
        OrderShipmentVO vo = new OrderShipmentVO();
        vo.setId(shipment.getId());
        vo.setOrderId(shipment.getOrderId());
        vo.setShipmentType(shipment.getShipmentType());
        vo.setLogisticsCompany(shipment.getLogisticsCompany());
        vo.setTrackingNo(shipment.getTrackingNo());
        vo.setShipmentStatus(shipment.getShipmentStatus());
        vo.setShippedBy(shipment.getShippedBy());
        vo.setShippedAt(shipment.getShippedAt());
        vo.setSignedAt(shipment.getSignedAt());
        vo.setPickupCode(shipment.getPickupCode());
        vo.setPickupVerifiedAt(shipment.getPickupVerifiedAt());
        vo.setCreatedAt(shipment.getCreatedAt());
        vo.setUpdatedAt(shipment.getUpdatedAt());
        return vo;
    }

    private LogisticsTraceVO toLogisticsTraceVO(LogisticsTrace trace) {
        LogisticsTraceVO vo = new LogisticsTraceVO();
        vo.setId(trace.getId());
        vo.setShipmentId(trace.getShipmentId());
        vo.setTraceTime(trace.getTraceTime());
        vo.setTraceStatus(trace.getTraceStatus());
        vo.setTraceDetail(trace.getTraceDetail());
        vo.setTraceLocation(trace.getTraceLocation());
        vo.setCreatedAt(trace.getCreatedAt());
        return vo;
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = 1000 + new Random().nextInt(9000);
        return "OD" + timePart + random;
    }

    private String generatePickupCode() {
        int random = 100000 + new Random().nextInt(900000);
        return String.valueOf(random);
    }
}
