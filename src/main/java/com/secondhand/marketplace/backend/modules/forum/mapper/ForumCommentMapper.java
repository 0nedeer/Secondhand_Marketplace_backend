package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumCommentMapper {
    
    /**
     * 插入评论
     */
    int insert(ForumComment forumComment);
    
    /**
     * 根据ID删除评论
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新评论
     */
    int updateById(ForumComment forumComment);
    
    /**
     * 根据ID查询评论
     */
    ForumComment selectById(@Param("id") Long id);
    
    /**
     * 查询所有评论
     */
    List<ForumComment> selectList();
    
    /**
     * 查询帖子的顶级评论列表（parent_comment_id = 0）
     */
    List<ForumComment> selectByPostId(@Param("postId") Long postId,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);
    
    /**
     * 查询某个评论的子回复列表
     */
    List<ForumComment> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 根据评论用户ID查询评论
     */
    List<ForumComment> selectByCommenterId(@Param("commenterId") Long commenterId,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);
    
    /**
     * 统计帖子的评论总数
     */
    int countByPostId(@Param("postId") Long postId);
}