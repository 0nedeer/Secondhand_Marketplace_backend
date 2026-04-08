package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumPostTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumPostTagMapper {
    
    /**
     * 插入帖子标签关联
     */
    int insert(ForumPostTag forumPostTag);
    
    /**
     * 根据ID删除帖子标签关联
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新帖子标签关联
     */
    int updateById(ForumPostTag forumPostTag);
    
    /**
     * 根据ID查询帖子标签关联
     */
    ForumPostTag selectById(@Param("id") Long id);
    
    /**
     * 查询所有帖子标签关联
     */
    List<ForumPostTag> selectList();
    
    /**
     * 根据帖子ID查询标签关联
     */
    List<ForumPostTag> selectByPostId(@Param("postId") Long postId);
    
    /**
     * 根据标签ID查询帖子关联
     */
    List<ForumPostTag> selectByTagId(@Param("tagId") Long tagId);
    
    /**
     * 根据帖子ID删除所有标签关联
     */
    int deleteByPostId(@Param("postId") Long postId);
}