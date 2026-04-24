package com.secondhand.marketplace.backend.modules.user.service;

import com.secondhand.marketplace.backend.modules.user.dto.*;
import com.secondhand.marketplace.backend.modules.user.vo.*;
import java.util.List;

import com.secondhand.marketplace.backend.modules.user.dto.SmsLoginDTO;
import com.secondhand.marketplace.backend.modules.user.vo.VerificationDetailVO;
import com.secondhand.marketplace.backend.modules.user.vo.ReputationVO;
import com.secondhand.marketplace.backend.modules.user.vo.ReputationHistoryVO;

public interface UserService {

    // 用户注册登录
    void register(RegisterDTO registerDTO);
    LoginVO login(LoginDTO loginDTO);
    void logout(String token);

    // 密码管理
    String forgotPassword(String account);
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
    void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

    // 绑定管理
    void bindPhone(Long userId, String phone, String verifyCode);
    void bindEmail(Long userId, String email, String verifyCode);
    void unbindPhone(Long userId);
    void unbindEmail(Long userId);

    // 用户信息
    UserVO getUserInfo(Long userId);
    UserProfileVO getUserProfile(Long userId);
    void updateUserProfile(Long userId, UpdateProfileDTO updateProfileDTO);
    UserPermissionsVO getUserPermissions(Long userId);
    String getUserStatus(Long userId);

    // 地址管理
    List<AddressVO> getUserAddresses(Long userId);
    void addAddress(Long userId, AddressDTO addressDTO);
    void updateAddress(Long userId, Long addressId, AddressDTO addressDTO);
    void deleteAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);

    // 认证管理
    void submitRealNameVerification(Long userId, RealNameVerificationDTO verificationDTO);
    List<VerificationStatusVO> getVerificationStatus(Long userId);

    // 收藏关注
    void favoriteProduct(Long userId, Long productId);
    void unfavoriteProduct(Long userId, Long productId);
    List<Long> getUserFavorites(Long userId);

    void followSeller(Long buyerId, Long sellerId);
    void unfollowSeller(Long buyerId, Long sellerId);
    List<Long> getUserFollows(Long buyerId);
    List<Long> getUserFollowers(Long sellerId);

    // 统计数据
    UserStatsVO getUserStats(Long userId);

    // 管理员功能
    AdminUserPageVO pageUsers(Long adminId, Boolean isAdmin, Boolean canBuy, Boolean canSell,
                              String userStatus, long page, long pageSize);
    void banUser(Long adminId, Long userId);
    void unbanUser(Long adminId, Long userId);
    void toggleCanBuy(Long userId, Boolean canBuy);
    void toggleCanSell(Long userId, Boolean canSell);


    // ========== 手机验证码登录（模拟版）==========
    //手机验证码登录
    LoginVO smsLogin(SmsLoginDTO smsLoginDTO);

    //发送短信验证码(模拟：控制台打印)
    void sendSmsCode(String phone);

    //查询认证记录详情
    VerificationDetailVO getVerificationDetail(Long userId,Long verificationId);

    //重新提交认证
    void resubmitVerification(Long userId,Long verificationId,RealNameVerificationDTO verificationDTO);

    // ========== 信誉相关 ==========
    //获取卖家信誉快照
    ReputationVO getSellerReputation(Long sellerId);

    //获取卖家历史趋势
    List<ReputationHistoryVO> getReputationHistory(Long sellerId,Integer days);





}