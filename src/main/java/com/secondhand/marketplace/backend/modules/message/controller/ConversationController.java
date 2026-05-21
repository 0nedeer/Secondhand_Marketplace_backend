package com.secondhand.marketplace.backend.modules.message.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.message.dto.CreateConversationDTO;
import com.secondhand.marketplace.backend.modules.message.service.MessageService;
import com.secondhand.marketplace.backend.modules.message.vo.ConversationDetailVO;
import com.secondhand.marketplace.backend.modules.message.vo.ConversationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会话管理接口
 * 所有接口都需要登录
 */
@RestController
@RequestMapping("/api/conversation")
@RequiredArgsConstructor
@Validated
public class ConversationController {

    private final MessageService messageService;

    /**
     * 获取会话列表
     * GET /api/conversation/list?pageNum=1&pageSize=20
     */
    @GetMapping("/list")
    public CommonResult<List<ConversationVO>> getConversationList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        List<ConversationVO> conversations = messageService.getConversationList(userId, pageNum, pageSize);
        return CommonResult.success(conversations);
    }

    /**
     * 获取单个会话详情（包含消息列表）
     * GET /api/conversation/{conversationId}
     */
    @GetMapping("/{conversationId}")
    public CommonResult<ConversationDetailVO> getConversationDetail(@PathVariable Long conversationId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        ConversationDetailVO detail = messageService.getConversationDetail(userId, conversationId);
        return CommonResult.success(detail);
    }

    /**
     * 标记会话已读
     * PUT /api/conversation/{conversationId}/read
     */
    @PutMapping("/{conversationId}/read")
    public CommonResult<Void> markConversationRead(@PathVariable Long conversationId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        messageService.markConversationRead(userId, conversationId);
        return CommonResult.success();
    }

    /**
     * 获取会话未读消息数
     * GET /api/conversation/{conversationId}/unread-count
     */
    @GetMapping("/{conversationId}/unread-count")
    public CommonResult<Integer> getConversationUnreadCount(@PathVariable Long conversationId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        Integer unreadCount = messageService.getConversationUnreadCount(userId, conversationId);
        return CommonResult.success(unreadCount);
    }

    /**
     * 获取总未读消息数（所有会话）
     * GET /api/conversation/unread/total
     */
    @GetMapping("/unread/total")
    public CommonResult<Long> getTotalUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        Long totalUnread = messageService.getTotalUnreadCount(userId);
        return CommonResult.success(totalUnread);
    }

    /**
     * 创建新会话
     * POST /api/conversation/create
     */
    @PostMapping("/create")
    public CommonResult<ConversationVO> createConversation(@Valid @RequestBody CreateConversationDTO createDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        ConversationVO conversation = messageService.createConversation(userId, createDTO);
        return CommonResult.success(conversation);
    }
}