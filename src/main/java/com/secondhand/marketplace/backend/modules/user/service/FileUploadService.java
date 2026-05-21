package com.secondhand.marketplace.backend.modules.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    /**
     * 上传头像
     * @param file 图片文件
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 上传通用图片
     * @param file 图片文件
     * @param folder 存放目录
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file, String folder);
}
