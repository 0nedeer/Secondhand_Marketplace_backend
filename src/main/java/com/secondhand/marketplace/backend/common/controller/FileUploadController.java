package com.secondhand.marketplace.backend.common.controller;

import com.secondhand.marketplace.backend.common.util.MinioUtil;
import com.secondhand.marketplace.backend.modules.user.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common")
@Tag(name = "通用接口", description = "通用功能接口")
public class FileUploadController {

    private final MinioUtil minioUtil;

    @Operation(summary = "上传文件到MinIO", description = "上传文件到MinIO并返回文件路径和访问URL")
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 上传文件到MinIO
            String objectName = minioUtil.uploadFile(file, "test");
            
            // 获取文件访问URL
            String fileUrl = minioUtil.getPresignedUrl(objectName);
            
            // 构建响应
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "文件上传成功");
            response.put("data", Map.of(
                    "objectName", objectName,
                    "fileUrl", fileUrl,
                    "fileName", file.getOriginalFilename(),
                    "fileSize", file.getSize()
            ));
            
            log.info("文件上传成功: {}", objectName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "文件上传失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
