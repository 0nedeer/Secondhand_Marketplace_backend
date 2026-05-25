package com.secondhand.marketplace.backend.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.message.entity.SystemNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SystemNoticeMapper extends BaseMapper<SystemNotice> {

    // 管理员获取通知列表（支持类型和状态筛选）
    @Select("<script>" +
            "SELECT * FROM system_notice " +
            "<where>" +
            "  <if test='noticeType != null'> AND notice_type = #{noticeType} </if>" +
            "  <if test='publishStatus != null'> AND publish_status = #{publishStatus} </if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<SystemNotice> findByConditions(@Param("noticeType") String noticeType,
                                        @Param("publishStatus") String publishStatus,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    // 统计符合条件的通知数量
    @Select("<script>" +
            "SELECT COUNT(*) FROM system_notice " +
            "<where>" +
            "  <if test='noticeType != null'> AND notice_type = #{noticeType} </if>" +
            "  <if test='publishStatus != null'> AND publish_status = #{publishStatus} </if>" +
            "</where>" +
            "</script>")
    Long countByConditions(@Param("noticeType") String noticeType,
                           @Param("publishStatus") String publishStatus);

    // 撤回通知
    @Update("UPDATE system_notice SET publish_status = 'revoked', updated_at = NOW() WHERE id = #{noticeId}")
    int revokeNotice(Long noticeId);

    // 发布通知
    @Update("UPDATE system_notice SET publish_status = 'published', published_at = NOW(), updated_at = NOW() WHERE id = #{noticeId}")
    int publishNotice(Long noticeId);

    // 查询已发布的全局通知
    @Select("SELECT * FROM system_notice WHERE publish_status = 'published' AND target_scope = 'all' " +
            "AND published_at &lt;= NOW() ORDER BY published_at DESC")
    List<SystemNotice> findPublishedGlobalNotices();

    // 查询指定角色的已发布通知
    @Select("SELECT * FROM system_notice WHERE publish_status = 'published' AND target_scope = 'role' " +
            "AND target_role_code = #{roleCode} AND published_at &lt;= NOW() ORDER BY published_at DESC")
    List<SystemNotice> findPublishedRoleNotices(String roleCode);

    // 查询指定用户的已发布通知
    @Select("SELECT * FROM system_notice WHERE publish_status = 'published' AND target_scope = 'user' " +
            "AND target_user_id = #{userId} AND published_at &lt;= NOW() ORDER BY published_at DESC")
    List<SystemNotice> findPublishedUserNotices(Long userId);
}