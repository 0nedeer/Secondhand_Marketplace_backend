package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.AdminLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminLogMapper {
    
    /**
     * 插入管理员操作日志
     */
    int insert(AdminLog adminLog);
    
    /**
     * 根据ID删除管理员操作日志
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新管理员操作日志
     */
    int updateById(AdminLog adminLog);
    
    /**
     * 根据ID查询管理员操作日志
     */
    AdminLog selectById(@Param("id") Long id);
    
    /**
     * 查询所有管理员操作日志
     */
    List<AdminLog> selectList();
    
    /**
     * 根据管理员ID查询操作日志
     */
    List<AdminLog> selectByAdminId(@Param("adminId") Long adminId);
    
    /**
     * 根据目标类型和目标ID查询操作日志
     */
    List<AdminLog> selectByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    List<AdminLog> selectPage(@Param("adminId") Long adminId,
                              @Param("targetType") String targetType,
                              @Param("action") String action,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

    long countByCondition(@Param("adminId") Long adminId,
                          @Param("targetType") String targetType,
                          @Param("action") String action,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);
}
