package com.secondhand.marketplace.backend.modules.user.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.modules.user.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**文件上传接口**/
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 上传头像
     * POST /api/upload/avatar
     * Content-Type: multipart/form-data
     * 参数名: file
     */
    @PostMapping("/avatar")
    public CommonResult uploadAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = fileUploadService.uploadAvatar(file);
        return CommonResult.success(avatarUrl);
    }

    
}
