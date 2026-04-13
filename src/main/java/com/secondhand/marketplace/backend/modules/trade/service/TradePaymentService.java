package com.secondhand.marketplace.backend.modules.trade.service;

import com.secondhand.marketplace.backend.modules.trade.dto.CreatePaymentRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.PaymentCallbackRequest;
import com.secondhand.marketplace.backend.modules.trade.vo.PaymentOrderVO;
import com.secondhand.marketplace.backend.modules.trade.vo.PaymentTransactionVO;

import java.util.List;

public interface TradePaymentService {

    Long createPayment(Long currentUserId, Long orderId, CreatePaymentRequest request);

    PaymentOrderVO getPaymentDetail(Long currentUserId, Long orderId, Long paymentId);

    PaymentOrderVO pay(Long currentUserId, Long paymentId);

    void handlePaymentCallback(PaymentCallbackRequest request);

    List<PaymentTransactionVO> getTransactions(Long currentUserId, Long paymentId);
}

