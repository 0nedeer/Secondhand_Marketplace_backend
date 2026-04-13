package com.secondhand.marketplace.backend.modules.aftersale.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.aftersale.dto.AdminDecisionRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CancelAfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateAfterSaleRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.SellerResponseRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.UploadAfterSaleEvidenceRequest;
import com.secondhand.marketplace.backend.modules.aftersale.service.AfterSaleService;
import com.secondhand.marketplace.backend.modules.aftersale.vo.AfterSaleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/after-sales")
public class AfterSaleController {

    private final AfterSaleService afterSaleService;

    @PostMapping
    public CommonResult<Long> createAfterSale(@Valid @RequestBody CreateAfterSaleRequest request) {
        return CommonResult.success(afterSaleService.createAfterSale(requireLogin(), request));
    }

    @GetMapping
    public CommonResult<List<AfterSaleVO>> listAfterSales(@RequestParam(required = false) Long orderId,
                                                          @RequestParam(required = false) String status,
                                                          @RequestParam(required = false) String requestType) {
        return CommonResult.success(afterSaleService.listAfterSales(requireLogin(), orderId, status, requestType));
    }

    @GetMapping("/{afterSaleId}")
    public CommonResult<AfterSaleVO> getAfterSaleDetail(@PathVariable Long afterSaleId) {
        return CommonResult.success(afterSaleService.getAfterSaleDetail(requireLogin(), afterSaleId));
    }

    @PostMapping("/{afterSaleId}/evidences")
    public CommonResult<Long> uploadEvidence(@PathVariable Long afterSaleId,
                                             @Valid @RequestBody UploadAfterSaleEvidenceRequest request) {
        return CommonResult.success(afterSaleService.uploadEvidence(requireLogin(), afterSaleId, request));
    }

    @PostMapping("/{afterSaleId}/seller-response")
    public CommonResult<Void> sellerResponse(@PathVariable Long afterSaleId,
                                             @Valid @RequestBody SellerResponseRequest request) {
        afterSaleService.sellerResponse(requireLogin(), afterSaleId, request);
        return CommonResult.success();
    }

    @PostMapping("/{afterSaleId}/cancel")
    public CommonResult<Void> cancelAfterSale(@PathVariable Long afterSaleId,
                                              @RequestBody(required = false) CancelAfterSaleRequest request) {
        afterSaleService.cancelAfterSale(requireLogin(), afterSaleId, request == null ? new CancelAfterSaleRequest() : request);
        return CommonResult.success();
    }

    @PostMapping("/{afterSaleId}/admin-decision")
    public CommonResult<Void> adminDecision(@PathVariable Long afterSaleId,
                                            @Valid @RequestBody AdminDecisionRequest request) {
        afterSaleService.adminDecision(requireLogin(), afterSaleId, request);
        return CommonResult.success();
    }

    private Long requireLogin() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }
}
