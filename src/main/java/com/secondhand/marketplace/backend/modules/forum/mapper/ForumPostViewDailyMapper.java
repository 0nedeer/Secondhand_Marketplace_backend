package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumPostViewDaily;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumPostViewDailyMapper {
    
    /**
     * 插入日浏览统计
     */
    int insert(ForumPostViewDaily forumPostViewDaily);
    
    /**
     * 根据ID删除日浏览统计
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新日浏览统计
     */
    int updateById(ForumPostViewDaily forumPostViewDaily);
    
    /**
     * 根据ID查询日浏览统计
     */
    ForumPostViewDaily selectById(@Param("id") Long id);
    
    /**
     * 查询所有日浏览统计
     */
    List<ForumPostViewDaily> selectList();
    
    /**
     * 根据帖子ID和日期查询统计
     */
    ForumPostViewDaily selectByPostIdAndDate(@Param("postId") Long postId, @Param("statDate") String statDate);
    
    /**
     * 插入或更新统计（ON DUPLICATE KEY UPDATE）
     */
    int insertOrUpdate(ForumPostViewDaily forumPostViewDaily);
}