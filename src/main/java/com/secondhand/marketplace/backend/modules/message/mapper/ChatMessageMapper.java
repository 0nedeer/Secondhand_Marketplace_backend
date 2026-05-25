package com.secondhand.marketplace.backend.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.message.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 根据会话ID查询消息列表（按时间正序，适合聊天界面）
     */
    @Select("SELECT * FROM chat_message WHERE conversation_id = #{conversationId}  " +
            "ORDER BY sent_at ASC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<ChatMessage> findByConversationId(@Param("conversationId") Long conversationId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    /**
     * 根据会话ID查询消息列表（按时间倒序，适合最新消息优先）
     */
    @Select("SELECT * FROM chat_message WHERE conversation_id = #{conversationId}  " +
            "ORDER BY sent_at DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<ChatMessage> findByConversationIdDesc(@Param("conversationId") Long conversationId,
                                               @Param("offset") int offset,
                                               @Param("limit") int limit);

    /**
     * 查询会话消息总数
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE conversation_id = #{conversationId} ")
    Integer countByConversationId(Long conversationId);

    /**
     * 撤回消息
     */
    @Update("UPDATE chat_message SET recalled = 1 WHERE id = #{messageId} AND sender_id = #{senderId}")
    int recallMessage(@Param("messageId") Long messageId, @Param("senderId") Long senderId);

    /**
     * 检查消息是否在可撤回时间内（5分钟内）
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE id = #{messageId} " +
            "AND sender_id = #{senderId} AND recalled = 0 " +
            "AND sent_at > DATE_SUB(NOW(), INTERVAL 5 MINUTE)")
    int checkRecallable(@Param("messageId") Long messageId, @Param("senderId") Long senderId);

    /**
     * 获取会话中某条消息之后的消息数量（用于判断是否有新消息）
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE conversation_id = #{conversationId} " +
            "AND sender_id != #{userId} AND sent_at > #{lastReadTime} AND recalled = 0")
    Integer countNewMessages(@Param("conversationId") Long conversationId,
                             @Param("userId") Long userId,
                             @Param("lastReadTime") LocalDateTime lastReadTime);

    /**
     * 获取会话中某用户的未读消息数（不依赖外部记录）
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE conversation_id = #{conversationId} " +
            "AND sender_id != #{userId} AND recalled = 0 " +
            "AND sent_at > IFNULL((" +
            "    SELECT last_read_at FROM conversation_read_record " +
            "    WHERE conversation_id = #{conversationId} AND user_id = #{userId}" +
            "), '1970-01-01')")
    Integer countUnreadCount(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    /**
     * 获取会话最后一条消息
     */
    @Select("SELECT * FROM chat_message WHERE conversation_id = #{conversationId} AND recalled = 0 " +
            "ORDER BY sent_at DESC LIMIT 1")
    ChatMessage getLastMessage(@Param("conversationId") Long conversationId);
}