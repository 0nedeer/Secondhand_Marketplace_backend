package com.secondhand.marketplace.backend.modules.aftersale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReplyReviewRequest {

    @NotBlank(message = "回复内容不能为空")
    @Size(max = 1000, message = "回复内容长度不能超过1000")
    private String sellerReply;
}
