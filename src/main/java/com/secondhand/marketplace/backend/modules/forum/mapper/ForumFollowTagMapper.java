package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumFollowTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumFollowTagMapper {
    
    /**
     * 插入用户关注标签记录
     */
    int insert(ForumFollowTag forumFollowTag);
    
    /**
     * 根据ID删除用户关注标签记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新用户关注标签记录
     */
    int updateById(ForumFollowTag forumFollowTag);
    
    /**
     * 根据ID查询用户关注标签记录
     */
    ForumFollowTag selectById(@Param("id") Long id);
    
    /**
     * 查询所有用户关注标签记录
     */
    List<ForumFollowTag> selectList();
    
    /**
     * 根据用户ID查询关注的标签
     */
    List<ForumFollowTag> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据标签ID查询关注的用户
     */
    List<ForumFollowTag> selectByTagId(@Param("tagId") Long tagId);
    
    /**
     * 根据用户ID和标签ID查询关注记录
     */
    ForumFollowTag selectByUserAndTag(@Param("userId") Long userId, @Param("tagId") Long tagId);
}