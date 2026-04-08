package com.secondhand.marketplace.backend.modules.user.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;


    @PutMapping("/ban/{id}")
    public CommonResult<Void> banUser(@PathVariable Long id) {
        Long adminId = UserContext.getCurrentUserId();
        userService.banUser(adminId, id);
        return CommonResult.success();
    }


    @PutMapping("/unban/{id}")
    public CommonResult<Void> unbanUser(@PathVariable Long id) {
        Long adminId = UserContext.getCurrentUserId();
        userService.unbanUser(adminId, id);
        return CommonResult.success();
    }


    @PutMapping("/toggle-can-buy")
    public CommonResult<Void> toggleCanBuy(@RequestParam Long userId,
                                           @RequestParam Boolean canBuy) {
        userService.toggleCanBuy(userId, canBuy);
        return CommonResult.success();
    }


    @PutMapping("/toggle-can-sell")
    public CommonResult<Void> toggleCanSell(@RequestParam Long userId,
                                            @RequestParam Boolean canSell) {
        userService.toggleCanSell(userId, canSell);
        return CommonResult.success();
    }
}