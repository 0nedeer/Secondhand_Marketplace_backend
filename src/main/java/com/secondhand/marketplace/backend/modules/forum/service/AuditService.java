package com.secondhand.marketplace.backend.modules.forum.service;

import com.secondhand.marketplace.backend.modules.forum.dto.AuditReviewDTO;
import com.secondhand.marketplace.backend.modules.forum.vo.AuditLogVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.forum.vo.PostListVO;
import org.springframework.transaction.annotation.Transactional;

public interface AuditService {
    
    /**
     * 审核帖子
     * @param adminId 管理员ID
     * @param dto 审核信息
     */
    @Transactional(rollbackFor = Exception.class)
    void auditPost(Long adminId, AuditReviewDTO dto);
    
    /**
     * 审核评论
     * @param adminId 管理员ID
     * @param dto 审核信息
     */
    @Transactional(rollbackFor = Exception.class)
    void auditComment(Long adminId, AuditReviewDTO dto);
    
    /**
     * 获取待审核帖子列表
     * @param adminId 管理员ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<PostListVO> getPendingPosts(Long adminId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取审核日志列表
     * @param adminId 管理员ID
     * @param targetType 目标类型
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<AuditLogVO> getAuditLogs(Long adminId, String targetType, Integer pageNum, Integer pageSize);
}