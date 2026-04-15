package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {
    private String token;
    private UserVO userInfo;
}