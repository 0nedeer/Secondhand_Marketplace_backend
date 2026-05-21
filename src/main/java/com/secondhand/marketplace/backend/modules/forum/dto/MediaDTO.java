package com.secondhand.marketplace.backend.modules.forum.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "媒体文件DTO")
public class MediaDTO {
    
    @Schema(description = "媒体类型：image/video", example = "image")
    private String type;
    
    @Schema(description = "媒体URL", example = "http://example.com/image.jpg")
    private String url;
}