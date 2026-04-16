package com.secondhand.marketplace.backend.modules.forum.service;

import com.secondhand.marketplace.backend.modules.forum.dto.TagCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.TagUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.vo.TagVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TagService {
    
    /**
     * 创建标签
     * @param adminId 管理员ID
     * @param dto 标签信息
     * @return 标签ID
     */
    @Transactional(rollbackFor = Exception.class)
    Long createTag(Long adminId, TagCreateDTO dto);
    
    /**
     * 更新标签
     * @param adminId 管理员ID
     * @param dto 更新信息
     */
    @Transactional(rollbackFor = Exception.class)
    void updateTag(Long adminId, TagUpdateDTO dto);
    
    /**
     * 删除标签
     * @param adminId 管理员ID
     * @param tagId 标签ID
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteTag(Long adminId, Long tagId);
    
    /**
     * 获取标签详情
     * @param tagId 标签ID
     * @return 标签详情VO
     */
    TagVO getTagById(Long tagId);
    
    /**
     * 获取所有标签列表
     * @return 标签列表
     */
    List<TagVO> listTags();
    
    /**
     * 获取所有启用的标签列表
     * @return 启用的标签列表
     */
    List<TagVO> listEnabledTags();
    
    /**
     * 关注标签
     * @param userId 用户ID
     * @param tagId 标签ID
     */
    @Transactional(rollbackFor = Exception.class)
    void followTag(Long userId, Long tagId);
    
    /**
     * 取消关注标签
     * @param userId 用户ID
     * @param tagId 标签ID
     */
    @Transactional(rollbackFor = Exception.class)
    void unfollowTag(Long userId, Long tagId);
    
    /**
     * 查询用户是否关注了标签
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 是否已关注
     */
    boolean isFollowing(Long userId, Long tagId);
    
    /**
     * 查询用户关注的标签列表
     * @param userId 用户ID
     * @return 关注的标签列表
     */
    List<TagVO> listUserFollowTags(Long userId);
}