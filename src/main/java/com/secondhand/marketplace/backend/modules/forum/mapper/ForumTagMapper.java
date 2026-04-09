package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumTagMapper {
    
    /**
     * 插入论坛标签
     */
    int insert(ForumTag forumTag);
    
    /**
     * 根据ID删除论坛标签
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新论坛标签
     */
    int updateById(ForumTag forumTag);
    
    /**
     * 根据ID查询论坛标签
     */
    ForumTag selectById(@Param("id") Long id);
    
    /**
     * 查询所有论坛标签
     */
    List<ForumTag> selectList();
    
    /**
     * 查询所有启用的论坛标签
     */
    List<ForumTag> selectAllEnabled();
    
    /**
     * 根据标签名称查询标签
     */
    ForumTag selectByTagName(@Param("tagName") String tagName);
}