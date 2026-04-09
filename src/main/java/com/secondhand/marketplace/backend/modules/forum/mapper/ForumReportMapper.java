package com.secondhand.marketplace.backend.modules.forum.mapper;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ForumReportMapper {
    
    /**
     * 插入举报记录
     */
    int insert(ForumReport forumReport);
    
    /**
     * 根据ID删除举报记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID更新举报记录
     */
    int updateById(ForumReport forumReport);
    
    /**
     * 根据ID查询举报记录
     */
    ForumReport selectById(@Param("id") Long id);
    
    /**
     * 查询所有举报记录
     */
    List<ForumReport> selectList();
    
    /**
     * 根据目标类型和目标ID查询举报记录
     */
    List<ForumReport> selectByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);
    
    /**
     * 根据状态查询举报记录
     */
    List<ForumReport> selectByStatus(@Param("reportStatus") String reportStatus,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);
    
    /**
     * 根据举报人ID查询举报记录
     */
    List<ForumReport> selectByReporterId(@Param("reporterId") Long reporterId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);
}