package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumPostShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumPostShareMapper {
    
    /**
     * 插入转发记录
     */
    int insert(ForumPostShare forumPostShare);
    
    /**
     * 根据ID删除转发记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新转发记录
     */
    int updateById(ForumPostShare forumPostShare);
    
    /**
     * 根据ID查询转发记录
     */
    ForumPostShare selectById(@Param("id") Long id);
    
    /**
     * 查询所有转发记录
     */
    List<ForumPostShare> selectList();
    
    /**
     * 根据帖子ID查询转发记录
     */
    List<ForumPostShare> selectByPostId(@Param("postId") Long postId);
    
    /**
     * 根据用户ID查询转发记录
     */
    List<ForumPostShare> selectByUserId(@Param("userId") Long userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);
}