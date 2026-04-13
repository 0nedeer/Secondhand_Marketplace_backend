package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UploadReviewImageRequest {

    @NotBlank(message = "图片地址不能为空")
    @Size(max = 500, message = "图片地址长度不能超过500")
    private String imageUrl;

    private Integer sortNo;
}
