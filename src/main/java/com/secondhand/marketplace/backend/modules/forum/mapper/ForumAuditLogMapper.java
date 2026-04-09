package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumAuditLogMapper {
    
    /**
     * 插入审核日志
     */
    int insert(ForumAuditLog forumAuditLog);
    
    /**
     * 根据ID删除审核日志
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新审核日志
     */
    int updateById(ForumAuditLog forumAuditLog);
    
    /**
     * 根据ID查询审核日志
     */
    ForumAuditLog selectById(@Param("id") Long id);
    
    /**
     * 查询所有审核日志
     */
    List<ForumAuditLog> selectList();
    
    /**
     * 根据目标类型和目标ID查询审核日志
     */
    List<ForumAuditLog> selectByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);
    
    /**
     * 根据审核员ID查询审核日志
     */
    List<ForumAuditLog> selectByAuditorId(@Param("auditorId") Long auditorId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
}