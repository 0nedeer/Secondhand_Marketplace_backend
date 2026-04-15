package com.secondhand.marketplace.backend.modules.trade.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.trade.dto.CreatePaymentRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.PaymentCallbackRequest;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderStatusLog;
import com.secondhand.marketplace.backend.modules.trade.entity.PaymentOrder;
import com.secondhand.marketplace.backend.modules.trade.entity.PaymentTransaction;
import com.secondhand.marketplace.backend.modules.trade.entity.TradeOrder;
import com.secondhand.marketplace.backend.modules.trade.mapper.OrderStatusLogMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.PaymentOrderMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.PaymentTransactionMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.TradeOrderMapper;
import com.secondhand.marketplace.backend.modules.trade.service.TradePaymentService;
import com.secondhand.marketplace.backend.modules.trade.vo.PaymentOrderVO;
import com.secondhand.marketplace.backend.modules.trade.vo.PaymentTransactionVO;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradePaymentServiceImpl implements TradePaymentService {

    private static final String ORDER_STATUS_PENDING_PAYMENT = "pending_payment";
    private static final String ORDER_STATUS_PAID_PENDING_SHIP = "paid_pending_ship";

    private static final String PAYMENT_STATUS_CREATED = "created";
    private static final String PAYMENT_STATUS_PAYING = "paying";
    private static final String PAYMENT_STATUS_PAID = "paid";
    private static final String PAYMENT_STATUS_FAILED = "failed";
    private static final String PAYMENT_STATUS_CLOSED = "closed";
    private static final String PAYMENT_STATUS_REFUNDED = "refunded";

    private static final String TX_TYPE_PAY = "pay";
    private static final String TX_TYPE_REFUND = "refund";
    private static final String TX_STATUS_PROCESSING = "processing";
    private static final String TX_STATUS_SUCCESS = "success";
    private static final String TX_STATUS_FAILED = "failed";

    private static final Set<String> VALID_PAYMENT_CHANNELS = Set.of("wechat", "alipay", "balance");
    private static final Set<String> VALID_CALLBACK_STATUS = Set.of(
            PAYMENT_STATUS_PAID, PAYMENT_STATUS_FAILED, PAYMENT_STATUS_CLOSED, PAYMENT_STATUS_REFUNDED
    );

    private final TradeOrderMapper tradeOrderMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final OrderStatusLogMapper orderStatusLogMapper;
    private final UserAccountMapper userAccountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPayment(Long currentUserId, Long orderId, CreatePaymentRequest request) {
        UserAccount currentUser = requireUser(currentUserId);
        if (!Integer.valueOf(1).equals(currentUser.getCanBuy())) {
            throw new BusinessException(403, "仅买家可发起支付");
        }
        validatePaymentChannel(request.getPaymentChannel());

        TradeOrder order = tradeOrderMapper.selectByIdForUpdate(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!currentUserId.equals(order.getBuyerId())) {
            throw new BusinessException(403, "仅订单买家可创建支付单");
        }
        if (!ORDER_STATUS_PENDING_PAYMENT.equals(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单状态不允许创建支付单");
        }

        PaymentOrder exists = paymentOrderMapper.selectByOrderIdForUpdate(orderId);
        if (exists != null) {
            if (PAYMENT_STATUS_PAID.equals(exists.getPaymentStatus())) {
                throw new BusinessException(400, "该订单已支付");
            }
            return exists.getId();
        }

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setOrderId(orderId);
        paymentOrder.setPaymentNo(generatePaymentNo());
        paymentOrder.setPaymentChannel(request.getPaymentChannel());
        paymentOrder.setPaymentStatus(PAYMENT_STATUS_CREATED);
        paymentOrder.setPayableAmount(order.getPayAmount());
        paymentOrderMapper.insert(paymentOrder);
        return paymentOrder.getId();
    }

    @Override
    public PaymentOrderVO getPaymentDetail(Long currentUserId, Long orderId, Long paymentId) {
        TradeOrder order = requireOrder(orderId);
        assertOrderReadable(currentUserId, order);

        PaymentOrder paymentOrder = requirePaymentOrder(paymentId);
        if (!orderId.equals(paymentOrder.getOrderId())) {
            throw new BusinessException(400, "支付单与订单不匹配");
        }
        return toPaymentOrderVO(paymentOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrderVO pay(Long currentUserId, Long paymentId) {
        PaymentOrder paymentOrder = paymentOrderMapper.selectByIdForUpdate(paymentId);
        if (paymentOrder == null) {
            throw new BusinessException(404, "支付单不存在");
        }

        TradeOrder order = tradeOrderMapper.selectByIdForUpdate(paymentOrder.getOrderId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!currentUserId.equals(order.getBuyerId())) {
            throw new BusinessException(403, "仅订单买家可发起支付");
        }
        if (!ORDER_STATUS_PENDING_PAYMENT.equals(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单状态不允许支付");
        }
        if (PAYMENT_STATUS_PAID.equals(paymentOrder.getPaymentStatus())) {
            return toPaymentOrderVO(paymentOrder);
        }
        if (!PAYMENT_STATUS_CREATED.equals(paymentOrder.getPaymentStatus())
                && !PAYMENT_STATUS_FAILED.equals(paymentOrder.getPaymentStatus())) {
            throw new BusinessException(400, "当前支付单状态不允许发起支付");
        }

        if ("balance".equals(paymentOrder.getPaymentChannel())) {
            LocalDateTime now = LocalDateTime.now();
            paymentOrder.setPaymentStatus(PAYMENT_STATUS_PAID);
            paymentOrder.setPaidAmount(paymentOrder.getPayableAmount());
            paymentOrder.setPaidAt(now);
            paymentOrder.setFailedReason(null);
            if (!StringUtils.hasText(paymentOrder.getChannelTradeNo())) {
                paymentOrder.setChannelTradeNo(generateChannelTradeNo("BAL"));
            }
            paymentOrderMapper.updateById(paymentOrder);

            String fromStatus = order.getOrderStatus();
            order.setOrderStatus(ORDER_STATUS_PAID_PENDING_SHIP);
            order.setPaidAt(now);
            tradeOrderMapper.updateById(order);
            insertOrderStatusLog(order.getId(), fromStatus, ORDER_STATUS_PAID_PENDING_SHIP, currentUserId, "支付成功");

            insertPaymentTransaction(
                    paymentOrder.getId(),
                    TX_TYPE_PAY,
                    TX_STATUS_SUCCESS,
                    paymentOrder.getPayableAmount(),
                    paymentOrder.getChannelTradeNo(),
                    "{\"message\":\"balance paid\"}"
            );
            return toPaymentOrderVO(paymentOrder);
        }

        paymentOrder.setPaymentStatus(PAYMENT_STATUS_PAYING);
        paymentOrder.setFailedReason(null);
        if (!StringUtils.hasText(paymentOrder.getChannelTradeNo())) {
            String prefix = "wechat".equals(paymentOrder.getPaymentChannel()) ? "WX" : "ALI";
            paymentOrder.setChannelTradeNo(generateChannelTradeNo(prefix));
        }
        paymentOrderMapper.updateById(paymentOrder);

        insertPaymentTransaction(
                paymentOrder.getId(),
                TX_TYPE_PAY,
                TX_STATUS_PROCESSING,
                paymentOrder.getPayableAmount(),
                paymentOrder.getChannelTradeNo(),
                "{\"message\":\"waiting callback\"}"
        );
        return toPaymentOrderVO(paymentOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(PaymentCallbackRequest request) {
        String callbackStatus = request.getPaymentStatus();
        if (!VALID_CALLBACK_STATUS.contains(callbackStatus)) {
            throw new BusinessException(400, "不支持的回调状态");
        }

        PaymentOrder paymentOrder = lockPaymentOrderByRequest(request);
        if (paymentOrder == null) {
            throw new BusinessException(404, "支付单不存在");
        }
        TradeOrder order = tradeOrderMapper.selectByIdForUpdate(paymentOrder.getOrderId());
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (callbackStatus.equals(paymentOrder.getPaymentStatus())) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (PAYMENT_STATUS_PAID.equals(callbackStatus)) {
            BigDecimal paidAmount = request.getPaidAmount() == null ? paymentOrder.getPayableAmount() : request.getPaidAmount();
            paymentOrder.setPaymentStatus(PAYMENT_STATUS_PAID);
            paymentOrder.setPaidAmount(paidAmount);
            paymentOrder.setPaidAt(now);
            paymentOrder.setFailedReason(null);
            if (StringUtils.hasText(request.getChannelTradeNo())) {
                paymentOrder.setChannelTradeNo(request.getChannelTradeNo());
            } else if (!StringUtils.hasText(paymentOrder.getChannelTradeNo())) {
                paymentOrder.setChannelTradeNo(generateChannelTradeNo("CH"));
            }
            paymentOrderMapper.updateById(paymentOrder);

            insertPaymentTransaction(
                    paymentOrder.getId(),
                    TX_TYPE_PAY,
                    TX_STATUS_SUCCESS,
                    paidAmount,
                    paymentOrder.getChannelTradeNo(),
                    StringUtils.hasText(request.getChannelResponse()) ? request.getChannelResponse() : "{\"message\":\"callback paid\"}"
            );

            if (ORDER_STATUS_PENDING_PAYMENT.equals(order.getOrderStatus())) {
                String fromStatus = order.getOrderStatus();
                order.setOrderStatus(ORDER_STATUS_PAID_PENDING_SHIP);
                order.setPaidAt(now);
                tradeOrderMapper.updateById(order);
                insertOrderStatusLog(order.getId(), fromStatus, ORDER_STATUS_PAID_PENDING_SHIP, null, "支付回调成功");
            }
            return;
        }

        if (PAYMENT_STATUS_FAILED.equals(callbackStatus)) {
            paymentOrder.setPaymentStatus(PAYMENT_STATUS_FAILED);
            paymentOrder.setFailedReason(StringUtils.hasText(request.getFailedReason()) ? request.getFailedReason() : "渠道返回支付失败");
            paymentOrderMapper.updateById(paymentOrder);
            insertPaymentTransaction(
                    paymentOrder.getId(),
                    TX_TYPE_PAY,
                    TX_STATUS_FAILED,
                    paymentOrder.getPayableAmount(),
                    request.getChannelTradeNo(),
                    StringUtils.hasText(request.getChannelResponse()) ? request.getChannelResponse() : "{\"message\":\"callback failed\"}"
            );
            return;
        }

        if (PAYMENT_STATUS_CLOSED.equals(callbackStatus)) {
            paymentOrder.setPaymentStatus(PAYMENT_STATUS_CLOSED);
            paymentOrder.setFailedReason(StringUtils.hasText(request.getFailedReason()) ? request.getFailedReason() : "支付已关闭");
            paymentOrderMapper.updateById(paymentOrder);
            return;
        }

        paymentOrder.setPaymentStatus(PAYMENT_STATUS_REFUNDED);
        paymentOrderMapper.updateById(paymentOrder);
        insertPaymentTransaction(
                paymentOrder.getId(),
                TX_TYPE_REFUND,
                TX_STATUS_SUCCESS,
                request.getPaidAmount() == null ? BigDecimal.ZERO : request.getPaidAmount(),
                request.getChannelTradeNo(),
                StringUtils.hasText(request.getChannelResponse()) ? request.getChannelResponse() : "{\"message\":\"callback refunded\"}"
        );
    }

    @Override
    public List<PaymentTransactionVO> getTransactions(Long currentUserId, Long paymentId) {
        PaymentOrder paymentOrder = requirePaymentOrder(paymentId);
        TradeOrder order = requireOrder(paymentOrder.getOrderId());
        assertOrderReadable(currentUserId, order);

        return paymentTransactionMapper.selectByPaymentOrderId(paymentId)
                .stream()
                .map(this::toPaymentTransactionVO)
                .collect(Collectors.toList());
    }

    private PaymentOrder lockPaymentOrderByRequest(PaymentCallbackRequest request) {
        if (request.getPaymentId() != null) {
            return paymentOrderMapper.selectByIdForUpdate(request.getPaymentId());
        }
        if (StringUtils.hasText(request.getPaymentNo())) {
            return paymentOrderMapper.selectByPaymentNoForUpdate(request.getPaymentNo());
        }
        throw new BusinessException(400, "paymentId 与 paymentNo 至少传一个");
    }

    private void validatePaymentChannel(String paymentChannel) {
        if (!VALID_PAYMENT_CHANNELS.contains(paymentChannel)) {
            throw new BusinessException(400, "不支持的支付渠道");
        }
    }

    private TradeOrder requireOrder(Long orderId) {
        TradeOrder order = tradeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private PaymentOrder requirePaymentOrder(Long paymentId) {
        PaymentOrder paymentOrder = paymentOrderMapper.selectById(paymentId);
        if (paymentOrder == null) {
            throw new BusinessException(404, "支付单不存在");
        }
        return paymentOrder;
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
        boolean isAdmin = currentUser != null && Integer.valueOf(1).equals(currentUser.getIsAdmin());
        if (!isAdmin && !currentUserId.equals(order.getBuyerId()) && !currentUserId.equals(order.getSellerId())) {
            throw new BusinessException(403, "无权限访问该订单支付信息");
        }
    }

    private void insertOrderStatusLog(Long orderId, String fromStatus, String toStatus, Long changedBy, String reason) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setChangedBy(changedBy);
        log.setChangeReason(reason);
        orderStatusLogMapper.insert(log);
    }

    private void insertPaymentTransaction(Long paymentOrderId,
                                          String type,
                                          String status,
                                          BigDecimal amount,
                                          String channelTradeNo,
                                          String channelResponse) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPaymentOrderId(paymentOrderId);
        transaction.setTransactionType(type);
        transaction.setTransactionStatus(status);
        transaction.setAmount(amount == null ? BigDecimal.ZERO : amount);
        transaction.setChannelTradeNo(channelTradeNo);
        transaction.setChannelResponse(channelResponse);
        paymentTransactionMapper.insert(transaction);
    }

    private PaymentOrderVO toPaymentOrderVO(PaymentOrder paymentOrder) {
        PaymentOrderVO vo = new PaymentOrderVO();
        vo.setId(paymentOrder.getId());
        vo.setOrderId(paymentOrder.getOrderId());
        vo.setPaymentNo(paymentOrder.getPaymentNo());
        vo.setPaymentChannel(paymentOrder.getPaymentChannel());
        vo.setPaymentStatus(paymentOrder.getPaymentStatus());
        vo.setPayableAmount(paymentOrder.getPayableAmount());
        vo.setPaidAmount(paymentOrder.getPaidAmount());
        vo.setChannelTradeNo(paymentOrder.getChannelTradeNo());
        vo.setPaidAt(paymentOrder.getPaidAt());
        vo.setFailedReason(paymentOrder.getFailedReason());
        vo.setCreatedAt(paymentOrder.getCreatedAt());
        vo.setUpdatedAt(paymentOrder.getUpdatedAt());
        return vo;
    }

    private PaymentTransactionVO toPaymentTransactionVO(PaymentTransaction transaction) {
        PaymentTransactionVO vo = new PaymentTransactionVO();
        vo.setId(transaction.getId());
        vo.setPaymentOrderId(transaction.getPaymentOrderId());
        vo.setTransactionType(transaction.getTransactionType());
        vo.setTransactionStatus(transaction.getTransactionStatus());
        vo.setAmount(transaction.getAmount());
        vo.setChannelTradeNo(transaction.getChannelTradeNo());
        vo.setChannelResponse(transaction.getChannelResponse());
        vo.setOccurredAt(transaction.getOccurredAt());
        return vo;
    }

    private String generatePaymentNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = 1000 + new Random().nextInt(9000);
        return "PO" + timePart + random;
    }

    private String generateChannelTradeNo(String prefix) {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = 10000 + new Random().nextInt(90000);
        return prefix + "_" + timePart + random;
    }
}

