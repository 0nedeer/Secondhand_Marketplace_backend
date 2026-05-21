package com.secondhand.marketplace.backend.modules.admin.service;

import com.secondhand.marketplace.backend.modules.admin.vo.DashboardOverviewVO;
import com.secondhand.marketplace.backend.modules.admin.vo.DashboardTrendsVO;

public interface DashboardService {
    DashboardOverviewVO getOverview(Long adminId);

    DashboardTrendsVO getTrends(Long adminId);
}
