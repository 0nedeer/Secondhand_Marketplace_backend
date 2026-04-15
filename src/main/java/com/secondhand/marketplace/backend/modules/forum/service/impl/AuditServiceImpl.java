package com.secondhand.marketplace.backend.modules.forum.service.impl;

import com.secondhand.marketplace.backend.modules.forum.convert.AuditLogConverter;
import com.secondhand.marketplace.backend.modules.forum.convert.PostConverter;
import com.secondhand.marketplace.backend.modules.forum.dto.AuditReviewDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.*;
import com.secondhand.marketplace.backend.modules.forum.mapper.*;
import com.secondhand.marketplace.backend.modules.forum.service.AuditService;
import com.secondhand.marketplace.backend.modules.forum.vo.AuditLogVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.forum.vo.PostListVO;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AuditServiceImpl implements AuditService {
    
    private final ForumAuditLogMapper auditLogMapper;
    private final ForumPostMapper postMapper;
    private final ForumCommentMapper commentMapper;
    private final ForumPostMediaMapper postMediaMapper;
    private final PostConverter postConverter;
    private final AuditLogConverter auditLogConverter;
    private final UserService userService;
    
    @Override
    public void auditPost(Long adminId, AuditReviewDTO dto) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限审核帖子");
        }
        
        // 校验帖子是否存在
        ForumPost post = postMapper.selectById(dto.getTargetId());
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }
        
        // 记录旧状态
        String oldStatus = post.getAuditStatus();
        
        // 更新帖子状态
        if ("approve".equals(dto.getAction())) {
            post.setAuditStatus("approved");
            post.setPublishedAt(LocalDateTime.now());
        } else if ("reject".equals(dto.getAction())) {
            post.setAuditStatus("rejected");
        } else if ("hide".equals(dto.getAction())) {
            post.setDisplayStatus("hidden");
        } else if ("delete".equals(dto.getAction())) {
            post.setIsDeleted(1);
        } else if ("restore".equals(dto.getAction())) {
            post.setIsDeleted(0);
            post.setAuditStatus("approved");
        }
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        // 记录审核日志
        ForumAuditLog auditLog = ForumAuditLog.builder()
                .targetType("post")
                .targetId(dto.getTargetId())
                .auditorId(adminId)
                .action(dto.getAction())
                .reason(dto.getReason())
                .oldStatus(oldStatus)
                .newStatus(post.getAuditStatus())
                .createdAt(LocalDateTime.now())
                .build();
        auditLogMapper.insert(auditLog);
        
        log.info("管理员 {} 审核帖子 {}，操作：{}", adminId, dto.getTargetId(), dto.getAction());
    }
    
    @Override
    public void auditComment(Long adminId, AuditReviewDTO dto) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限审核评论");
        }
        
        // 校验评论是否存在
        ForumComment comment = commentMapper.selectById(dto.getTargetId());
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 记录旧状态
        String oldStatus = comment.getAuditStatus();
        
        // 更新评论状态
        if ("approve".equals(dto.getAction())) {
            comment.setAuditStatus("approved");
        } else if ("reject".equals(dto.getAction())) {
            comment.setAuditStatus("rejected");
        } else if ("delete".equals(dto.getAction())) {
            comment.setIsDeleted(1);
        }
        comment.setUpdatedAt(LocalDateTime.now());
        commentMapper.updateById(comment);
        
        // 记录审核日志
        ForumAuditLog auditLog = ForumAuditLog.builder()
                .targetType("comment")
                .targetId(dto.getTargetId())
                .auditorId(adminId)
                .action(dto.getAction())
                .reason(dto.getReason())
                .oldStatus(oldStatus)
                .newStatus(comment.getAuditStatus())
                .createdAt(LocalDateTime.now())
                .build();
        auditLogMapper.insert(auditLog);
        
        log.info("管理员 {} 审核评论 {}，操作：{}", adminId, dto.getTargetId(), dto.getAction());
    }
    
    @Override
    public PageResult<PostListVO> getPendingPosts(Long adminId, Integer pageNum, Integer pageSize) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限查看待审核帖子");
        }
        
        int offset = (pageNum - 1) * pageSize;
        List<ForumPost> posts = postMapper.selectByStatus("pending", offset, pageSize);
        long total = postMapper.countByCondition(null, null, "pending", null, null, null);
        
        // 转换为VO
        List<PostListVO> voList = new ArrayList<>();
        for (ForumPost post : posts) {
            PostListVO vo = postConverter.toListVo(post);
            
            // 填充作者信息
            UserVO authorInfo = userService.getUserInfo(post.getAuthorId());
            if (authorInfo != null) {
                vo.setAuthorName(authorInfo.getUsername());
                vo.setAuthorAvatar(authorInfo.getAvatar());
            }
            
            // 填充首张图片
            List<ForumPostMedia> mediaList = postMediaMapper.selectByPostId(post.getId());
            if (!mediaList.isEmpty()) {
                vo.setFirstMediaUrl(mediaList.get(0).getMediaUrl());
            }
            
            voList.add(vo);
        }
        
        return new PageResult<>(total, pageNum, pageSize, voList);
    }
    
    @Override
    public PageResult<AuditLogVO> getAuditLogs(Long adminId, String targetType, Integer pageNum, Integer pageSize) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限查看审核日志");
        }
        
        // 获取所有日志并过滤
        List<ForumAuditLog> allLogs = auditLogMapper.selectList();
        List<ForumAuditLog> filteredLogs = allLogs.stream()
                .filter(log -> targetType == null || targetType.isEmpty() || log.getTargetType().equals(targetType))
                .collect(java.util.stream.Collectors.toList());
        
        // 分页处理
        int offset = (pageNum - 1) * pageSize;
        int endIndex = Math.min(offset + pageSize, filteredLogs.size());
        List<ForumAuditLog> logs = offset >= filteredLogs.size() ? new java.util.ArrayList<>() : filteredLogs.subList(offset, endIndex);
        long total = filteredLogs.size();
        
        // 转换为VO
        List<AuditLogVO> voList = new ArrayList<>();
        for (ForumAuditLog log : logs) {
            AuditLogVO vo = auditLogConverter.toVo(log);
            
            // 填充审核员信息
            UserVO auditorInfo = userService.getUserInfo(log.getAuditorId());
            if (auditorInfo != null) {
                UserInfoVO auditorUserInfo = new UserInfoVO();
                auditorUserInfo.setId(auditorInfo.getId());
                auditorUserInfo.setUsername(auditorInfo.getUsername());
                auditorUserInfo.setAvatar(auditorInfo.getAvatar());
                vo.setAuditorInfo(auditorUserInfo);
            }
            
            voList.add(vo);
        }
        
        return new PageResult<>(total, pageNum, pageSize, voList);
    }
}