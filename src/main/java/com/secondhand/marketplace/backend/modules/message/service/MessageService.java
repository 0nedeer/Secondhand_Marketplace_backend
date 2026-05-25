package com.secondhand.marketplace.backend.modules.message.service;

import com.secondhand.marketplace.backend.modules.message.dto.*;
import com.secondhand.marketplace.backend.modules.message.vo.*;

import java.util.List;

public interface MessageService {

    // ========== 消息相关 ==========

    // 发送消息
    ChatMessageVO sendMessage(Long senderId, SendMessageDTO sendMessageDTO);

    // 获取消息详情（根据会话ID获取消息列表）
    List<ChatMessageVO> getMessageList(Long userId, Long conversationId, Integer pageNum, Integer pageSize);

    // 撤回消息
    void recallMessage(Long senderId, Long messageId);

    // ========== 会话相关 ==========

    // 获取消息列表（会话列表）
    List<ConversationVO> getConversationList(Long userId);

    List<ConversationVO> getConversationList(Long userId, Integer pageNum, Integer pageSize);


    // 获取单个会话详情
    ConversationDetailVO getConversationDetail(Long userId, Long conversationId);

    // 标记会话已读
    void markConversationRead(Long userId, Long conversationId);

    Integer getConversationUnreadCount(Long userId, Long conversationId);

    Long getTotalUnreadCount(Long userId);

    // 创建会话（内部使用，或由业务触发）
    ConversationVO createConversation(Long currentUserId, CreateConversationDTO dto);
}