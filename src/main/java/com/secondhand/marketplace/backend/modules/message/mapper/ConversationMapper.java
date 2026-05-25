package com.secondhand.marketplace.backend.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.message.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    /**
     * 查询用户参与的所有会话（作为发起方或接收方）
     */
    @Select("SELECT * FROM conversation WHERE initiator_id = #{userId} OR receiver_id = #{userId} " +
            "ORDER BY last_message_at DESC")
    List<Conversation> findByUserId(@Param("userId") Long userId);

    /**
     * 查询用户参与的所有会话（分页）
     */
    @Select("SELECT c.* FROM conversation c WHERE c.initiator_id = #{userId} " +
            "UNION " +
            "SELECT c.* FROM conversation c WHERE c.receiver_id = #{userId} " +
            "ORDER BY last_message_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Conversation> findByUserIdWithPage(@Param("userId") Long userId,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

    /**
     * 统计用户会话总数
     */
    @Select("SELECT COUNT(*) FROM (" +
            "SELECT id FROM conversation WHERE initiator_id = #{userId} " +
            "UNION " +
            "SELECT id FROM conversation WHERE receiver_id = #{userId}" +
            ") AS t")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 更新会话最后消息时间和内容
     */
    @Update("UPDATE conversation SET last_message_at = NOW(), last_message_content = #{content} WHERE id = #{conversationId}")
    void updateLastMessage(@Param("conversationId") Long conversationId, @Param("content") String content);
    /**
     * 检查两个用户之间是否已存在某种类型的会话
     */
    @Select("SELECT * FROM conversation WHERE conversation_type = #{conversationType} " +
            "AND ((initiator_id = #{userId1} AND receiver_id = #{userId2}) " +
            "OR (initiator_id = #{userId2} AND receiver_id = #{userId1})) " +
            "AND (product_id = #{productId} OR (#{productId} IS NULL AND product_id IS NULL)) " +
            "AND (order_id = #{orderId} OR (#{orderId} IS NULL AND order_id IS NULL)) " +
            "LIMIT 1")
    Conversation findExistingConversation(@Param("conversationType") String conversationType,
                                          @Param("userId1") Long userId1,
                                          @Param("userId2") Long userId2,
                                          @Param("productId") Long productId,
                                          @Param("orderId") Long orderId);

    /**
     * 获取会话的对方用户ID
     */
    @Select("SELECT CASE WHEN initiator_id = #{userId} THEN receiver_id ELSE initiator_id END " +
            "FROM conversation WHERE id = #{conversationId}")
    Long getOtherParticipantId(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}