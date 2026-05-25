package com.secondhand.marketplace.backend.modules.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PublishNoticeDTO {

    @NotBlank(message = "通知类型不能为空")
    private String noticeType; // system, order, after_sale, promotion, forum

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "目标范围不能为空")
    private String targetScope; // all, role, user

    private String targetRoleCode; // 当target_scope=role时使用

    private Long targetUserId; // 当target_scope=user时使用
}