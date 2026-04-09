package com.secondhand.marketplace.backend.modules.forum.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统用户信息")
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "密码（加密存储）")
    private String password;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "个人简介")
    private String bio;
    
    @Schema(description = "角色：user/admin/super_admin")
    private String role;
    
    @Schema(description = "状态：active/banned/pending")
    private String status;
    
    @Schema(description = "是否禁言（0-否，1-是）")
    private Integer isMuted;
    
    @Schema(description = "禁言到期时间")
    private LocalDateTime muteExpireAt;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "身份证号")
    private String idCardNo;
    
    @Schema(description = "身份证正面照URL")
    private String idCardFront;
    
    @Schema(description = "身份证反面照URL")
    private String idCardBack;
    
    @Schema(description = "实名认证状态：unverified/pending/verified/failed")
    private String realNameStatus;
    
    @Schema(description = "信用分（0-100）")
    private Integer creditScore;
    
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;
    
    @Schema(description = "最后登录IP")
    private String lastLoginIp;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}