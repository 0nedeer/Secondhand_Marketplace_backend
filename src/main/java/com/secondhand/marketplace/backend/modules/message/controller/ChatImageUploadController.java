package com.secondhand.marketplace.backend.modules.message.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.message.service.ChatImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class ChatImageUploadController {

    private final ChatImageUploadService chatImageUploadService;

    /**
     * 上传聊天图片
     * POST /api/upload/chat-image
     * Content-Type: multipart/form-data
     * 参数名: file
     */
    @PostMapping("/chat-image")
    public CommonResult<Map<String, Object>> uploadChatImage(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }

        Map<String, Object> result = chatImageUploadService.uploadChatImage(file, userId);
        return CommonResult.success(result);
    }
}