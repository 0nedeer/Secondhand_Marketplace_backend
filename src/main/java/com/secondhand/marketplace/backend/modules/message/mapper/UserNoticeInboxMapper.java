package com.secondhand.marketplace.backend.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.message.entity.UserNoticeInbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserNoticeInboxMapper extends BaseMapper<UserNoticeInbox> {

    // 查询用户的通知列表（支持阅读状态筛选）
    @Select("<script>" +
            "SELECT i.*, n.title, n.content, n.notice_type " +
            "FROM user_notice_inbox i " +
            "JOIN system_notice n ON i.notice_id = n.id " +
            "WHERE i.user_id = #{userId} " +
            "  <if test='readStatus != null'> AND i.read_status = #{readStatus} </if>" +
            "ORDER BY i.delivered_at DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<UserNoticeInbox> findByUserId(@Param("userId") Long userId,
                                       @Param("readStatus") String readStatus,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    // 统计用户通知数量
    @Select("<script>" +
            "SELECT COUNT(*) FROM user_notice_inbox " +
            "WHERE user_id = #{userId} " +
            "  <if test='readStatus != null'> AND read_status = #{readStatus} </if>" +
            "</script>")
    Long countByUserId(@Param("userId") Long userId,
                       @Param("readStatus") String readStatus);

    // 查询未读通知数量
    @Select("SELECT COUNT(*) FROM user_notice_inbox WHERE user_id = #{userId} AND read_status = 'unread'")
    Long countUnreadByUserId(Long userId);

    // 标记单条通知为已读
    @Update("UPDATE user_notice_inbox SET read_status = 'read', read_at = NOW(), updated_at = NOW() " +
            "WHERE id = #{inboxId} AND user_id = #{userId}")
    int markAsRead(@Param("inboxId") Long inboxId, @Param("userId") Long userId);

    // 标记所有通知为已读
    @Update("UPDATE user_notice_inbox SET read_status = 'read', read_at = NOW(), updated_at = NOW() " +
            "WHERE user_id = #{userId} AND read_status = 'unread'")
    int markAllAsRead(Long userId);

    // 检查用户是否已收到某条通知
    @Select("SELECT COUNT(*) FROM user_notice_inbox WHERE notice_id = #{noticeId} AND user_id = #{userId}")
    int checkExists(@Param("noticeId") Long noticeId, @Param("userId") Long userId);
}