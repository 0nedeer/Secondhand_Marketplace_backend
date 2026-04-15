package com.secondhand.marketplace.backend.modules.forum.service.impl;

import com.secondhand.marketplace.backend.modules.forum.convert.TagConverter;
import com.secondhand.marketplace.backend.modules.forum.dto.TagCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.TagUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.*;
import com.secondhand.marketplace.backend.modules.forum.mapper.*;
import com.secondhand.marketplace.backend.modules.forum.service.TagService;
import com.secondhand.marketplace.backend.modules.forum.vo.TagVO;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import com.secondhand.marketplace.backend.modules.user.vo.UserPermissionsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TagServiceImpl implements TagService {
    
    private final ForumTagMapper tagMapper;
    private final ForumFollowTagMapper followTagMapper;
    private final UserService userService;
    private final TagConverter tagConverter;
    
    @Override
    public Long createTag(Long adminId, TagCreateDTO dto) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限创建标签");
        }
        
        // 校验标签名是否已存在
        ForumTag existingTag = tagMapper.selectByTagName(dto.getTagName());
        if (existingTag != null) {
            throw new RuntimeException("标签名已存在");
        }
        
        // DTO转Entity
        ForumTag tag = tagConverter.toEntity(dto);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());
        
        // 保存标签
        tagMapper.insert(tag);
        
        log.info("管理员 {} 创建标签成功，标签ID：{}", adminId, tag.getId());
        return tag.getId();
    }
    
    @Override
    public void updateTag(Long adminId, TagUpdateDTO dto) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限更新标签");
        }
        
        // 校验标签是否存在
        ForumTag tag = tagMapper.selectById(dto.getId());
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        
        // 校验标签名是否已存在
        if (!tag.getTagName().equals(dto.getTagName())) {
            ForumTag existingTag = tagMapper.selectByTagName(dto.getTagName());
            if (existingTag != null) {
                throw new RuntimeException("标签名已存在");
            }
        }
        
        // 更新标签
        tagConverter.updateEntity(dto, tag);
        tag.setUpdatedAt(LocalDateTime.now());
        tagMapper.updateById(tag);
        
        log.info("管理员 {} 更新标签成功，标签ID：{}", adminId, dto.getId());
    }
    
    @Override
    public void deleteTag(Long adminId, Long tagId) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限删除标签");
        }
        
        // 校验标签是否存在
        ForumTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        
        // 删除标签
        tagMapper.deleteById(tagId);
        
        // 删除相关的关注关系
        List<ForumFollowTag> followTags = followTagMapper.selectByTagId(tagId);
        for (ForumFollowTag followTag : followTags) {
            followTagMapper.deleteById(followTag.getId());
        }
        
        log.info("管理员 {} 删除标签成功，标签ID：{}", adminId, tagId);
    }
    
    @Override
    public TagVO getTagById(Long tagId) {
        ForumTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        return tagConverter.toVo(tag);
    }
    
    @Override
    public List<TagVO> listTags() {
        List<ForumTag> tags = tagMapper.selectList();
        return tagConverter.toVoList(tags);
    }
    
    @Override
    public List<TagVO> listEnabledTags() {
        List<ForumTag> tags = tagMapper.selectAllEnabled();
        return tagConverter.toVoList(tags);
    }
    
    @Override
    public void followTag(Long userId, Long tagId) {
        // 校验标签是否存在
        ForumTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new RuntimeException("标签不存在");
        }
        
        // 校验是否已关注
        ForumFollowTag existing = followTagMapper.selectByUserAndTag(userId, tagId);
        if (existing != null) {
            throw new RuntimeException("已经关注了该标签");
        }
        
        // 创建关注关系
        ForumFollowTag followTag = ForumFollowTag.builder()
                .userId(userId)
                .tagId(tagId)
                .createdAt(LocalDateTime.now())
                .build();
        followTagMapper.insert(followTag);
        
        log.info("用户 {} 关注标签成功，标签ID：{}", userId, tagId);
    }
    
    @Override
    public void unfollowTag(Long userId, Long tagId) {
        // 校验是否已关注
        ForumFollowTag existing = followTagMapper.selectByUserAndTag(userId, tagId);
        if (existing == null) {
            throw new RuntimeException("未关注该标签");
        }
        
        // 删除关注关系
        followTagMapper.deleteById(existing.getId());
        
        log.info("用户 {} 取消关注标签成功，标签ID：{}", userId, tagId);
    }
    
    @Override
    public boolean isFollowing(Long userId, Long tagId) {
        ForumFollowTag followTag = followTagMapper.selectByUserAndTag(userId, tagId);
        return followTag != null;
    }
    
    @Override
    public List<TagVO> listUserFollowTags(Long userId) {
        List<ForumFollowTag> followTags = followTagMapper.selectByUserId(userId);
        if (followTags.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取标签ID列表
        List<Long> tagIds = followTags.stream().map(ForumFollowTag::getTagId).collect(Collectors.toList());
        
        // 查询标签详情
        List<TagVO> tags = new ArrayList<TagVO>();
        for (Long tagId : tagIds) {
            ForumTag tag = tagMapper.selectById(tagId);
            if (tag != null) {
                TagVO tagVO = tagConverter.toVo(tag);
                tagVO.setIsFollowed(true);
                tags.add(tagVO);
            }
        }
        
        return tags;
    }
}