package com.secondhand.marketplace.backend.modules.forum.service;

import com.secondhand.marketplace.backend.modules.forum.dto.ReportCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.forum.vo.ReportVO;
import org.springframework.transaction.annotation.Transactional;

public interface ReportService {
    
    /**
     * 创建举报
     * @param userId 举报用户ID
     * @param dto 举报信息
     * @return 举报ID
     */
    @Transactional(rollbackFor = Exception.class)
    Long createReport(Long userId, ReportCreateDTO dto);
    
    /**
     * 处理举报（管理员）
     * @param adminId 管理员ID
     * @param reportId 举报ID
     * @param status 处理状态
     * @param result 处理结果
     */
    @Transactional(rollbackFor = Exception.class)
    void handleReport(Long adminId, Long reportId, String status, String result);
    
    /**
     * 获取举报详情
     * @param reportId 举报ID
     * @return 举报详情VO
     */
    ReportVO getReportDetail(Long reportId);
    
    /**
     * 分页查询举报列表
     * @param adminId 管理员ID
     * @param status 状态筛选
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<ReportVO> listReports(Long adminId, String status, Integer pageNum, Integer pageSize);
    
    /**
     * 查询用户的举报记录
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<ReportVO> listUserReports(Long userId, Integer pageNum, Integer pageSize);
}