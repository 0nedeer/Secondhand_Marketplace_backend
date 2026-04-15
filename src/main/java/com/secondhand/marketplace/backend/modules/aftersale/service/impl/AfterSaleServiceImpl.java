package com.secondhand.marketplace.backend.modules.aftersale.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.aftersale.dto.AdminDecisionRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CancelAfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateAfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.SellerResponseRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.UploadAfterSaleEvidenceRequest;
import com.secondhand.marketplace.backend.modules.aftersale.entity.AfterSaleEvidence;
import com.secondhand.marketplace.backend.modules.aftersale.entity.AfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.AfterSaleEvidenceMapper;
import com.secondhand.marketplace.backend.modules.aftersale.mapper.AfterSaleRequestMapper;
import com.secondhand.marketplace.backend.modules.aftersale.service.AfterSaleService;
import com.secondhand.marketplace.backend.modules.aftersale.vo.AfterSaleEvidenceVO;
import com.secondhand.marketplace.backend.modules.aftersale.vo.AfterSaleVO;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderItem;
import com.secondhand.marketplace.backend.modules.trade.entity.TradeOrder;
import com.secondhand.marketplace.backend.modules.trade.mapper.OrderItemMapper;
import com.secondhand.marketplace.backend.modules.trade.mapper.TradeOrderMapper;
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
public class AfterSaleServiceImpl implements AfterSaleService {

    private static final Set<String> VALID_REQUEST_TYPES = Set.of("return_refund", "refund_only", "exchange", "complaint");
    private static final Set<String> VALID_EVIDENCE_TYPES = Set.of("image", "video", "text", "logistics_doc");
    private static final Set<String> VALID_ADMIN_DECISION = Set.of("approved", "rejected", "completed");
    private static final Set<String> FINAL_AFTER_SALE_STATUS = Set.of("rejected", "cancelled", "completed");
    private static final Set<String> ALLOWED_ORDER_STATUS = Set.of("shipped", "delivered", "completed", "refund_in_progress");

    private final AfterSaleRequestMapper afterSaleRequestMapper;
    private final AfterSaleEvidenceMapper afterSaleEvidenceMapper;
    private final TradeOrderMapper tradeOrderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserAccountMapper userAccountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAfterSale(Long currentUserId, CreateAfterSaleRequest request) {
        validateRequestType(request.getRequestType());
        validateAmount(request.getRequestedAmount(), "申请退款金额不能为负数");

        TradeOrder order = requireOrder(request.getOrderId());
        if (!currentUserId.equals(order.getBuyerId())) {
            throw new BusinessException(403, "仅订单买家可发起售后");
        }
        if (!ALLOWED_ORDER_STATUS.contains(order.getOrderStatus())) {
            throw new BusinessException(400, "当前订单状态不允许发起售后");
        }

        OrderItem item = requireOrderItem(request.getOrderItemId());
        if (!order.getId().equals(item.getOrderId())) {
            throw new BusinessException(400, "订单明细与订单不匹配");
        }
        if (request.getRequestedAmount() != null && request.getRequestedAmount().compareTo(item.getSubtotalAmount()) > 0) {
            throw new BusinessException(400, "申请退款金额不能超过订单明细金额");
        }
        if (afterSaleRequestMapper.selectActiveByOrderItemId(item.getId()) != null) {
            throw new BusinessException(400, "该订单明细已有进行中的售后申请");
        }

        AfterSaleRequest afterSale = new AfterSaleRequest();
        afterSale.setAfterSaleNo(generateAfterSaleNo());
        afterSale.setOrderId(order.getId());
        afterSale.setOrderItemId(item.getId());
        afterSale.setBuyerId(order.getBuyerId());
        afterSale.setSellerId(order.getSellerId());
        afterSale.setRequestType(request.getRequestType());
        afterSale.setRequestReason(request.getRequestReason().trim());
        afterSale.setDetailDesc(normalize(request.getDetailDesc()));
        afterSale.setRequestedAmount(request.getRequestedAmount());
        afterSale.setRequestStatus("pending_seller");
        afterSaleRequestMapper.insert(afterSale);
        return afterSale.getId();
    }

    @Override
    public List<AfterSaleVO> listAfterSales(Long currentUserId, Long orderId, String status, String requestType) {
        UserAccount currentUser = requireUser(currentUserId);
        boolean admin = isAdmin(currentUser);
        return afterSaleRequestMapper.selectByFilters(orderId, status, requestType, null, null)
                .stream()
                .filter(item -> admin || currentUserId.equals(item.getBuyerId()) || currentUserId.equals(item.getSellerId()))
                .map(this::toAfterSaleVO)
                .collect(Collectors.toList());
    }

