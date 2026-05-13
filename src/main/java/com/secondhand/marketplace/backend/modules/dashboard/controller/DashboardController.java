package com.secondhand.marketplace.backend.modules.dashboard.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.dashboard.service.DashboardService;
import com.secondhand.marketplace.backend.modules.dashboard.vo.DashboardOverviewVO;
import com.secondhand.marketplace.backend.modules.dashboard.vo.DashboardTrendsVO;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserAccountMapper userAccountMapper;

    @GetMapping("/overview")
    public CommonResult<DashboardOverviewVO> getOverview() {
        // 验证管理员权限
        Long userId = UserContext.getCurrentUserId();
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null || user.getIsAdmin() != 1) {
            return CommonResult.error("无权限访问");
        }
        
        DashboardOverviewVO overview = dashboardService.getOverviewData();
        return CommonResult.success(overview);
    }

    @GetMapping("/trends")
    public CommonResult<DashboardTrendsVO> getTrends(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String type) {
        // 验证管理员权限
        Long userId = UserContext.getCurrentUserId();
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null || user.getIsAdmin() != 1) {
            return CommonResult.error("无权限访问");
        }
        
        DashboardTrendsVO trends = dashboardService.getTrendsData(period, type);
        return CommonResult.success(trends);
    }
}
