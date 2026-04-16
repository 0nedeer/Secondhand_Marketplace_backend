package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumPostMapper {
    
    /**
     * 插入帖子
     */
    int insert(ForumPost forumPost);
    
    /**
     * 根据ID删除帖子
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新帖子
     */
    int updateById(ForumPost forumPost);
    
    /**
     * 根据ID查询帖子
     */
    ForumPost selectById(@Param("id") Long id);
    
    /**
     * 查询所有帖子
     */
    List<ForumPost> selectList();
    
    /**
     * 分页查询帖子列表（支持多条件筛选）
     */
    List<ForumPost> selectPageList(@Param("categoryId") Long categoryId,
                                  @Param("postType") String postType,
                                  @Param("auditStatus") String auditStatus,
                                  @Param("displayStatus") String displayStatus,
                                  @Param("keyword") String keyword,
                                  @Param("authorId") Long authorId,
                                  @Param("sortBy") String sortBy,
                                  @Param("order") String order,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);
    
    /**
     * 统计符合条件的帖子总数
     */
    long countByCondition(@Param("categoryId") Long categoryId,
                         @Param("postType") String postType,
                         @Param("auditStatus") String auditStatus,
                         @Param("displayStatus") String displayStatus,
                         @Param("keyword") String keyword,
                         @Param("authorId") Long authorId);
    
    /**
     * 根据作者ID查询帖子列表
     */
    List<ForumPost> selectByAuthorId(@Param("authorId") Long authorId,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);
    
    /**
     * 根据状态查询帖子
     */
    List<ForumPost> selectByStatus(@Param("auditStatus") String auditStatus,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);
    
    /**
     * 更新帖子计数（点赞数、评论数、分享数、收藏数、浏览数）
     */
    int updateCounts(@Param("id") Long id,
                    @Param("likeCount") int likeCount,
                    @Param("commentCount") int commentCount,
                    @Param("shareCount") int shareCount,
                    @Param("collectCount") int collectCount,
                    @Param("viewCount") int viewCount);
}