package com.secondhand.marketplace.backend.modules.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageDTO {

    @Schema(description = "会话ID（可选）。传了就走已有会话；不传则自动查已有会话或新建")
    private Long conversationId;

    @Schema(description = "对方用户ID。conversationId 未传时必填，用于查找或创建会话")
    private Long receiverId;

    private Long productId;

    private Long orderId;

    @NotBlank(message = "消息类型不能为空")
    private String messageType; // text, image, system, order_card, product_card

    //@NotBlank(message = "消息内容不能为空")
    private String content;

    private String extJson; // 扩展信息JSON（商品卡片、订单卡片等）
}