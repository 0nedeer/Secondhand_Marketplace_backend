package com.secondhand.marketplace.backend.modules.user.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.user.dto.*;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import com.secondhand.marketplace.backend.modules.user.vo.*;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

//用户模块
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //("用户注册")
    @PostMapping("/register")
    public CommonResult<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return CommonResult.success();
    }

    //("用户登录")
    @PostMapping("/login")
    public CommonResult<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return CommonResult.success(userService.login(loginDTO));
    }

    //("用户登出")
    @PostMapping("/logout")
    public CommonResult<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        userService.logout(token);
        return CommonResult.success();
    }

    //("忘记密码")
    @PostMapping("/forgot-password")
    public CommonResult<Void> forgotPassword(@RequestParam String account) {
        userService.forgotPassword(account);
        return CommonResult.success();
    }

    //("重置密码")
    @PostMapping("/reset-password")
    public CommonResult<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO);
        return CommonResult.success();
    }

    //("修改登录密码")
    @PutMapping("/change-password")
    public CommonResult<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.changePassword(userId, changePasswordDTO);
        return CommonResult.success();
    }

    //("绑定手机号")
    @PutMapping("/bind-phone")
    public CommonResult<Void> bindPhone(@RequestParam String phone,
                                        @RequestParam String verifyCode) {
        Long userId = UserContext.getCurrentUserId();
        userService.bindPhone(userId, phone, verifyCode);
        return CommonResult.success();
    }

    //("绑定邮箱")
    @PutMapping("/bind-email")
    public CommonResult<Void> bindEmail(@RequestParam String email,
                                        @RequestParam String verifyCode) {
        Long userId = UserContext.getCurrentUserId();
        userService.bindEmail(userId, email, verifyCode);
        return CommonResult.success();
    }

    //("解绑手机号")
    @PutMapping("/unbind-phone")
    public CommonResult<Void> unbindPhone() {
        Long userId = UserContext.getCurrentUserId();
        userService.unbindPhone(userId);
        return CommonResult.success();
    }

    //("解绑邮箱")
    @PutMapping("/unbind-email")
    public CommonResult<Void> unbindEmail() {
        Long userId = UserContext.getCurrentUserId();
        userService.unbindEmail(userId);
        return CommonResult.success();
    }

    //("查询用户当前状态")
    @GetMapping("/status")
    public CommonResult<String> getUserStatus() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserStatus(userId));
    }

    //("查询用户权限")
    @GetMapping("/permissions")
    public CommonResult<UserPermissionsVO> getUserPermissions() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserPermissions(userId));
    }

    //("获取用户个人信息")
    @GetMapping("/profile")
    public CommonResult<UserProfileVO> getUserProfile() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserProfile(userId));
    }

    //("更新用户个人信息")
    @PutMapping("/profile")
    public CommonResult<Void> updateUserProfile(@RequestBody UpdateProfileDTO updateProfileDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateUserProfile(userId, updateProfileDTO);
        return CommonResult.success();
    }

    //("查询用户信用分/好评率")
    @GetMapping("/credit-score")
    public CommonResult<UserProfileVO> getCreditScore() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserProfile(userId));
    }

    //("获取用户数据统计")
    @GetMapping("/stats")
    public CommonResult<UserStatsVO> getUserStats() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserStats(userId));
    }

    //("获取用户所有收货地址列表")
    @GetMapping("/addresses")
    public CommonResult<List<AddressVO>> getUserAddresses() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserAddresses(userId));
    }

    //("新增收货地址")
    @PostMapping("/address")
    public CommonResult<Void> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.addAddress(userId, addressDTO);
        return CommonResult.success();
    }

    //("更新某个收货地址")
    @PutMapping("/address/{id}")
    public CommonResult<Void> updateAddress(@PathVariable Long id,
                                            @Valid @RequestBody AddressDTO addressDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.updateAddress(userId, id, addressDTO);
        return CommonResult.success();
    }

    //("删除某个收货地址")
    @DeleteMapping("/address/{id}")
    public CommonResult<Void> deleteAddress(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        userService.deleteAddress(userId, id);
        return CommonResult.success();
    }

    //("设置某个地址为默认收货地址")
    @PutMapping("/address/{id}/default")
    public CommonResult<Void> setDefaultAddress(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        userService.setDefaultAddress(userId, id);
        return CommonResult.success();
    }

    //("提交实名认证")
    @PostMapping("/verification/realname")
    public CommonResult<Void> submitRealNameVerification(@Valid @RequestBody RealNameVerificationDTO verificationDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.submitRealNameVerification(userId, verificationDTO);
        return CommonResult.success();
    }

    //("查询用户各种认证状态")
    @GetMapping("/verification/status")
    public CommonResult<List<VerificationStatusVO>> getVerificationStatus() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getVerificationStatus(userId));
    }

    //("获取用户收藏的商品列表")
    @GetMapping("/favorites")
    public CommonResult<List<Long>> getUserFavorites() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserFavorites(userId));
    }

    //("收藏某个商品")
    @PostMapping("/favorite/{product_id}")
    public CommonResult<Void> favoriteProduct(@PathVariable("product_id") Long productId) {
        Long userId = UserContext.getCurrentUserId();
        userService.favoriteProduct(userId, productId);
        return CommonResult.success();
    }

    //("取消收藏某个商品")
    @DeleteMapping("/favorite/{product_id}")
    public CommonResult<Void> unfavoriteProduct(@PathVariable("product_id") Long productId) {
        Long userId = UserContext.getCurrentUserId();
        userService.unfavoriteProduct(userId, productId);
        return CommonResult.success();
    }

    //("获取我关注的卖家列表")
    @GetMapping("/follows")
    public CommonResult<List<Long>> getUserFollows() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserFollows(userId));
    }

    //("关注某个卖家")
    @PostMapping("/follow/{seller_id}")
    public CommonResult<Void> followSeller(@PathVariable("seller_id") Long sellerId) {
        Long userId = UserContext.getCurrentUserId();
        userService.followSeller(userId, sellerId);
        return CommonResult.success();
    }

    //("取消关注某个卖家")
    @DeleteMapping("/follow/{seller_id}")
    public CommonResult<Void> unfollowSeller(@PathVariable("seller_id") Long sellerId) {
        Long userId = UserContext.getCurrentUserId();
        userService.unfollowSeller(userId, sellerId);
        return CommonResult.success();
    }

    //("获取关注我的粉丝列表")
    @GetMapping("/followers")
    public CommonResult<List<Long>> getUserFollowers() {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getUserFollowers(userId));
    }

    // ========== 认证管理补充接口 ==========

    //("查询某条认证记录详情")
    @GetMapping("/verification/detail/{id}")
    public CommonResult<VerificationDetailVO> getVerificationDetail(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        return CommonResult.success(userService.getVerificationDetail(userId, id));
    }

    //("重新提交认证")
    @PutMapping("/verification/{id}/resubmit")
    public CommonResult<Void> resubmitVerification(@PathVariable Long id,
                                                   @Valid @RequestBody RealNameVerificationDTO verificationDTO) {
        Long userId = UserContext.getCurrentUserId();
        userService.resubmitVerification(userId, id, verificationDTO);
        return CommonResult.success();
    }

