package com.secondhand.marketplace.backend.modules.forum.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户基本信息")
public class UserInfoVO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "个人简介")
    private String bio;
    
    @Schema(description = "信用分")
    private Integer creditScore;
    
    @Schema(description = "角色：user/admin/super_admin")
    private String role;
    
    @Schema(description = "状态：active/banned/pending")
    private String status;
    
    @Schema(description = "是否被当前用户关注")
    private Boolean isFollowed;
    
    @Schema(description = "注册时间")
    private LocalDateTime createdAt;
}