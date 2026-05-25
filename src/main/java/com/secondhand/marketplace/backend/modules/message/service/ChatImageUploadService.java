package com.secondhand.marketplace.backend.modules.message.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ChatImageUploadService {

    /**
     * 上传聊天图片
     * @param file 图片文件
     * @param userId 上传用户ID
     * @return 包含 url 和 extJson 的 Map
     */
    Map<String, Object> uploadChatImage(MultipartFile file, Long userId);
}