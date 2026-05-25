package com.secondhand.marketplace.backend.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.message.entity.ConversationReadRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface ConversationReadRecordMapper extends BaseMapper<ConversationReadRecord> {

    @Select("SELECT last_read_at FROM conversation_read_record WHERE conversation_id = #{conversationId} AND user_id = #{userId}")
    LocalDateTime getLastReadTime(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Update("INSERT INTO conversation_read_record (conversation_id, user_id, last_read_at) " +
            "VALUES (#{conversationId}, #{userId}, #{lastReadAt}) " +
            "ON DUPLICATE KEY UPDATE last_read_at = #{lastReadAt}, updated_at = NOW()")
    void updateLastReadTime(@Param("conversationId") Long conversationId,
                            @Param("userId") Long userId,
                            @Param("lastReadAt") LocalDateTime lastReadAt);
}