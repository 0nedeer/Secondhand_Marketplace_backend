package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPermissionsVO {
    private Boolean canBuy;
    private Boolean canSell;
    private Boolean isAdmin;
}