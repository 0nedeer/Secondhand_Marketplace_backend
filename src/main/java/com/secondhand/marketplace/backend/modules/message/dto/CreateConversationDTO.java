package com.secondhand.marketplace.backend.modules.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateConversationDTO {

    @NotBlank(message = "会话类型不能为空")
    private String conversationType; // product_consult, order_service, system

    private Long productId;

    private Long orderId;

    @NotNull(message = "对方用户ID不能为空")
    private Long userId;
}