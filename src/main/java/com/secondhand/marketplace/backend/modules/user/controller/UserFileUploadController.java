package com.secondhand.marketplace.backend.modules.user.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.modules.user.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;





/**文件上传接口**/
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UserFileUploadController {

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

    /**
     * 上传聊天图片
     * POST /api/upload/chat-image
     */
    @PostMapping("/chat-image")
    public CommonResult<Map<String, Object>> uploadChatImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".jpg") && !originalFilename.endsWith(".jpeg")
                    && !originalFilename.endsWith(".png") && !originalFilename.endsWith(".gif") && !originalFilename.endsWith(".bmp"))) {
                return CommonResult.error("只支持 JPG、PNG、GIF、BMP 格式的图片");
            }

            // 2. 校验文件大小（限制 5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return CommonResult.error("图片大小不能超过 5MB");
            }

            // 3. 上传文件到 MinIO
            String imageUrl = fileUploadService.uploadImage(file, "chat-images");

            // 4. 获取图片信息
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            long size = file.getSize();
            String filename = originalFilename;

            // 5. 构建 extJson
            String extJson = String.format(
                    "{\"width\":%d,\"height\":%d,\"size\":%d,\"filename\":\"%s\"}",
                    width, height, size, filename
            );

            // 6. 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("url", imageUrl);
            result.put("extJson", extJson);

            return CommonResult.success(result);
        } catch (IOException e) {
            return CommonResult.error("图片上传失败");
        }
    }
    
}
