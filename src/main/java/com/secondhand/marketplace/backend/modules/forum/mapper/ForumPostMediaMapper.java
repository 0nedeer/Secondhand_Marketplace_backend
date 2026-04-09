package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumPostMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumPostMediaMapper {
    
    /**
     * 插入帖子媒体附件
     */
    int insert(ForumPostMedia forumPostMedia);
    
    /**
     * 根据ID删除帖子媒体附件
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新帖子媒体附件
     */
    int updateById(ForumPostMedia forumPostMedia);
    
    /**
     * 根据ID查询帖子媒体附件
     */
    ForumPostMedia selectById(@Param("id") Long id);
    
    /**
     * 查询所有帖子媒体附件
     */
    List<ForumPostMedia> selectList();
    
    /**
     * 根据帖子ID查询媒体附件
     */
    List<ForumPostMedia> selectByPostId(@Param("postId") Long postId);
    
    /**
     * 根据帖子ID删除所有媒体附件
     */
    int deleteByPostId(@Param("postId") Long postId);
}