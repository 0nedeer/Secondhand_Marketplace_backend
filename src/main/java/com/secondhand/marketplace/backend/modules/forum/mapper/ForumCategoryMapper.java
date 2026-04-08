package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumCategoryMapper {
    
    /**
     * 插入论坛分类
     */
    int insert(ForumCategory forumCategory);
    
    /**
     * 根据ID删除论坛分类
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新论坛分类
     */
    int updateById(ForumCategory forumCategory);
    
    /**
     * 根据ID查询论坛分类
     */
    ForumCategory selectById(@Param("id") Long id);
    
    /**
     * 查询所有论坛分类
     */
    List<ForumCategory> selectList();
    
    /**
     * 查询所有启用的论坛分类
     */
    List<ForumCategory> selectAllEnabled();
    
    /**
     * 根据父分类ID查询子分类
     */
    List<ForumCategory> selectByParentId(@Param("parentId") Long parentId);
}