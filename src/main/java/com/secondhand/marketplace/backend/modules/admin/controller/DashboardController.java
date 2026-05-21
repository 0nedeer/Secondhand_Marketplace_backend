package com.secondhand.marketplace.backend.modules.admin.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.admin.service.DashboardService;
import com.secondhand.marketplace.backend.modules.admin.vo.DashboardOverviewVO;
import com.secondhand.marketplace.backend.modules.admin.vo.DashboardTrendsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public CommonResult<DashboardOverviewVO> overview() {
        return CommonResult.success(dashboardService.getOverview(UserContext.getCurrentUserId()));
    }

    @GetMapping("/trends")
    public CommonResult<DashboardTrendsVO> trends() {
        return CommonResult.success(dashboardService.getTrends(UserContext.getCurrentUserId()));
    }
}
