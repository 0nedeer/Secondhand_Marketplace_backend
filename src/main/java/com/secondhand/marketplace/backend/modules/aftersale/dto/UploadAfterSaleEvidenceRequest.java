package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UploadAfterSaleEvidenceRequest {

    @NotBlank(message = "凭证类型不能为空")
    private String evidenceType;

    @Size(max = 500, message = "凭证链接长度不能超过500")
    private String contentUrl;

    @Size(max = 1000, message = "凭证文本长度不能超过1000")
    private String contentText;
}
