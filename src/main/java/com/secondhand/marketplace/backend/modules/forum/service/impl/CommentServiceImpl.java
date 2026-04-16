package com.secondhand.marketplace.backend.modules.forum.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.forum.convert.CommentConverter;
import com.secondhand.marketplace.backend.modules.forum.dto.CommentCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CommentUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.*;
import com.secondhand.marketplace.backend.modules.forum.mapper.*;
import com.secondhand.marketplace.backend.modules.forum.service.CommentService;
import com.secondhand.marketplace.backend.modules.forum.vo.CommentVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.forum.vo.UserInfoVO;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import com.secondhand.marketplace.backend.modules.user.vo.UserVO;
import com.secondhand.marketplace.backend.modules.user.vo.UserPermissionsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CommentServiceImpl implements CommentService {
    
    private final ForumCommentMapper commentMapper;
    private final ForumReactionMapper reactionMapper;
    private final ForumPostMapper postMapper;
    private final CommentConverter commentConverter;
    private final UserService userService;
    
    @Override
    public Long createComment(Long userId, CommentCreateDTO dto) {
        // 1. 校验用户是否被禁用
        String userStatus = userService.getUserStatus(userId);
        if ("banned".equals(userStatus)) {
            throw new RuntimeException("您已被禁用，无法发表评论");
        }
        
        // 2. 校验帖子是否存在
        ForumPost post = postMapper.selectById(dto.getPostId());
        if (post == null || post.getIsDeleted() == 1) {
            throw new RuntimeException("帖子不存在");
        }
        
        // 3. 如果是回复，校验父评论是否存在
        if (dto.getParentCommentId() != null && dto.getParentCommentId() > 0) {
            ForumComment parentComment = commentMapper.selectById(dto.getParentCommentId());
            if (parentComment == null || parentComment.getIsDeleted() == 1) {
                throw new RuntimeException("回复的评论不存在");
            }
        }
        
        // 4. DTO转Entity
        ForumComment comment = commentConverter.toEntity(dto);
        comment.setCommenterId(userId);
        
        // 5. 设置父评论ID默认值（0表示顶级评论）
        if (comment.getParentCommentId() == null) {
            comment.setParentCommentId(0L);
        }
        
        // 6. 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        
        // 7. 保存评论
        commentMapper.insert(comment);
        
        // 6. 更新帖子评论数
        post.setCommentCount(post.getCommentCount() + 1);
        post.setLastCommentedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        // 7. 如果是回复，更新父评论的回复数
        if (dto.getParentCommentId() != null && dto.getParentCommentId() > 0) {
            ForumComment parentComment = commentMapper.selectById(dto.getParentCommentId());
            if (parentComment != null) {
                parentComment.setReplyCount(parentComment.getReplyCount() + 1);
                commentMapper.updateById(parentComment);
            }
        }
        
        log.info("用户 {} 发表评论成功，评论ID：{}", userId, comment.getId());
        return comment.getId();
    }
    
    @Override
    public void updateComment(Long userId, CommentUpdateDTO dto) {
        // 1. 查询原评论
        ForumComment comment = commentMapper.selectById(dto.getId());
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 2. 权限校验：只有作者或管理员可以编辑
        if (!comment.getCommenterId().equals(userId)) {
            UserPermissionsVO permissions = userService.getUserPermissions(userId);
            if (!permissions.getIsAdmin()) {
                throw new BusinessException(403, "无权限编辑此评论");
            }
        }
        
        // 3. 已发布的评论编辑后需要重新审核
        if ("approved".equals(comment.getAuditStatus())) {
            comment.setAuditStatus("pending");
        }
        
        // 4. 更新评论
        commentConverter.updateEntity(dto, comment);
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(comment);
        
        log.info("用户 {} 更新评论成功，评论ID：{}", userId, comment.getId());
    }
    
    @Override
    public void deleteComment(Long userId, Long commentId) {
        ForumComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 权限校验
        if (!comment.getCommenterId().equals(userId)) {
            UserPermissionsVO permissions = userService.getUserPermissions(userId);
            if (!permissions.getIsAdmin()) {
                throw new BusinessException(403, "无权限删除此评论");
            }
        }
        
        // 软删除
        comment.setIsDeleted(1);
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(comment);
        
        // 更新帖子评论数
        ForumPost post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.updateById(post);
        }
        
        // 如果是回复，更新父评论的回复数
        if (comment.getParentCommentId() != null && comment.getParentCommentId() > 0) {
            ForumComment parentComment = commentMapper.selectById(comment.getParentCommentId());
            if (parentComment != null) {
                parentComment.setReplyCount(Math.max(0, parentComment.getReplyCount() - 1));
                commentMapper.updateById(parentComment);
            }
        }
        
        log.info("用户 {} 删除评论成功，评论ID：{}", userId, commentId);
    }
    
    @Override
    public CommentVO getCommentDetail(Long userId, Long commentId) {
        ForumComment comment = commentMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new RuntimeException("评论不存在");
        }
        
        // 转换基本信息
        CommentVO vo = commentConverter.toVo(comment);
        
        // 填充评论者信息
        UserVO commenterInfo = userService.getUserInfo(comment.getCommenterId());
        if (commenterInfo != null) {
            UserInfoVO commenterUserInfo = new UserInfoVO();
            commenterUserInfo.setId(commenterInfo.getId());
            commenterUserInfo.setUsername(commenterInfo.getUsername());
            commenterUserInfo.setAvatar(commenterInfo.getAvatar());
            vo.setCommenterInfo(commenterUserInfo);
        }
        
        // 填充被回复者信息
        if (comment.getReplyToUserId() != null) {
            UserVO replyToUserInfo = userService.getUserInfo(comment.getReplyToUserId());
            if (replyToUserInfo != null) {
                UserInfoVO replyToUserInfoVO = new UserInfoVO();
                replyToUserInfoVO.setId(replyToUserInfo.getId());
                replyToUserInfoVO.setUsername(replyToUserInfo.getUsername());
                replyToUserInfoVO.setAvatar(replyToUserInfo.getAvatar());
                vo.setReplyToUserInfo(replyToUserInfoVO);
            }
        }
        
        // 填充回复列表（只填充前几条）
        List<ForumComment> replies = commentMapper.selectByParentId(commentId);
        if (!replies.isEmpty()) {
            List<CommentVO> replyVOs = replies.stream().map(reply -> {
                CommentVO replyVO = commentConverter.toVo(reply);
                UserVO replyCommenterInfo = userService.getUserInfo(reply.getCommenterId());
                if (replyCommenterInfo != null) {
                    UserInfoVO replyCommenterUserInfo = new UserInfoVO();
                    replyCommenterUserInfo.setId(replyCommenterInfo.getId());
                    replyCommenterUserInfo.setUsername(replyCommenterInfo.getUsername());
                    replyCommenterUserInfo.setAvatar(replyCommenterInfo.getAvatar());
                    replyVO.setCommenterInfo(replyCommenterUserInfo);
                }
                if (reply.getReplyToUserId() != null) {
                    UserVO replyToUserInfo = userService.getUserInfo(reply.getReplyToUserId());
                    if (replyToUserInfo != null) {
                        UserInfoVO replyToUserInfoVO = new UserInfoVO();
                        replyToUserInfoVO.setId(replyToUserInfo.getId());
                        replyToUserInfoVO.setUsername(replyToUserInfo.getUsername());
                        replyVO.setReplyToUserInfo(replyToUserInfoVO);
                    }
                }
                return replyVO;
            }).collect(Collectors.toList());
            vo.setReplyList(replyVOs);
        }
        
        // 当前用户的点赞状态
        if (userId != null) {
            ForumReaction like = reactionMapper.selectByUserAndTarget(userId, "comment", commentId);
            vo.setIsLiked(like != null && "like".equals(like.getReactionType()));
        }
        
        return vo;
    }
    
    @Override
    public PageResult<CommentVO> listComments(Long userId, Long postId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        
        // 查询顶级评论
        List<ForumComment> comments = commentMapper.selectByPostId(postId, offset, pageSize);
        long total = commentMapper.countByPostId(postId);
        
        // 转换为VO
        List<CommentVO> voList = new ArrayList<>();
        for (ForumComment comment : comments) {
            CommentVO vo = commentConverter.toVo(comment);
            
            // 填充评论者信息
            UserVO commenterInfo = userService.getUserInfo(comment.getCommenterId());
            if (commenterInfo != null) {
                UserInfoVO commenterUserInfo = new UserInfoVO();
                commenterUserInfo.setId(commenterInfo.getId());
                commenterUserInfo.setUsername(commenterInfo.getUsername());
                commenterUserInfo.setAvatar(commenterInfo.getAvatar());
                vo.setCommenterInfo(commenterUserInfo);
            }
            
            // 填充被回复者信息
            if (comment.getReplyToUserId() != null) {
                UserVO replyToUserInfo = userService.getUserInfo(comment.getReplyToUserId());
                if (replyToUserInfo != null) {
                    UserInfoVO replyToUserInfoVO = new UserInfoVO();
                    replyToUserInfoVO.setId(replyToUserInfo.getId());
                    replyToUserInfoVO.setUsername(replyToUserInfo.getUsername());
                    vo.setReplyToUserInfo(replyToUserInfoVO);
                }
            }
            
            // 填充回复列表（只填充前几条）
            List<ForumComment> replies = commentMapper.selectByParentId(comment.getId());
            if (!replies.isEmpty()) {
                List<CommentVO> replyVOs = replies.stream().map(reply -> {
                    CommentVO replyVO = commentConverter.toVo(reply);
                    UserVO replyCommenterInfo = userService.getUserInfo(reply.getCommenterId());
                    if (replyCommenterInfo != null) {
                        UserInfoVO replyCommenterUserInfo = new UserInfoVO();
                        replyCommenterUserInfo.setId(replyCommenterInfo.getId());
                        replyCommenterUserInfo.setUsername(replyCommenterInfo.getUsername());
                        replyCommenterUserInfo.setAvatar(replyCommenterInfo.getAvatar());
                        replyVO.setCommenterInfo(replyCommenterUserInfo);
                    }
                    return replyVO;
                }).collect(Collectors.toList());
                vo.setReplyList(replyVOs);
            }
            
            // 当前用户的点赞状态
            if (userId != null) {
                ForumReaction like = reactionMapper.selectByUserAndTarget(userId, "comment", comment.getId());
                vo.setIsLiked(like != null && "like".equals(like.getReactionType()));
            }
            
            voList.add(vo);
        }
        
        return new PageResult<>(total, pageNum, pageSize, voList);
    }
    
    @Override
    public PageResult<CommentVO> listReplies(Long userId, Long parentId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        
        // 获取所有回复并分页
        List<ForumComment> allReplies = commentMapper.selectByParentId(parentId);
        long total = allReplies.size();
        
        // 分页处理
        int endIndex = Math.min(offset + pageSize, allReplies.size());
        List<ForumComment> replies = offset >= allReplies.size() ? new ArrayList<>() : allReplies.subList(offset, endIndex);
        
        // 转换为VO
        List<CommentVO> voList = replies.stream().map(reply -> {
            CommentVO vo = commentConverter.toVo(reply);
            
            // 填充评论者信息
            UserVO commenterInfo = userService.getUserInfo(reply.getCommenterId());
            if (commenterInfo != null) {
                UserInfoVO commenterUserInfo = new UserInfoVO();
                commenterUserInfo.setId(commenterInfo.getId());
                commenterUserInfo.setUsername(commenterInfo.getUsername());
                commenterUserInfo.setAvatar(commenterInfo.getAvatar());
                vo.setCommenterInfo(commenterUserInfo);
            }
            
            // 填充被回复者信息
            if (reply.getReplyToUserId() != null) {
                UserVO replyToUserInfo = userService.getUserInfo(reply.getReplyToUserId());
                if (replyToUserInfo != null) {
                    UserInfoVO replyToUserInfoVO = new UserInfoVO();
                    replyToUserInfoVO.setId(replyToUserInfo.getId());
                    replyToUserInfoVO.setUsername(replyToUserInfo.getUsername());
                    vo.setReplyToUserInfo(replyToUserInfoVO);
                }
            }
            
            // 当前用户的点赞状态
            if (userId != null) {
                ForumReaction like = reactionMapper.selectByUserAndTarget(userId, "comment", reply.getId());
                vo.setIsLiked(like != null && "like".equals(like.getReactionType()));
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(total, pageNum, pageSize, voList);
    }
    
    @Override
    public Integer likeComment(Long userId, Long commentId) {
        ForumComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        ForumReaction existing = reactionMapper.selectByUserAndTarget(userId, "comment", commentId);
        
        if (existing != null) {
            // 取消点赞
            reactionMapper.deleteById(existing.getId());
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            comment.setUpdatedAt(LocalDateTime.now());
            commentMapper.updateById(comment);
            return comment.getLikeCount();
        } else {
            // 点赞
            ForumReaction reaction = ForumReaction.builder()
                    .targetType("comment")
                    .targetId(commentId)
                    .userId(userId)
                    .reactionType("like")
                    .createdAt(LocalDateTime.now())
                    .build();
            reactionMapper.insert(reaction);
            comment.setLikeCount(comment.getLikeCount() + 1);
            comment.setUpdatedAt(LocalDateTime.now());
            commentMapper.updateById(comment);
            return comment.getLikeCount();
        }
    }
    
    @Override
    public void auditComment(Long adminId, Long commentId, Boolean approved, String rejectReason) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new BusinessException(403, "无权限审核评论");
        }
        
        ForumComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        
        if (approved) {
            comment.setAuditStatus("approved");
        } else {
            comment.setAuditStatus("rejected");
        }
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(comment);
        
        log.info("管理员 {} 审核评论 {}，结果：{}", adminId, commentId, approved ? "通过" : "驳回");
    }
}