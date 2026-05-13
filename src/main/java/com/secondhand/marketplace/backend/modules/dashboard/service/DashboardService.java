package com.secondhand.marketplace.backend.modules.dashboard.service;

import com.secondhand.marketplace.backend.modules.dashboard.vo.DashboardOverviewVO;
import com.secondhand.marketplace.backend.modules.dashboard.vo.DashboardTrendsVO;

public interface DashboardService {
    DashboardOverviewVO getOverviewData();
    DashboardTrendsVO getTrendsData(String period, String type);
}
