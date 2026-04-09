package com.secondhand.marketplace.backend.modules.forum.service;

import com.secondhand.marketplace.backend.modules.forum.dto.CommentCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CommentUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.vo.CommentVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import org.springframework.transaction.annotation.Transactional;

public interface CommentService {
    
    /**
     * 创建评论
     * @param userId 当前登录用户ID
     * @param dto 评论信息
     * @return 评论ID
     */
    @Transactional(rollbackFor = Exception.class)
    Long createComment(Long userId, CommentCreateDTO dto);
    
    /**
     * 编辑评论
     * @param userId 当前登录用户ID
     * @param dto 更新信息
     */
    @Transactional(rollbackFor = Exception.class)
    void updateComment(Long userId, CommentUpdateDTO dto);
    
    /**
     * 删除评论（软删除）
     * @param userId 当前登录用户ID
     * @param commentId 评论ID
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteComment(Long userId, Long commentId);
    
    /**
     * 获取评论详情
     * @param userId 当前登录用户ID（可为null）
     * @param commentId 评论ID
     * @return 评论详情VO
     */
    CommentVO getCommentDetail(Long userId, Long commentId);
    
    /**
     * 分页查询帖子的顶级评论
     * @param userId 当前登录用户ID（可为null）
     * @param postId 帖子ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<CommentVO> listComments(Long userId, Long postId, Integer pageNum, Integer pageSize);
    
    /**
     * 查询评论的回复列表
     * @param userId 当前登录用户ID（可为null）
     * @param parentId 父评论ID
     * @return 回复列表
     */
    PageResult<CommentVO> listReplies(Long userId, Long parentId, Integer pageNum, Integer pageSize);
    
    /**
     * 点赞/取消点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 当前点赞数
     */
    @Transactional(rollbackFor = Exception.class)
    Integer likeComment(Long userId, Long commentId);
    
    /**
     * 审核评论（管理员）
     * @param adminId 管理员ID
     * @param commentId 评论ID
     * @param approved 是否通过（true-通过，false-驳回）
     * @param rejectReason 驳回原因
     */
    @Transactional(rollbackFor = Exception.class)
    void auditComment(Long adminId, Long commentId, Boolean approved, String rejectReason);
}