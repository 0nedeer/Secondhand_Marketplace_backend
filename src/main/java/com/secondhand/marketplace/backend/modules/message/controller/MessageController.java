package com.secondhand.marketplace.backend.modules.message.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.message.dto.SendMessageDTO;
import com.secondhand.marketplace.backend.modules.message.service.MessageService;
import com.secondhand.marketplace.backend.modules.message.vo.ChatMessageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息管理接口
 * 所有接口都需要登录
 */
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@Validated
public class MessageController {

    private final MessageService messageService;

    /**
     * 发送消息
     * POST /api/message/send
     */
    @PostMapping("/send")
    public CommonResult<ChatMessageVO> sendMessage(@Valid @RequestBody SendMessageDTO sendMessageDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        ChatMessageVO message = messageService.sendMessage(userId, sendMessageDTO);
        return CommonResult.success(message);
    }

    /**
     * 获取消息列表（按会话ID）
     * GET /api/message/list?conversationId=1&pageNum=1&pageSize=20
     */
    @GetMapping("/list")
    public CommonResult<List<ChatMessageVO>> getMessageList(
            @RequestParam Long conversationId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        List<ChatMessageVO> messages = messageService.getMessageList(userId, conversationId, pageNum, pageSize);
        return CommonResult.success(messages);
    }

    /**
     * 撤回消息
     * POST /api/message/recall/{messageId}
     */
    @PostMapping("/recall/{messageId}")
    public CommonResult<Void> recallMessage(@PathVariable Long messageId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        messageService.recallMessage(userId, messageId);
        return CommonResult.success();
    }
}