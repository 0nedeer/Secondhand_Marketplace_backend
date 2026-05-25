package com.secondhand.marketplace.backend.modules.message.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.common.util.MinioUtil;
import com.secondhand.marketplace.backend.modules.message.service.ChatImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatImageUploadServiceImpl implements ChatImageUploadService {

    private final MinioUtil minioUtil;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.use-presigned:false}")
    private boolean usePresigned;

    @Override
    public Map<String, Object> uploadChatImage(MultipartFile file, Long userId) {
        // 1. 校验图片
        validateImage(file);

        // 2. 获取图片信息（宽高）
        Map<String, Object> imageInfo = getImageInfo(file);

        // 3. 上传到 MinIO（存储在 chat-images 目录下）
        String objectName;
        try {
            objectName = minioUtil.uploadFile(file, "chat-images");
        } catch (Exception e) {
            log.error("聊天图片上传失败", e);
            throw new BusinessException("图片上传失败：" + e.getMessage());
        }

        // 4. 构建访问 URL
        String fileUrl = buildUrl(objectName);

        // 5. 构建 extJson 扩展信息
        Map<String, Object> extJson = new HashMap<>();
        extJson.put("width", imageInfo.getOrDefault("width", 0));
        extJson.put("height", imageInfo.getOrDefault("height", 0));
        extJson.put("size", file.getSize());
        extJson.put("filename", file.getOriginalFilename());

        // 6. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("extJson", extJson);

        log.info("用户 {} 上传聊天图片成功: {}", userId, fileUrl);

        return result;
    }

    /**
     * 校验图片文件（复用 FileUploadServiceImpl 的逻辑）
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件（JPG/PNG/GIF/WEBP）");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!ext.matches("jpg|jpeg|png|gif|webp")) {
                throw new BusinessException("不支持的图片格式");
            }
        }
    }

    /**
     * 获取图片的宽度和高度
     */
    private Map<String, Object> getImageInfo(MultipartFile file) {
        Map<String, Object> info = new HashMap<>();
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                info.put("width", image.getWidth());
                info.put("height", image.getHeight());
            } else {
                info.put("width", 0);
                info.put("height", 0);
            }
        } catch (IOException e) {
            log.warn("获取图片信息失败: {}", e.getMessage());
            info.put("width", 0);
            info.put("height", 0);
        }
        return info;
    }

    /**
     * 根据配置构建 URL：public用直链，private用预签名
     */
    private String buildUrl(String objectName) {
        try {
            if (usePresigned) {
                return minioUtil.getPresignedUrl(objectName);
            }
            return endpoint + "/" + bucketName + "/" + objectName;
        } catch (Exception e) {
            log.error("构建图片URL失败", e);
            throw new BusinessException("获取图片访问地址失败");
        }
    }
}