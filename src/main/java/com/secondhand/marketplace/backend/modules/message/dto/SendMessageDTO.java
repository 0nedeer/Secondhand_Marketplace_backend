package com.secondhand.marketplace.backend.modules.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageDTO {

    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    private Long productId;

    private Long orderId;

    @NotBlank(message = "消息类型不能为空")
    private String messageType; // text, image, system, order_card, product_card

    //@NotBlank(message = "消息内容不能为空")
    private String content;

    private String extJson; // 扩展信息JSON（商品卡片、订单卡片等）
}