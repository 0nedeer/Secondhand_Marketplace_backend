package com.secondhand.marketplace.backend.modules.user.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.user.dto.*;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import com.secondhand.marketplace.backend.modules.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public CommonResult<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return CommonResult.success();
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public CommonResult<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return CommonResult.success(userService.login(loginDTO));
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public CommonResult<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        userService.logout(token);
        return CommonResult.success();
    }

    @ApiOperation("忘记密码")
    @PostMapping("/forgot-password")
    public CommonResult<Void> forgotPassword(@RequestParam String account) {
        userService.forgotPassword(account);
        return CommonResult.success();
    }

    @ApiOperation("重置密码")
    @PostMapping("/reset-password")
    public CommonResult<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return CommonResult.success();
    }

    @ApiOperation("修改登录密码")
    @PutMapping("/change-password")
    public CommonResult<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.changePassword(userId, changePasswordDTO);
        return CommonResult.success();
    }

    @ApiOperation("绑定手机号")
    @PutMapping("/bind-phone")
    public CommonResult<Void> bindPhone(@RequestParam String phone,
                                        @RequestParam String verifyCode) {
        Long userId = UserContext.getCurrentUserId();
        userService.bindPhone(userId, phone, verifyCode);
        return CommonResult.success();
    }

    @ApiOperation("绑定邮箱")
    @PutMapping("/bind-email")
    public CommonResult<Void> bindEmail(@RequestParam String email,
                                        @RequestParam String verifyCode) {
        Long userId = UserContext.getCurrentUserId();
        userService.bindEmail(userId, email, verifyCode);
        return CommonResult.success();
    }

    @ApiOperation("解绑手机号")
    @PutMapping("/unbind-phone")
    public CommonResult<Void> unbindPhone() {
        Long userId = UserContext.getCurrentUserId();
        userService.unbindPhone(userId);
        return CommonResult.success();
    }

    @ApiOperation("解绑邮箱")
    @PutMapping("/unbind-email")
    public CommonResult<Void> unbindEmail() {
        Long userId = UserContext.getCurrentUserId();
        userService.unbindEmail(userId);
        return CommonResult.success();
    }

    @ApiOperation("查询用户当前状态")
    @GetMapping("/status")
    public CommonResult<String> getUserStatus() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserStatus(userId));
    }

    @ApiOperation("查询用户权限")
    @GetMapping("/permissions")
    public CommonResult<UserPermissionsVO> getUserPermissions() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserPermissions(userId));
    }

    @ApiOperation("获取用户个人信息")
    @GetMapping("/profile")
    public CommonResult<UserProfileVO> getUserProfile() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserProfile(userId));
    }

    @ApiOperation("更新用户个人信息")
    @PutMapping("/profile")
    public CommonResult<Void> updateUserProfile(@RequestBody UpdateProfileDTO updateProfileDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateUserProfile(userId, updateProfileDTO);
        return CommonResult.success();
    }

    @ApiOperation("查询用户信用分/好评率")
    @GetMapping("/credit-score")
    public CommonResult<UserProfileVO> getCreditScore() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserProfile(userId));
    }

    @ApiOperation("获取用户数据统计")
    @GetMapping("/stats")
    public CommonResult<UserStatsVO> getUserStats() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserStats(userId));
    }

    @ApiOperation("获取用户所有收货地址列表")
    @GetMapping("/addresses")
    public CommonResult<List<AddressVO>> getUserAddresses() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserAddresses(userId));
    }

    @ApiOperation("新增收货地址")
    @PostMapping("/address")
    public CommonResult<Void> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.addAddress(userId, addressDTO);
        return CommonResult.success();
    }

    @ApiOperation("更新某个收货地址")
    @PutMapping("/address/{id}")
    public CommonResult<Void> updateAddress(@PathVariable Long id,
                                            @Valid @RequestBody AddressDTO addressDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateAddress(userId, id, addressDTO);
        return CommonResult.success();
    }

    @ApiOperation("删除某个收货地址")
    @DeleteMapping("/address/{id}")
    public CommonResult<Void> deleteAddress(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        userService.deleteAddress(userId, id);
        return CommonResult.success();
    }

    @ApiOperation("设置某个地址为默认收货地址")
    @PutMapping("/address/{id}/default")
    public CommonResult<Void> setDefaultAddress(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        userService.setDefaultAddress(userId, id);
        return CommonResult.success();
    }

    @ApiOperation("提交实名认证")
    @PostMapping("/verification/realname")
    public CommonResult<Void> submitRealNameVerification(@Valid @RequestBody RealNameVerificationDTO verificationDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.submitRealNameVerification(userId, verificationDTO);
        return CommonResult.success();
    }

    @ApiOperation("查询用户各种认证状态")
    @GetMapping("/verification/status")
    public CommonResult<List<VerificationStatusVO>> getVerificationStatus() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getVerificationStatus(userId));
    }

    @ApiOperation("获取用户收藏的商品列表")
    @GetMapping("/favorites")
    public CommonResult<List<Long>> getUserFavorites() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserFavorites(userId));
    }

    @ApiOperation("收藏某个商品")
    @PostMapping("/favorite/{product_id}")
    public CommonResult<Void> favoriteProduct(@PathVariable("product_id") Long productId) {
        Long userId = UserContext.getCurrentUserId();
        userService.favoriteProduct(userId, productId);
        return CommonResult.success();
    }

    @ApiOperation("取消收藏某个商品")
    @DeleteMapping("/favorite/{product_id}")
    public CommonResult<Void> unfavoriteProduct(@PathVariable("product_id") Long productId) {
        Long userId = UserContext.getCurrentUserId();
        userService.unfavoriteProduct(userId, productId);
        return CommonResult.success();
    }

    @ApiOperation("获取我关注的卖家列表")
    @GetMapping("/follows")
    public CommonResult<List<Long>> getUserFollows() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserFollows(userId));
    }

    @ApiOperation("关注某个卖家")
    @PostMapping("/follow/{seller_id}")
    public CommonResult<Void> followSeller(@PathVariable("seller_id") Long sellerId) {
        Long userId = UserContext.getCurrentUserId();
        userService.followSeller(userId, sellerId);
        return CommonResult.success();
    }

    @ApiOperation("取消关注某个卖家")
    @DeleteMapping("/follow/{seller_id}")
    public CommonResult<Void> unfollowSeller(@PathVariable("seller_id") Long sellerId) {
        Long userId = UserContext.getCurrentUserId();
        userService.unfollowSeller(userId, sellerId);
        return CommonResult.success();
    }

    @ApiOperation("获取关注我的粉丝列表")
    @GetMapping("/followers")
    public CommonResult<List<Long>> getUserFollowers() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserFollowers(userId));
    }

    //手机验证码部分
    /*
     //发送短信验证码

    @PostMapping("/sms/send-code")
    public Map<String, Object> sendSmsCode(@Valid @RequestBody SendSmsCodeDTO sendSmsCodeDTO) {
        Map<String, Object> result = new HashMap<>();
        try {
            userService.sendSmsCode(sendSmsCodeDTO.getPhone());
            result.put("code", 200);
            result.put("message", "验证码发送成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }


     //手机验证码登录

    @PostMapping("/sms-login")
    public Map<String, Object> smsLogin(@Valid @RequestBody SmsLoginDTO smsLoginDTO) {
        Map<String, Object> result = new HashMap<>();
        try {
            LoginVO loginVO = userService.smsLogin(smsLoginDTO);
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", loginVO);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }*/
}