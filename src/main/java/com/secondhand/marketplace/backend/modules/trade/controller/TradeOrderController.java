package com.secondhand.marketplace.backend.modules.trade.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.trade.dto.CancelOrderRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateLogisticsTraceRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateOrderRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateShipmentRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.PickupVerifyRequest;
import com.secondhand.marketplace.backend.modules.trade.vo.LogisticsTraceVO;
import com.secondhand.marketplace.backend.modules.trade.service.TradeOrderService;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderDetailVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderPageVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderShipmentVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderStatusLogVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class TradeOrderController {

    private final TradeOrderService tradeOrderService;

    @PostMapping
    public CommonResult<Long> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Long currentUserId = requireLogin();
        return CommonResult.success(tradeOrderService.createOrder(currentUserId, request));
    }

    @GetMapping
    public CommonResult<OrderPageVO> listOrders(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(1) long page,
            @RequestParam(defaultValue = "20") @Min(1) long pageSize) {
        Long currentUserId = requireLogin();
        return CommonResult.success(tradeOrderService.listOrders(currentUserId, role, status, page, pageSize));
    }

    @GetMapping("/{orderId}")
    public CommonResult<OrderDetailVO> getOrderDetail(@PathVariable Long orderId) {
        Long currentUserId = requireLogin();
        return CommonResult.success(tradeOrderService.getOrderDetail(currentUserId, orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public CommonResult<Void> cancelOrder(@PathVariable Long orderId, @RequestBody(required = false) CancelOrderRequest request) {
        Long currentUserId = requireLogin();
        tradeOrderService.cancelOrder(currentUserId, orderId, request);
        return CommonResult.success();
    }

    @PostMapping("/{orderId}/confirm-receipt")
    public CommonResult<Void> confirmReceipt(@PathVariable Long orderId) {
        Long currentUserId = requireLogin();
        tradeOrderService.confirmReceipt(currentUserId, orderId);
        return CommonResult.success();
    }

    @GetMapping("/{orderId}/status-logs")
    public CommonResult<List<OrderStatusLogVO>> getStatusLogs(@PathVariable Long orderId) {
        Long currentUserId = requireLogin();
        return CommonResult.success(tradeOrderService.getOrderStatusLogs(currentUserId, orderId));
    }

    @PostMapping("/{orderId}/shipments")
    public CommonResult<Long> createShipment(@PathVariable Long orderId, @Valid @RequestBody(required = false) CreateShipmentRequest request) {
        Long currentUserId = requireLogin();
        return CommonResult.success(tradeOrderService.createShipment(currentUserId, orderId, request == null ? new CreateShipmentRequest() : request));
    }

    @PostMapping("/{orderId}/shipments/{shipmentId}/sign")
    public CommonResult<Void> signShipment(@PathVariable Long orderId, @PathVariable Long shipmentId) {
        Long currentUserId = requireLogin();
        tradeOrderService.signShipment(currentUserId, orderId, shipmentId);
        return CommonResult.success();
    }

    @PostMapping("/{orderId}/shipments/{shipmentId}/pickup-verify")
    public CommonResult<Void> verifyPickup(@PathVariable Long orderId,
                                           @PathVariable Long shipmentId,
                                           @Valid @RequestBody PickupVerifyRequest request) {
        Long currentUserId = requireLogin();
        tradeOrderService.verifyPickup(currentUserId, orderId, shipmentId, request);
        return CommonResult.success();
    }

    @GetMapping("/{orderId}/shipments/{shipmentId}")
    public CommonResult<OrderShipmentVO> getShipmentDetail(@PathVariable Long orderId, @PathVariable Long shipmentId) {
        Long currentUserId = requireLogin();
        return CommonResult.success(tradeOrderService.getShipmentDetail(currentUserId, orderId, shipmentId));
    }

    @PostMapping("/{orderId}/shipments/{shipmentId}/traces")
    public CommonResult<Long> createLogisticsTrace(@PathVariable Long orderId,
                                                   @PathVariable Long shipmentId,
                                                   @Valid @RequestBody CreateLogisticsTraceRequest request) {
        Long currentUserId = requireLogin();
        return CommonResult.success(tradeOrderService.createLogisticsTrace(currentUserId, orderId, shipmentId, request));
    }

    @GetMapping("/{orderId}/shipments/{shipmentId}/traces")
    public CommonResult<List<LogisticsTraceVO>> getLogisticsTraces(@PathVariable Long orderId, @PathVariable Long shipmentId) {
        Long currentUserId = requireLogin();
        return CommonResult.success(tradeOrderService.getLogisticsTraces(currentUserId, orderId, shipmentId));
    }

    private Long requireLogin() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new com.secondhand.marketplace.backend.common.exception.BusinessException(401, "请先登录");
        }
        return userId;
    }
}


