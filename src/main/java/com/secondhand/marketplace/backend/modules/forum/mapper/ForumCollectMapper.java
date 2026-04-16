package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumCollect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumCollectMapper {
    
    /**
     * 插入收藏记录
     */
    int insert(ForumCollect forumCollect);
    
    /**
     * 根据ID删除收藏记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新收藏记录
     */
    int updateById(ForumCollect forumCollect);
    
    /**
     * 根据ID查询收藏记录
     */
    ForumCollect selectById(@Param("id") Long id);
    
    /**
     * 查询所有收藏记录
     */
    List<ForumCollect> selectList();
    
    /**
     * 根据用户ID查询收藏记录
     */
    List<ForumCollect> selectByUserId(@Param("userId") Long userId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);
    
    /**
     * 根据帖子ID查询收藏记录
     */
    List<ForumCollect> selectByPostId(@Param("postId") Long postId);
    
    /**
     * 根据用户ID和帖子ID查询收藏记录
     */
    ForumCollect selectByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);
}