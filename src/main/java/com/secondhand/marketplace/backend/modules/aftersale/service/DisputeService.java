package com.secondhand.marketplace.backend.modules.aftersale.service;

import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateDisputeRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.DisputeActionRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.ResolveDisputeRequest;
import com.secondhand.marketplace.backend.modules.aftersale.vo.DisputeVO;

import java.util.List;

public interface DisputeService {

    Long createDispute(Long currentUserId, CreateDisputeRequest request);

    List<DisputeVO> listDisputes(Long currentUserId, Long orderId, Long afterSaleId, String status);

    DisputeVO getDisputeDetail(Long currentUserId, Long disputeId);

    Long addAction(Long currentUserId, Long disputeId, DisputeActionRequest request);

    void resolveDispute(Long currentUserId, Long disputeId, ResolveDisputeRequest request);
}