    @Override
    public AfterSaleVO getAfterSaleDetail(Long currentUserId, Long afterSaleId) {
        AfterSaleRequest afterSale = requireAfterSale(afterSaleId);
        assertAfterSaleReadable(currentUserId, afterSale);
        return toAfterSaleVO(afterSale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadEvidence(Long currentUserId, Long afterSaleId, UploadAfterSaleEvidenceRequest request) {
        AfterSaleRequest afterSale = requireAfterSaleForUpdate(afterSaleId);
        assertAfterSaleReadable(currentUserId, afterSale);
        if (FINAL_AFTER_SALE_STATUS.contains(afterSale.getRequestStatus())) {
            throw new BusinessException(400, "当前售后状态不允许上传凭证");
        }
        if (!VALID_EVIDENCE_TYPES.contains(request.getEvidenceType())) {
            throw new BusinessException(400, "不支持的凭证类型");
        }
        if (!StringUtils.hasText(request.getContentUrl()) && !StringUtils.hasText(request.getContentText())) {
            throw new BusinessException(400, "凭证链接和文本说明至少填写一项");
        }

        AfterSaleEvidence evidence = new AfterSaleEvidence();
        evidence.setAfterSaleId(afterSaleId);
        evidence.setEvidenceType(request.getEvidenceType());
        evidence.setContentUrl(normalize(request.getContentUrl()));
        evidence.setContentText(normalize(request.getContentText()));
        evidence.setUploadedBy(currentUserId);
        afterSaleEvidenceMapper.insert(evidence);
        return evidence.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sellerResponse(Long currentUserId, Long afterSaleId, SellerResponseRequest request) {
        AfterSaleRequest afterSale = requireAfterSaleForUpdate(afterSaleId);
        if (!currentUserId.equals(afterSale.getSellerId())) {
            throw new BusinessException(403, "仅订单卖家可提交处理意见");
        }
        if (!"pending_seller".equals(afterSale.getRequestStatus())) {
            throw new BusinessException(400, "当前售后状态不允许卖家处理");
        }

        afterSale.setSellerResponse(request.getSellerResponse().trim());
        afterSale.setSellerRespondedAt(LocalDateTime.now());
        afterSale.setRequestStatus("pending_admin");
        afterSaleRequestMapper.updateById(afterSale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAfterSale(Long currentUserId, Long afterSaleId, CancelAfterSaleRequest request) {
        AfterSaleRequest afterSale = requireAfterSaleForUpdate(afterSaleId);
        if (!currentUserId.equals(afterSale.getBuyerId())) {
            throw new BusinessException(403, "仅订单买家可取消售后申请");
        }
        if (!"pending_seller".equals(afterSale.getRequestStatus()) && !"pending_admin".equals(afterSale.getRequestStatus())) {
            throw new BusinessException(400, "当前售后状态不允许取消");
        }

        afterSale.setRequestStatus("cancelled");
        afterSale.setAdminDecision(normalize(request == null ? null : request.getCancelReason()));
        afterSale.setClosedAt(LocalDateTime.now());
        afterSaleRequestMapper.updateById(afterSale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDecision(Long currentUserId, Long afterSaleId, AdminDecisionRequest request) {
        UserAccount user = requireUser(currentUserId);
        if (!isAdmin(user)) {
            throw new BusinessException(403, "仅管理员可裁决售后");
        }
        if (!VALID_ADMIN_DECISION.contains(request.getDecisionStatus())) {
            throw new BusinessException(400, "不支持的管理员裁决状态");
        }
        validateAmount(request.getFinalAmount(), "最终退款金额不能为负数");

        AfterSaleRequest afterSale = requireAfterSaleForUpdate(afterSaleId);
        if ("completed".equals(request.getDecisionStatus())) {
            if (!"pending_admin".equals(afterSale.getRequestStatus()) && !"approved".equals(afterSale.getRequestStatus())) {
                throw new BusinessException(400, "当前售后状态不允许完成");
            }
        } else if (!"pending_admin".equals(afterSale.getRequestStatus())) {
            throw new BusinessException(400, "当前售后状态不允许管理员裁决");
        }

        afterSale.setRequestStatus(request.getDecisionStatus());
        afterSale.setAdminId(currentUserId);
        afterSale.setAdminDecision(normalize(request.getAdminDecision()));
        afterSale.setFinalAmount(request.getFinalAmount());
        if ("rejected".equals(request.getDecisionStatus()) || "completed".equals(request.getDecisionStatus())) {
            afterSale.setClosedAt(LocalDateTime.now());
        }
        afterSaleRequestMapper.updateById(afterSale);
    }

    private TradeOrder requireOrder(Long orderId) {
        TradeOrder order = tradeOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private OrderItem requireOrderItem(Long orderItemId) {
        OrderItem item = orderItemMapper.selectById(orderItemId);
        if (item == null) {
            throw new BusinessException(404, "订单明细不存在");
        }
        return item;
    }

    private AfterSaleRequest requireAfterSale(Long afterSaleId) {
        AfterSaleRequest afterSale = afterSaleRequestMapper.selectById(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }
        return afterSale;
    }

    private AfterSaleRequest requireAfterSaleForUpdate(Long afterSaleId) {
        AfterSaleRequest afterSale = afterSaleRequestMapper.selectByIdForUpdate(afterSaleId);
        if (afterSale == null) {
            throw new BusinessException(404, "售后申请不存在");
        }
        return afterSale;
    }

    private UserAccount requireUser(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private void assertAfterSaleReadable(Long currentUserId, AfterSaleRequest afterSale) {
        UserAccount user = requireUser(currentUserId);
        if (!isAdmin(user) && !currentUserId.equals(afterSale.getBuyerId()) && !currentUserId.equals(afterSale.getSellerId())) {
            throw new BusinessException(403, "无权限访问该售后申请");
        }
    }

    private boolean isAdmin(UserAccount user) {
        return user != null && Integer.valueOf(1).equals(user.getIsAdmin());
    }

    private void validateRequestType(String requestType) {
        if (!VALID_REQUEST_TYPES.contains(requestType)) {
            throw new BusinessException(400, "不支持的售后类型");
        }
    }

    private void validateAmount(BigDecimal amount, String message) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(400, message);
        }
    }

    private AfterSaleVO toAfterSaleVO(AfterSaleRequest afterSale) {
        AfterSaleVO vo = new AfterSaleVO();
        vo.setId(afterSale.getId());
        vo.setAfterSaleNo(afterSale.getAfterSaleNo());
        vo.setOrderId(afterSale.getOrderId());
        vo.setOrderItemId(afterSale.getOrderItemId());
        vo.setBuyerId(afterSale.getBuyerId());
        vo.setSellerId(afterSale.getSellerId());
        vo.setRequestType(afterSale.getRequestType());
        vo.setRequestReason(afterSale.getRequestReason());
        vo.setDetailDesc(afterSale.getDetailDesc());
        vo.setRequestedAmount(afterSale.getRequestedAmount());
        vo.setFinalAmount(afterSale.getFinalAmount());
        vo.setRequestStatus(afterSale.getRequestStatus());
        vo.setSellerResponse(afterSale.getSellerResponse());
        vo.setSellerRespondedAt(afterSale.getSellerRespondedAt());
        vo.setAdminId(afterSale.getAdminId());
        vo.setAdminDecision(afterSale.getAdminDecision());
        vo.setClosedAt(afterSale.getClosedAt());
        vo.setCreatedAt(afterSale.getCreatedAt());
        vo.setUpdatedAt(afterSale.getUpdatedAt());
        vo.setEvidences(afterSaleEvidenceMapper.selectByAfterSaleId(afterSale.getId()).stream().map(this::toEvidenceVO).collect(Collectors.toList()));
        return vo;
    }

    private AfterSaleEvidenceVO toEvidenceVO(AfterSaleEvidence evidence) {
        AfterSaleEvidenceVO vo = new AfterSaleEvidenceVO();
        vo.setId(evidence.getId());
        vo.setAfterSaleId(evidence.getAfterSaleId());
        vo.setEvidenceType(evidence.getEvidenceType());
        vo.setContentUrl(evidence.getContentUrl());
        vo.setContentText(evidence.getContentText());
        vo.setUploadedBy(evidence.getUploadedBy());
        vo.setCreatedAt(evidence.getCreatedAt());
        return vo;
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String generateAfterSaleNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = 1000 + new Random().nextInt(9000);
        return "AS" + timePart + random;
    }
}
