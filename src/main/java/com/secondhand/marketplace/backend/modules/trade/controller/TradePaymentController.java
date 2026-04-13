package com.secondhand.marketplace.backend.modules.trade.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.trade.dto.CreatePaymentRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.PaymentCallbackRequest;
import com.secondhand.marketplace.backend.modules.trade.service.TradePaymentService;
import com.secondhand.marketplace.backend.modules.trade.vo.PaymentOrderVO;
import com.secondhand.marketplace.backend.modules.trade.vo.PaymentTransactionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TradePaymentController {

    private final TradePaymentService tradePaymentService;

    @PostMapping("/orders/{orderId}/payments")
    public CommonResult<Long> createPayment(@PathVariable Long orderId,
                                            @Valid @RequestBody CreatePaymentRequest request) {
        return CommonResult.success(tradePaymentService.createPayment(requireLogin(), orderId, request));
    }

    @GetMapping("/orders/{orderId}/payments/{paymentId}")
    public CommonResult<PaymentOrderVO> getPaymentDetail(@PathVariable Long orderId,
                                                          @PathVariable Long paymentId) {
        return CommonResult.success(tradePaymentService.getPaymentDetail(requireLogin(), orderId, paymentId));
    }

    @PostMapping("/payments/{paymentId}/pay")
    public CommonResult<PaymentOrderVO> pay(@PathVariable Long paymentId) {
        return CommonResult.success(tradePaymentService.pay(requireLogin(), paymentId));
    }

    @PostMapping("/payments/callback")
    public CommonResult<Void> callback(@Valid @RequestBody PaymentCallbackRequest request) {
        tradePaymentService.handlePaymentCallback(request);
        return CommonResult.success();
    }

    @GetMapping("/payments/{paymentId}/transactions")
    public CommonResult<List<PaymentTransactionVO>> getTransactions(@PathVariable Long paymentId) {
        return CommonResult.success(tradePaymentService.getTransactions(requireLogin(), paymentId));
    }

    private Long requireLogin() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}

