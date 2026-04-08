package com.secondhand.marketplace.backend.modules.user.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员-用户管理")
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @ApiOperation("管理员封禁用户")
    @PutMapping("/ban/{id}")
    public CommonResult<Void> banUser(@PathVariable Long id) {
        Long adminId = UserContext.getCurrentUserId();
        userService.banUser(adminId, id);
        return CommonResult.success();
    }

    @ApiOperation("管理员解封用户")
    @PutMapping("/unban/{id}")
    public CommonResult<Void> unbanUser(@PathVariable Long id) {
        Long adminId = UserContext.getCurrentUserId();
        userService.unbanUser(adminId, id);
        return CommonResult.success();
    }

    @ApiOperation("开启/关闭买家权限")
    @PutMapping("/toggle-can-buy")
    public CommonResult<Void> toggleCanBuy(@RequestParam Long userId,
                                           @RequestParam Boolean canBuy) {
        userService.toggleCanBuy(userId, canBuy);
        return CommonResult.success();
    }

    @ApiOperation("开启/关闭卖家权限")
    @PutMapping("/toggle-can-sell")
    public CommonResult<Void> toggleCanSell(@RequestParam Long userId,
                                            @RequestParam Boolean canSell) {
        userService.toggleCanSell(userId, canSell);
        return CommonResult.success();
    }
}