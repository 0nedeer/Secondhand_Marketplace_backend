package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserItemVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private Boolean canBuy;
    private Boolean canSell;
    private Boolean isAdmin;
    private String userStatus;
    private LocalDateTime lastLoginAt;
    private LocalDateTime registeredAt;
}
