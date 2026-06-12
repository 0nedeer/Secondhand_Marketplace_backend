package com.secondhand.marketplace.backend.modules.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.message.dto.CreateConversationDTO;
import com.secondhand.marketplace.backend.modules.message.dto.SendMessageDTO;
import com.secondhand.marketplace.backend.modules.message.entity.ChatMessage;
import com.secondhand.marketplace.backend.modules.message.entity.Conversation;
import com.secondhand.marketplace.backend.modules.message.entity.ConversationReadRecord;
import com.secondhand.marketplace.backend.modules.message.mapper.ChatMessageMapper;
import com.secondhand.marketplace.backend.modules.message.mapper.ConversationMapper;
import com.secondhand.marketplace.backend.modules.message.mapper.ConversationReadRecordMapper;
import com.secondhand.marketplace.backend.modules.message.service.MessageService;
import com.secondhand.marketplace.backend.modules.message.vo.ChatMessageVO;
import com.secondhand.marketplace.backend.modules.message.vo.ConversationDetailVO;
import com.secondhand.marketplace.backend.modules.message.vo.ConversationVO;
import com.secondhand.marketplace.backend.modules.product.entity.Product;
import com.secondhand.marketplace.backend.modules.product.entity.ProductImage;
import com.secondhand.marketplace.backend.modules.product.mapper.ProductMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.TradeOrder;
import com.secondhand.marketplace.backend.modules.trade.mapper.TradeOrderMapper;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final ConversationMapper conversationMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final ConversationReadRecordMapper readRecordMapper;
    private final UserAccountMapper userAccountMapper;
    //注入订单模块mapper
    private final TradeOrderMapper tradeOrderMapper;
    // 注入商品模块的 Mapper
    private final com.secondhand.marketplace.backend.modules.product.mapper.ProductMapper productMapper;
    private final com.secondhand.marketplace.backend.modules.product.mapper.ProdImageMapper prodImageMapper;


    // ==================== 消息相关 ====================

    @Override
    @Transactional
    public ChatMessageVO sendMessage(Long senderId, SendMessageDTO sendMessageDTO) {
        Long conversationId = sendMessageDTO.getConversationId();

        // 1. 未传 conversationId：自动查找已有会话，没有则创建
        if (conversationId == null) {
            if (sendMessageDTO.getReceiverId() == null) {
                throw new BusinessException("未指定会话ID时，对方用户ID不能为空");
            }
            conversationId = findOrCreateConversation(senderId, sendMessageDTO.getReceiverId(),
                    sendMessageDTO.getProductId(), sendMessageDTO.getOrderId());
        }

        // 2. 验证会话是否存在
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        // 3. 验证用户是否属于该会话
        if (!isConversationParticipant(conversation, senderId)) {
            throw new BusinessException("无权在该会话中发送消息");
        }

        // 4. 验证用户是否有发送消息的权限（根据用户状态判断）
        UserAccount sender = userAccountMapper.selectById(senderId);
        if (sender == null) {
            throw new BusinessException("用户不存在");
        }
        if ("banned".equals(sender.getUserStatus())) {
            throw new BusinessException("账号已被封禁，无法发送消息");
        }

        // 5. 创建消息
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setMessageType(sendMessageDTO.getMessageType());
        message.setSentAt(LocalDateTime.now());
        message.setRecalled(0);

        String extJson = sendMessageDTO.getExtJson();
        String content = sendMessageDTO.getContent();

        // ========== 商品卡片 ==========
        if ("product_card".equals(sendMessageDTO.getMessageType())) {
            if (sendMessageDTO.getProductId() != null) {
                // 使用 BaseMapper 自带的 selectById 方法
                Product product = productMapper.selectById(sendMessageDTO.getProductId());
                if (product == null) {
                    throw new BusinessException("商品不存在");
                }

                // 查询商品主图：使用 BaseMapper 的 selectOne + LambdaQueryWrapper
                LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ProductImage::getProductId, sendMessageDTO.getProductId())
                        .eq(ProductImage::getIsCover, true)
                        .last("LIMIT 1");
                ProductImage productImage = prodImageMapper.selectOne(wrapper);

                String coverImageUrl = productImage != null ? productImage.getImageUrl() : "";

                // 构建 extJson
                extJson = String.format(
                        "{\"productId\":%d,\"price\":%d,\"imageUrl\":\"%s\"}",
                        product.getId(),
                        product.getSellingPrice() != null ? product.getSellingPrice().intValue() : 0,
                        coverImageUrl
                );

                // 使用商品标题作为消息内容
                content = product.getTitle();
            }
        }

        // ========== 订单卡片 ==========
        if ("order_card".equals(sendMessageDTO.getMessageType())) {
            if (sendMessageDTO.getOrderId() != null) {
                TradeOrder order = tradeOrderMapper.selectById(sendMessageDTO.getOrderId());
                if (order == null) {
                    throw new BusinessException("订单不存在");
                }

                extJson = String.format(
                        "{\"orderId\":%d,\"amount\":%d,\"status\":\"%s\"}",
                        order.getId(),
                        order.getPayAmount().intValue(),
                        order.getOrderStatus()
                );

                if (content == null) {
                    content = "订单号：" + order.getOrderNo();
                }
            }
        }

        message.setExtJson(extJson);
        message.setContent(content);

        chatMessageMapper.insert(message);

        // 5. 更新会话最后消息时间和内容
        String lastContent = content;
        if (lastContent != null && lastContent.length() > 500) {
            lastContent = lastContent.substring(0, 497) + "...";
        }
        conversationMapper.updateLastMessage(conversationId, lastContent);

        // 6. 返回VO
        return buildChatMessageVO(message);
    }

    @Override
    public List<ChatMessageVO> getMessageList(Long userId, Long conversationId, Integer pageNum, Integer pageSize) {
        // 参数校验
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 20;
        if (pageSize > 100) pageSize = 100;

        // 验证会话权限
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!isConversationParticipant(conversation, userId)) {
            throw new BusinessException("无权查看该会话");
        }

        // 分页查询（按时间正序，最新消息在最后）
        int offset = (pageNum - 1) * pageSize;
        List<ChatMessage> messages = chatMessageMapper.findByConversationId(conversationId, offset, pageSize);

        return messages.stream()
                .map(this::buildChatMessageVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recallMessage(Long senderId, Long messageId) {
        // 检查消息是否存在
        ChatMessage message = chatMessageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }

        // 检查是否是自己的消息
        if (!message.getSenderId().equals(senderId)) {
            throw new BusinessException("只能撤回自己发送的消息");
        }

        // 检查是否已撤回
        if (message.getRecalled() == 1) {
            throw new BusinessException("消息已被撤回");
        }

        // 检查是否在可撤回时间内（5分钟）
        int recallable = chatMessageMapper.checkRecallable(messageId, senderId);
        if (recallable == 0) {
            throw new BusinessException("消息发送超过5分钟，无法撤回");
        }

        // 执行撤回
        int affected = chatMessageMapper.recallMessage(messageId, senderId);
        if (affected == 0) {
            throw new BusinessException("撤回失败");
        }
    }

    // ==================== 会话相关 ====================

    @Override
    public List<ConversationVO> getConversationList(Long userId) {
        return getConversationList(userId, 1, 20);
    }

    @Override
    public List<ConversationVO> getConversationList(Long userId, Integer pageNum, Integer pageSize) {
        // 参数校验
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 20;
        if (pageSize > 100) pageSize = 100;

        int offset = (pageNum - 1) * pageSize;

        // 查询用户参与的所有会话
        List<Conversation> conversations = conversationMapper.findByUserIdWithPage(userId, offset, pageSize);

        return conversations.stream()
                .map(conv -> buildConversationVO(conv, userId))
                .collect(Collectors.toList());
    }

    @Override
    public ConversationDetailVO getConversationDetail(Long userId, Long conversationId) {
        // 验证权限
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!isConversationParticipant(conversation, userId)) {
            throw new BusinessException("无权查看该会话");
        }

        // 获取对方用户信息
        Long otherUserId = conversationMapper.getOtherParticipantId(conversationId, userId);
        UserAccount otherUser = userAccountMapper.selectById(otherUserId);
        String nickname = otherUser != null ? otherUser.getNickname() : "未知用户";
        String avatarUrl = null;
        // 可以通过 UserProfile 获取头像，这里简化处理

        // 获取消息列表（默认查询最近50条）
        List<ChatMessageVO> messages = getMessageList(userId, conversationId, 1, 50);

        // 标记对话为已读
        markConversationRead(userId, conversationId);

        return ConversationDetailVO.builder()
                .id(conversation.getId())
                .conversationType(conversation.getConversationType())
                .productId(conversation.getProductId())
                .orderId(conversation.getOrderId())
                .userId(otherUserId)
                .userNickname(nickname)
                .userAvatar(avatarUrl)
                .messages(messages)
                .build();
    }

    @Override
    @Transactional
    public void markConversationRead(Long userId, Long conversationId) {
        // 验证权限
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!isConversationParticipant(conversation, userId)) {
            throw new BusinessException("无权操作该会话");
        }

        // 更新已读记录
        readRecordMapper.updateLastReadTime(conversationId, userId, LocalDateTime.now());
    }

    @Override
    public Integer getConversationUnreadCount(Long userId, Long conversationId) {
        // 验证权限
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }
        if (!isConversationParticipant(conversation, userId)) {
            throw new BusinessException("无权查看该会话");
        }

        return chatMessageMapper.countUnreadCount(conversationId, userId);
    }

    @Override
    public Long getTotalUnreadCount(Long userId) {
        // 获取用户所有会话
        List<Conversation> conversations = conversationMapper.findByUserId(userId);

        long totalUnread = 0L;
        for (Conversation conv : conversations) {
            totalUnread += chatMessageMapper.countUnreadCount(conv.getId(), userId);
        }
        return totalUnread;
    }

    /**
     * 查找已有会话，没有则创建新的
     */
    private Long findOrCreateConversation(Long senderId, Long receiverId, Long productId, Long orderId) {
        String type = productId != null ? "product_consult" : "order_service";
        // 1. 精确匹配（同 productId/orderId）
        Conversation existing = conversationMapper.findExistingConversation(
                type, senderId, receiverId, productId, orderId);
        // 2. 宽松匹配（仅按用户对+类型，忽略 productId/orderId 差异）
        if (existing == null) {
            existing = conversationMapper.findConversationByUserPair(
                    type, senderId, receiverId);
        }
        if (existing != null) {
            return existing.getId();
        }
        // 不存在则创建
        Conversation conversation = new Conversation();
        conversation.setInitiatorId(senderId);
        conversation.setReceiverId(receiverId);
        conversation.setConversationType(productId != null ? "product_consult" : "order_service");
        conversation.setProductId(productId);
        conversation.setOrderId(orderId);
        conversation.setCreatedAt(LocalDateTime.now());
        conversationMapper.insert(conversation);
        return conversation.getId();
    }

    @Override
    @Transactional
    public ConversationVO createConversation(Long currentUserId, CreateConversationDTO dto) {
        // 验证对方用户是否存在
        UserAccount otherUser = userAccountMapper.selectById(dto.getUserId());
        if (otherUser == null) {
            throw new BusinessException("对方用户不存在");
        }

        // 不能和自己创建会话
        if (currentUserId.equals(dto.getUserId())) {
            throw new BusinessException("不能与自己创建会话");
        }

        // ========== 商品咨询场景：校验商品 ==========
        if ("product_consult".equals(dto.getConversationType())) {
            if (dto.getProductId() == null) {
                throw new BusinessException("商品咨询场景需要提供商品ID");
            }
            Product product = productMapper.selectById(dto.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
        }

        // ========== 订单售后场景：校验订单 ==========
        if ("order_service".equals(dto.getConversationType())) {
            if (dto.getOrderId() == null) {
                throw new BusinessException("订单售后场景需要提供订单ID");
            }

            // 查询订单是否存在
            TradeOrder order = tradeOrderMapper.selectById(dto.getOrderId());
            if (order == null) {
                throw new BusinessException("订单不存在");
            }

            // 验证当前用户是否与该订单相关（是买家或卖家）
            if (!order.getBuyerId().equals(currentUserId) && !order.getSellerId().equals(currentUserId)) {
                throw new BusinessException("您与该订单无关，无法创建售后会话");
            }

            // 验证订单状态是否允许发起售后
            String status = order.getOrderStatus();
            // 可售后状态：paid_pending_ship、shipped、delivered、completed
            if (!"paid_pending_ship".equals(status) && !"shipped".equals(status)
                    && !"delivered".equals(status) && !"completed".equals(status)) {
                throw new BusinessException("当前订单状态不支持发起售后，订单状态：" + status);
            }

            // 可选：验证对方用户ID是否匹配（卖家就是订单的sellerId）
            if (!dto.getUserId().equals(order.getSellerId())) {
                throw new BusinessException("对方用户不是该订单的卖家");
            }
        }

        // 检查是否已存在相同场景的会话（先精确匹配，再宽松匹配）
        Conversation existing = conversationMapper.findExistingConversation(
                dto.getConversationType(), currentUserId, dto.getUserId(),
                dto.getProductId(), dto.getOrderId());
        if (existing == null) {
            existing = conversationMapper.findConversationByUserPair(
                    dto.getConversationType(), currentUserId, dto.getUserId());
        }

        if (existing != null) {
            // 返回已有会话
            return buildConversationVO(existing, currentUserId);
        }

        // 创建新会话
        Conversation conversation = new Conversation();
        conversation.setConversationType(dto.getConversationType());
        conversation.setProductId(dto.getProductId());
        conversation.setOrderId(dto.getOrderId());
        conversation.setInitiatorId(currentUserId);
        conversation.setReceiverId(dto.getUserId());
        conversation.setCreatedAt(LocalDateTime.now());

        conversationMapper.insert(conversation);

        return buildConversationVO(conversation, currentUserId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 判断用户是否是会话的参与者
     */
    private boolean isConversationParticipant(Conversation conversation, Long userId) {
        return conversation.getInitiatorId().equals(userId) || conversation.getReceiverId().equals(userId);
    }

    /**
     * 构建 ChatMessageVO
     */
    private ChatMessageVO buildChatMessageVO(ChatMessage message) {
        UserAccount sender = userAccountMapper.selectById(message.getSenderId());

        return ChatMessageVO.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .senderNickname(sender != null ? sender.getNickname() : "未知用户")
                .messageType(message.getMessageType())
                .content(message.getRecalled() == 1 ? "该消息已被撤回" : message.getContent())
                .extJson(message.getExtJson())
                .sentAt(message.getSentAt())
                .recalled(message.getRecalled() == 1)
                .build();
    }

    /**
     * 构建 ConversationVO
     */
    private ConversationVO buildConversationVO(Conversation conversation, Long currentUserId) {
        // 获取对方用户信息
        Long otherUserId = conversationMapper.getOtherParticipantId(conversation.getId(), currentUserId);
        UserAccount otherUser = userAccountMapper.selectById(otherUserId);

        // 获取最后一条消息
        ChatMessage lastMessage = chatMessageMapper.getLastMessage(conversation.getId());
        String lastMessageContent = null;
        LocalDateTime lastMessageAt = conversation.getLastMessageAt();

        if (lastMessage != null) {
            lastMessageContent = lastMessage.getRecalled() == 1 ? "该消息已被撤回" : lastMessage.getContent();
            lastMessageAt = lastMessage.getSentAt();
        }

        // 获取未读消息数
        Integer unreadCount = chatMessageMapper.countUnreadCount(conversation.getId(), currentUserId);

        return ConversationVO.builder()
                .id(conversation.getId())
                .conversationType(conversation.getConversationType())
                .productId(conversation.getProductId())
                .orderId(conversation.getOrderId())
                .userId(otherUserId)
                .userNickname(otherUser != null ? otherUser.getNickname() : "未知用户")
                .lastMessageContent(lastMessageContent)
                .lastMessageAt(lastMessageAt)
                .createdAt(conversation.getCreatedAt())
                .unreadCount(unreadCount)
                .build();
    }

    /**
     * 构建商品卡片消息的扩展信息
     * 可选功能：发送商品卡片时自动填充商品信息

     private String buildProductCardExtJson(Long productId) {
     if (productId == null) {
     return null;
     }
     Product product = productMapper.selectById(productId);
     if (product == null) {
     return null;
     }

     // 构建商品卡片JSON
     return String.format(
     "{\"productId\":%d,\"title\":\"%s\",\"price\":%d,\"imageUrl\":\"%s\"}",
     product.getId(),
     product.getTitle(),
     product.getPrice(),
     product.getImageUrl() != null ? product.getImageUrl() : ""
     );
     }*/
}