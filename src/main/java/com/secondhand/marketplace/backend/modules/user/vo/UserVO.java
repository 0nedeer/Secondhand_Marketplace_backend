package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatarUrl;
    private String userStatus;
    private LocalDateTime lastLoginAt;
    private LocalDateTime registeredAt;
    private Integer isAdmin;

    public String getRole() {
        return isAdmin != null && isAdmin == 1 ? "ADMIN" : "USER";
    }
}