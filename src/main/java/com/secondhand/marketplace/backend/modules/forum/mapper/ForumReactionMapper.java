package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumReaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumReactionMapper {
    
    /**
     * 插入互动记录
     */
    int insert(ForumReaction forumReaction);
    
    /**
     * 根据ID删除互动记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新互动记录
     */
    int updateById(ForumReaction forumReaction);
    
    /**
     * 根据ID查询互动记录
     */
    ForumReaction selectById(@Param("id") Long id);
    
    /**
     * 查询所有互动记录
     */
    List<ForumReaction> selectList();
    
    /**
     * 查询目标的所有互动记录
     */
    List<ForumReaction> selectByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);
    
    /**
     * 查询用户对某个目标的互动记录
     */
    ForumReaction selectByUserAndTarget(@Param("userId") Long userId,
                                       @Param("targetType") String targetType,
                                       @Param("targetId") Long targetId);
    
    /**
     * 删除目标的所有互动记录（用于删除帖子/评论时）
     */
    int deleteByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);
}