// ========== 信誉相关接口 ==========

    //("获取卖家信誉快照")
    @GetMapping("/reputation/{sellerId}")
    public CommonResult<ReputationVO> getSellerReputation(@PathVariable Long sellerId) {
        return CommonResult.success(userService.getSellerReputation(sellerId));
    }

    //("获取卖家信誉历史趋势")
    @GetMapping("/reputation/{sellerId}/history")
    public CommonResult<List<ReputationHistoryVO>> getReputationHistory(
            @PathVariable Long sellerId,
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        return CommonResult.success(userService.getReputationHistory(sellerId, days));
    }

// ========== 手机验证码登录接口（模拟版）==========

    //("发送短信验证码（模拟：控制台打印）")
    @PostMapping("/sms/send-code")
    public CommonResult<String> sendSmsCode(@Valid @RequestBody SendSmsCodeDTO sendSmsCodeDTO) {
        userService.sendSmsCode(sendSmsCodeDTO.getPhone());
        return CommonResult.success("验证码已发送，请在控制台查看");
    }

    //("手机验证码登录")
    @PostMapping("/sms-login")
    public CommonResult<LoginVO> smsLogin(@Valid @RequestBody SmsLoginDTO smsLoginDTO) {
        return CommonResult.success(userService.smsLogin(smsLoginDTO));
    }


}