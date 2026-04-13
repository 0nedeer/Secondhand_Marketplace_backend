package com.secondhand.marketplace.backend.modules.aftersale.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.aftersale.dto.CreateDisputeRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.DisputeActionRequest;
import com.secondhand.marketplace.backend.modules.aftersale.dto.ResolveDisputeRequest;
import com.secondhand.marketplace.backend.modules.aftersale.service.DisputeService;
import com.secondhand.marketplace.backend.modules.aftersale.vo.DisputeVO;
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
@RequestMapping("/api/disputes")
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    public CommonResult<Long> createDispute(@Valid @RequestBody CreateDisputeRequest request) {
        return CommonResult.success(disputeService.createDispute(requireLogin(), request));
    }

    @GetMapping
    public CommonResult<List<DisputeVO>> listDisputes(@RequestParam(required = false) Long orderId,
                                                      @RequestParam(required = false) Long afterSaleId,
                                                      @RequestParam(required = false) String status) {
        return CommonResult.success(disputeService.listDisputes(requireLogin(), orderId, afterSaleId, status));
    }

    @GetMapping("/{disputeId}")
    public CommonResult<DisputeVO> getDisputeDetail(@PathVariable Long disputeId) {
        return CommonResult.success(disputeService.getDisputeDetail(requireLogin(), disputeId));
    }

    @PostMapping("/{disputeId}/actions")
    public CommonResult<Long> addAction(@PathVariable Long disputeId, @Valid @RequestBody DisputeActionRequest request) {
        return CommonResult.success(disputeService.addAction(requireLogin(), disputeId, request));
    }

    @PostMapping("/{disputeId}/resolve")
    public CommonResult<Void> resolveDispute(@PathVariable Long disputeId, @Valid @RequestBody ResolveDisputeRequest request) {
        disputeService.resolveDispute(requireLogin(), disputeId, request);
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
