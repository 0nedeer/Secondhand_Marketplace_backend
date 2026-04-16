package com.secondhand.marketplace.backend.modules.aftersale.service;

import com.secondhand.marketplace.backend.modules.aftersale.dto.AdminDecisionRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CancelAfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateAfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.SellerResponseRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.UploadAfterSaleEvidenceRequest;
import com.secondhand.marketplace.backend.modules.aftersale.vo.AfterSaleVO;

import java.util.List;

public interface AfterSaleService {

    Long createAfterSale(Long currentUserId, CreateAfterSaleRequest request);

    List<AfterSaleVO> listAfterSales(Long currentUserId, Long orderId, String status, String requestType);

    AfterSaleVO getAfterSaleDetail(Long currentUserId, Long afterSaleId);

    Long uploadEvidence(Long currentUserId, Long afterSaleId, UploadAfterSaleEvidenceRequest request);

    void sellerResponse(Long currentUserId, Long afterSaleId, SellerResponseRequest request);

    void cancelAfterSale(Long currentUserId, Long afterSaleId, CancelAfterSaleRequest request);

    void adminDecision(Long currentUserId, Long afterSaleId, AdminDecisionRequest request);
}
