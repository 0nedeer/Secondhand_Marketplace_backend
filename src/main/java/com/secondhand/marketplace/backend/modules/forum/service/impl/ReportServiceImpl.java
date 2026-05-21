package com.secondhand.marketplace.backend.modules.forum.service.impl;

import com.secondhand.marketplace.backend.modules.forum.convert.ReportConverter;
import com.secondhand.marketplace.backend.modules.forum.dto.ReportCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.*;
import com.secondhand.marketplace.backend.modules.forum.mapper.*;
import com.secondhand.marketplace.backend.modules.forum.service.ReportService;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.forum.vo.ReportVO;
import com.secondhand.marketplace.backend.modules.forum.vo.UserInfoVO;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import com.secondhand.marketplace.backend.modules.user.vo.UserPermissionsVO;
import com.secondhand.marketplace.backend.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ReportServiceImpl implements ReportService {
    
    private final ForumReportMapper reportMapper;
    private final ForumPostMapper postMapper;
    private final ForumCommentMapper commentMapper;
    private final UserService userService;
    private final ReportConverter reportConverter;
    
    @Override
    public Long createReport(Long userId, ReportCreateDTO dto) {
        // 校验目标是否存在
        if ("post".equals(dto.getTargetType())) {
            ForumPost post = postMapper.selectById(dto.getTargetId());
            if (post == null || post.getIsDeleted() == 1) {
                throw new RuntimeException("举报的帖子不存在");
            }
        } else if ("comment".equals(dto.getTargetType())) {
            ForumComment comment = commentMapper.selectById(dto.getTargetId());
            if (comment == null || comment.getIsDeleted() == 1) {
                throw new RuntimeException("举报的评论不存在");
            }
        } else {
            throw new RuntimeException("不支持的举报目标类型");
        }
        
        // DTO转Entity
        ForumReport report = reportConverter.toEntity(dto);
        report.setReporterId(userId);
        report.setCreatedAt(LocalDateTime.now());
        
        // 保存举报
        reportMapper.insert(report);
        
        log.info("用户 {} 提交举报成功，举报ID：{}", userId, report.getId());
        return report.getId();
    }
    
    @Override
    public void handleReport(Long adminId, Long reportId, String status, String result) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限处理举报");
        }
        
        // 校验举报是否存在
        ForumReport report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new RuntimeException("举报不存在");
        }
        
        // 更新举报状态
        report.setReportStatus(status);
        report.setHandledBy(adminId);
        report.setHandleResult(result);
        report.setHandledAt(LocalDateTime.now());
        reportMapper.updateById(report);
        
        log.info("管理员 {} 处理举报成功，举报ID：{}", adminId, reportId);
    }
    
    @Override
    public ReportVO getReportDetail(Long reportId) {
        ForumReport report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new RuntimeException("举报不存在");
        }
        
        // 转换基本信息
        ReportVO vo = reportConverter.toVo(report);
        
        // 填充目标信息
        if ("post".equals(report.getTargetType())) {
            ForumPost post = postMapper.selectById(report.getTargetId());
            if (post != null) {
                vo.setTargetTitle(post.getTitle());
            }
        } else if ("comment".equals(report.getTargetType())) {
            ForumComment comment = commentMapper.selectById(report.getTargetId());
            if (comment != null) {
                vo.setTargetTitle(comment.getContent());
            }
        }
        
        // 填充举报人信息
        UserVO reporter = userService.getUserInfo(report.getReporterId());
        if (reporter != null) {
            UserInfoVO reporterInfo = new UserInfoVO();
            reporterInfo.setId(reporter.getId());
            reporterInfo.setUsername(reporter.getUsername());
            reporterInfo.setAvatar(reporter.getAvatar());
            vo.setReporterInfo(reporterInfo);
        }
        
        // 填充处理人信息
        if (report.getHandledBy() != null) {
            UserVO handler = userService.getUserInfo(report.getHandledBy());
            if (handler != null) {
                UserInfoVO handlerInfo = new UserInfoVO();
                handlerInfo.setId(handler.getId());
                handlerInfo.setUsername(handler.getUsername());
                handlerInfo.setAvatar(handler.getAvatar());
                vo.setHandlerInfo(handlerInfo);
            }
        }
        
        return vo;
    }
    
    @Override
    public PageResult<ReportVO> listReports(Long adminId, String status, Integer pageNum, Integer pageSize) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限查看举报列表");
        }
        
        // 获取所有举报并过滤
        List<ForumReport> allReports = reportMapper.selectList();
        List<ForumReport> filteredReports = allReports.stream()
                .filter(report -> status == null || status.isEmpty() || report.getReportStatus().equals(status))
                .collect(java.util.stream.Collectors.toList());
        
        // 分页处理
        int offset = (pageNum - 1) * pageSize;
        int endIndex = Math.min(offset + pageSize, filteredReports.size());
        List<ForumReport> reports = offset >= filteredReports.size() ? new java.util.ArrayList<>() : filteredReports.subList(offset, endIndex);
        long total = filteredReports.size();
        
        // 转换为VO
        List<ReportVO> voList = new ArrayList<>();
        for (ForumReport report : reports) {
            ReportVO vo = reportConverter.toVo(report);
            
            // 填充目标信息
            if ("post".equals(report.getTargetType())) {
                ForumPost post = postMapper.selectById(report.getTargetId());
                if (post != null) {
                    vo.setTargetTitle(post.getTitle());
                }
            } else if ("comment".equals(report.getTargetType())) {
                ForumComment comment = commentMapper.selectById(report.getTargetId());
                if (comment != null) {
                    vo.setTargetTitle(comment.getContent());
                }
            }
            
            // 填充举报人信息
            UserVO reporter = userService.getUserInfo(report.getReporterId());
            if (reporter != null) {
                UserInfoVO reporterInfo = new UserInfoVO();
                reporterInfo.setId(reporter.getId());
                reporterInfo.setUsername(reporter.getUsername());
                reporterInfo.setAvatar(reporter.getAvatar());
                vo.setReporterInfo(reporterInfo);
            }
            
            voList.add(vo);
        }
        
        return new PageResult<>(total, pageNum, pageSize, voList);
    }
    
    @Override
    public PageResult<ReportVO> listUserReports(Long userId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<ForumReport> reports = reportMapper.selectByReporterId(userId, offset, pageSize);
        
        // 获取所有用户举报以计算总数
        List<ForumReport> allUserReports = reportMapper.selectList();
        long total = allUserReports.stream()
                .filter(report -> report.getReporterId().equals(userId))
                .count();
        
        // 转换为VO
        List<ReportVO> voList = new ArrayList<>();
        for (ForumReport report : reports) {
            ReportVO vo = reportConverter.toVo(report);
            
            // 填充目标信息
            if ("post".equals(report.getTargetType())) {
                ForumPost post = postMapper.selectById(report.getTargetId());
                if (post != null) {
                    vo.setTargetTitle(post.getTitle());
                }
            } else if ("comment".equals(report.getTargetType())) {
                ForumComment comment = commentMapper.selectById(report.getTargetId());
                if (comment != null) {
                    vo.setTargetTitle(comment.getContent());
                }
            }
            
            voList.add(vo);
        }
        
        return new PageResult<>(total, pageNum, pageSize, voList);
    }
}