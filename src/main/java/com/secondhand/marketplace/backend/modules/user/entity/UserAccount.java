package com.secondhand.marketplace.backend.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_account")
public class UserAccount {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String passwordHash;

    private Integer canBuy;
    private Integer canSell;
    private Integer isAdmin;

    private String userStatus;

    private LocalDateTime lastLoginAt;
    private LocalDateTime registeredAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}