package com.secondhand.marketplace.backend.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.common.util.JwtUtil;
import com.secondhand.marketplace.backend.common.util.PasswordUtil;
import com.secondhand.marketplace.backend.modules.user.dto.*;
import com.secondhand.marketplace.backend.modules.user.entity.*;
import com.secondhand.marketplace.backend.modules.user.mapper.*;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import com.secondhand.marketplace.backend.modules.user.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
/*商品服务
import com.secondhand.marketplace.backend.modules.product.entity.Product;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;*/
/*交易服务*/

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAccountMapper userAccountMapper;
    private final UserProfileMapper userProfileMapper;
    private final UserAddressMapper userAddressMapper;
    private final UserVerificationMapper userVerificationMapper;
    private final ProductFavoriteMapper productFavoriteMapper;
    private final SellerFollowMapper sellerFollowMapper;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final SellerReputationSnapshotMapper sellerReputationSnapshotMapper;
    //用于验证码模拟
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    // 模拟验证码存储（使用简单的内存Map，生产环境建议用Redis）
    private final java.util.Map<String, String> smsCodeCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, Long> smsCodeSendTime = new java.util.concurrent.ConcurrentHashMap<>();





    @Override
    @Transactional // 事务注解：保证多表操作要么全成功，要么全失败
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAccount::getUsername, registerDTO.getUsername());
        if (userAccountMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (registerDTO.getPhone() != null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserAccount::getPhone, registerDTO.getPhone());
            if (userAccountMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("手机号已被注册");
            }
        }

        // 检查邮箱是否已存在
        if (registerDTO.getEmail() != null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserAccount::getEmail, registerDTO.getEmail());
            if (userAccountMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("邮箱已被注册");
            }
        }

        // 创建用户
        UserAccount user = new UserAccount();
        user.setUsername(registerDTO.getUsername());
        user.setNickname(registerDTO.getNickname());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setPasswordHash(passwordUtil.encode(registerDTO.getPassword()));
        user.setCanBuy(1);
        user.setCanSell(1);
        user.setIsAdmin(0);
        user.setUserStatus("pending");
        user.setRegisteredAt(LocalDateTime.now());

        userAccountMapper.insert(user);//插入主表

        // 创建用户资料
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setCreditScore(100);
        profile.setPositiveRate(java.math.BigDecimal.valueOf(100.00));
        profile.setTotalReviewCount(0);

        userProfileMapper.insert(profile);
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查找用户
        UserAccount user = userAccountMapper.findByAccount(loginDTO.getAccount());
        if (user == null) {
            throw new BusinessException("账号或密码错误");
        }

        // 检查密码
        if (!passwordUtil.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new BusinessException("账号或密码错误");
        }

        // 检查用户状态
        if ("banned".equals(user.getUserStatus())) {
            throw new BusinessException("账号已被封禁");
        }
        if ("deleted".equals(user.getUserStatus())) {
            throw new BusinessException("账号已被删除");
        }

        // 更新最后登录时间
        userAccountMapper.updateLastLoginTime(user.getId());

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建用户信息
        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .userStatus(user.getUserStatus())
                .lastLoginAt(LocalDateTime.now())
                .registeredAt(user.getRegisteredAt())
                .build();

        return LoginVO.builder()
                .token(token)
                .userInfo(userVO)
                .build();
    }

    @Override
    public void logout(String token) {
        // 将token加入黑名单
        jwtUtil.addToBlacklist(token);
    }

    @Override
    public String forgotPassword(String account) {
        UserAccount user = userAccountMapper.findByAccount(account);
        if (user == null) {
            throw new BusinessException("账号不存在");
        }

        // 发送重置密码验证码（实际项目中调用短信或邮件服务）
        // 这里简化处理，实际应该发送验证码到手机或邮箱
        String resetToken = jwtUtil.generateResetToken(user.getId());
        // TODO: 发送重置链接或验证码

        //模拟：在控制台打印重置连接
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        System.out.println("==========================================");
        System.out.println("【重置密码】账号：" + account);
        System.out.println("重置链接：" + resetLink);
        System.out.println("请使用以下token调用重置接口：");
        System.out.println(resetToken);
        System.out.println("==========================================");

        return resetToken;  // 返回token供测试使用
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Long userId = jwtUtil.validateResetToken(resetPasswordDTO.getToken());
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPasswordHash(passwordUtil.encode(resetPasswordDTO.getNewPassword()));
        userAccountMapper.updateById(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证原密码
        if (!passwordUtil.matches(changePasswordDTO.getOldPassword(), user.getPasswordHash())) {
            throw new BusinessException("原密码错误");
        }

        user.setPasswordHash(passwordUtil.encode(changePasswordDTO.getNewPassword()));
        userAccountMapper.updateById(user);
    }

    @Override
    public void bindPhone(Long userId, String phone, String verifyCode) {
        // 验证验证码（实际项目中验证）
        // TODO: 验证短信验证码

        // 检查手机号是否已被绑定
        LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAccount::getPhone, phone);
        UserAccount existingUser = userAccountMapper.selectOne(wrapper);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new BusinessException("手机号已被其他用户绑定");
        }

        UserAccount user = userAccountMapper.selectById(userId);
        user.setPhone(phone);
        userAccountMapper.updateById(user);
    }

    @Override
    public void bindEmail(Long userId, String email, String verifyCode) {
        // 验证验证码（实际项目中验证）
        // TODO: 验证邮箱验证码

        // 检查邮箱是否已被绑定
        LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAccount::getEmail, email);
        UserAccount existingUser = userAccountMapper.selectOne(wrapper);
        if (existingUser != null && !existingUser.getId().equals(userId)) {
            throw new BusinessException("邮箱已被其他用户绑定");
        }

        UserAccount user = userAccountMapper.selectById(userId);
        user.setEmail(email);
        userAccountMapper.updateById(user);
    }

    @Override
    public void unbindPhone(Long userId) {
        LambdaUpdateWrapper<UserAccount> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAccount::getId, userId)
                .set(UserAccount::getPhone, null);
        userAccountMapper.update(null, wrapper);
    }

    @Override
    public void unbindEmail(Long userId) {
        LambdaUpdateWrapper<UserAccount> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAccount::getId, userId)
                .set(UserAccount::getEmail, null);
        userAccountMapper.update(null, wrapper);
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取头像URL
        UserProfile profile = userProfileMapper.findByUserId(user.getId());
        String avatarUrl = profile != null ? profile.getAvatarUrl() : null;

        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatarUrl(avatarUrl)
                .userStatus(user.getUserStatus())
                .lastLoginAt(user.getLastLoginAt())
                .registeredAt(user.getRegisteredAt())
                .build();
    }

    @Override
    public UserProfileVO getUserProfile(Long userId) {

        UserProfile profile = userProfileMapper.findByUserId(userId);


        if (profile == null) {
            throw new BusinessException("用户资料不存在");
        }

        return UserProfileVO.builder()
                .nickname(userAccountMapper.selectById(userId).getNickname())
                .avatarUrl(profile.getAvatarUrl())
                .gender(profile.getGender())
                .birthday(profile.getBirthday())
                .bio(profile.getBio())
                .city(profile.getCity())
                .district(profile.getDistrict())
                .creditScore(profile.getCreditScore())
                .positiveRate(profile.getPositiveRate())
                .totalReviewCount(profile.getTotalReviewCount())
                .build();
    }

    @Override
    public void updateUserProfile(Long userId, UpdateProfileDTO updateProfileDTO) {
        // 更新昵称
        if (updateProfileDTO.getNickname() != null) {
            UserAccount user = userAccountMapper.selectById(userId);
            user.setNickname(updateProfileDTO.getNickname());
            userAccountMapper.updateById(user);
        }

        // 更新资料
        UserProfile profile = userProfileMapper.findByUserId(userId);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
        }

        if (updateProfileDTO.getAvatarUrl() != null) profile.setAvatarUrl(updateProfileDTO.getAvatarUrl());
        if (updateProfileDTO.getGender() != null) profile.setGender(updateProfileDTO.getGender());
        if (updateProfileDTO.getBirthday() != null) profile.setBirthday(updateProfileDTO.getBirthday());
        if (updateProfileDTO.getBio() != null) profile.setBio(updateProfileDTO.getBio());
        if (updateProfileDTO.getCity() != null) profile.setCity(updateProfileDTO.getCity());
        if (updateProfileDTO.getDistrict() != null) profile.setDistrict(updateProfileDTO.getDistrict());

        if (profile.getUserId() == null) {
            userProfileMapper.insert(profile);
        } else {
            userProfileMapper.updateById(profile);
        }
    }

    @Override
    public UserPermissionsVO getUserPermissions(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return UserPermissionsVO.builder()
                .canBuy(user.getCanBuy() == 1)
                .canSell(user.getCanSell() == 1)
                .isAdmin(user.getIsAdmin() == 1)
                .build();
    }

    @Override
    public String getUserStatus(Long userId) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getUserStatus();
    }

    @Override
    public List<AddressVO> getUserAddresses(Long userId) {
        List<UserAddress> addresses = userAddressMapper.findByUserId(userId);
        return addresses.stream()
                .map(addr -> AddressVO.builder()
                        .id(addr.getId())
                        .receiverName(addr.getReceiverName())
                        .receiverPhone(addr.getReceiverPhone())
                        .province(addr.getProvince())
                        .city(addr.getCity())
                        .district(addr.getDistrict())
                        .detailAddress(addr.getDetailAddress())
                        .isDefault(addr.getIsDefault() == 1)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addAddress(Long userId, AddressDTO addressDTO) {
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiverName(addressDTO.getReceiverName());
        address.setReceiverPhone(addressDTO.getReceiverPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailAddress(addressDTO.getDetailAddress());
        address.setIsDefault(addressDTO.getIsDefault() != null && addressDTO.getIsDefault() ? 1 : 0);

        // 如果是默认地址，取消其他默认地址
        if (address.getIsDefault() == 1) {
            userAddressMapper.resetDefaultAddress(userId);
        }

        userAddressMapper.insert(address);
    }

    @Override
    @Transactional
    public void updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        address.setReceiverName(addressDTO.getReceiverName());
        address.setReceiverPhone(addressDTO.getReceiverPhone());
        address.setProvince(addressDTO.getProvince());
        address.setCity(addressDTO.getCity());
        address.setDistrict(addressDTO.getDistrict());
        address.setDetailAddress(addressDTO.getDetailAddress());

        boolean isDefault = addressDTO.getIsDefault() != null && addressDTO.getIsDefault();
        if (isDefault && address.getIsDefault() != 1) {
            userAddressMapper.resetDefaultAddress(userId);
            address.setIsDefault(1);
        } else if (!isDefault && address.getIsDefault() == 1) {
            address.setIsDefault(0);
        }

        userAddressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        userAddressMapper.deleteById(addressId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }

        userAddressMapper.resetDefaultAddress(userId);
        address.setIsDefault(1);
        userAddressMapper.updateById(address);
    }

    @Override
    @Transactional
    public void submitRealNameVerification(Long userId, RealNameVerificationDTO verificationDTO) {
        // 检查是否已有通过的认证
        UserVerification existing = userVerificationMapper.findApprovedByUserIdAndType(userId, "real_name");
        if (existing != null) {
            throw new BusinessException("您已通过实名认证，无需重复提交");
        }

        // 检查身份证号是否已被认证
        LambdaQueryWrapper<UserVerification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserVerification::getIdCardNumber, verificationDTO.getIdCardNumber())
                .eq(UserVerification::getVerifyStatus, "approved");
        if (userVerificationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该身份证号已被认证");
        }

        UserVerification verification = new UserVerification();
        verification.setUserId(userId);
        verification.setVerifyType("real_name");
        verification.setRealName(verificationDTO.getRealName());
        verification.setIdCardNumber(verificationDTO.getIdCardNumber());
        verification.setVerifyStatus("pending");
        verification.setSubmittedAt(LocalDateTime.now());

        userVerificationMapper.insert(verification);
    }

    @Override
    public List<VerificationStatusVO> getVerificationStatus(Long userId) {
        List<UserVerification> verifications = userVerificationMapper.findByUserId(userId);
        return verifications.stream()
                .map(v -> VerificationStatusVO.builder()
                        .verifyType(v.getVerifyType())
                        .verifyStatus(v.getVerifyStatus())
                        .rejectReason(v.getRejectReason())
                        .submittedAt(v.getSubmittedAt().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void favoriteProduct(Long userId, Long productId) {
        // 检查是否已收藏
        LambdaQueryWrapper<ProductFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getProductId, productId);
        if (productFavoriteMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已经收藏过该商品");
        }

        ProductFavorite favorite = new ProductFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        productFavoriteMapper.insert(favorite);
    }

    @Override
    public void unfavoriteProduct(Long userId, Long productId) {
        LambdaQueryWrapper<ProductFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getProductId, productId);
        productFavoriteMapper.delete(wrapper);
    }

    @Override
    public List<Long> getUserFavorites(Long userId) {
        return productFavoriteMapper.findProductIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void followSeller(Long buyerId, Long sellerId) {
        if (buyerId.equals(sellerId)) {
            throw new BusinessException("不能关注自己");
        }

        // 检查是否已关注
        LambdaQueryWrapper<SellerFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SellerFollow::getBuyerId, buyerId)
                .eq(SellerFollow::getSellerId, sellerId);
        if (sellerFollowMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已经关注过该卖家");
        }

        SellerFollow follow = new SellerFollow();
        follow.setBuyerId(buyerId);
        follow.setSellerId(sellerId);
        sellerFollowMapper.insert(follow);
    }

    @Override
    public void unfollowSeller(Long buyerId, Long sellerId) {
        LambdaQueryWrapper<SellerFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SellerFollow::getBuyerId, buyerId)
                .eq(SellerFollow::getSellerId, sellerId);
        sellerFollowMapper.delete(wrapper);
    }

    @Override
    public List<Long> getUserFollows(Long buyerId) {
        return sellerFollowMapper.findSellerIdsByBuyerId(buyerId);
    }

    @Override
    public List<Long> getUserFollowers(Long sellerId) {
        LambdaQueryWrapper<SellerFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SellerFollow::getSellerId, sellerId)
                .select(SellerFollow::getBuyerId);
        return sellerFollowMapper.selectList(wrapper).stream()
                .map(SellerFollow::getBuyerId)
                .collect(Collectors.toList());
    }

    @Override
    public UserStatsVO getUserStats(Long userId) {
        // 商品数量（需要调用商品模块的service）
        int productCount = 0; // TODO: 调用商品服务
        /*LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
    productWrapper.eq(Product::getSellerId, userId);  // 使用 sellerId
    int productCount = (int) productService.count(productWrapper);*/

        // 订单数量（需要调用交易模块的service）
        int orderCount = 0; // TODO: 调用交易服务

        int favoriteCount = productFavoriteMapper.countByUserId(userId);
        int followCount = sellerFollowMapper.countFollowsByBuyerId(userId);
        int followerCount = sellerFollowMapper.countFollowersBySellerId(userId);

        return UserStatsVO.builder()
                .productCount(productCount)
                .orderCount(orderCount)
                .favoriteCount(favoriteCount)
                .followCount(followCount)
                .followerCount(followerCount)
                .build();
    }

    @Override
    public AdminUserPageVO pageUsers(Long adminId, Boolean isAdmin, Boolean canBuy, Boolean canSell,
                                     String userStatus, long page, long pageSize) {
        UserAccount admin = userAccountMapper.selectById(adminId);
        if (admin == null || admin.getIsAdmin() != 1) {
            throw new BusinessException("无权限操作");
        }
        if (page < 1 || pageSize < 1) {
            throw new BusinessException("分页参数不合法");
        }

        String normalizedStatus = userStatus == null ? null : userStatus.trim();
        LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(isAdmin != null, UserAccount::getIsAdmin, Boolean.TRUE.equals(isAdmin) ? 1 : 0)
                .eq(canBuy != null, UserAccount::getCanBuy, Boolean.TRUE.equals(canBuy) ? 1 : 0)
                .eq(canSell != null, UserAccount::getCanSell, Boolean.TRUE.equals(canSell) ? 1 : 0)
                .eq(normalizedStatus != null && !normalizedStatus.isEmpty(), UserAccount::getUserStatus, normalizedStatus)
                .orderByDesc(UserAccount::getRegisteredAt);

        Page<UserAccount> userPage = userAccountMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<AdminUserItemVO> list = userPage.getRecords().stream()
                .map(user -> AdminUserItemVO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .canBuy(user.getCanBuy() != null && user.getCanBuy() == 1)
                        .canSell(user.getCanSell() != null && user.getCanSell() == 1)
                        .isAdmin(user.getIsAdmin() != null && user.getIsAdmin() == 1)
                        .userStatus(user.getUserStatus())
                        .lastLoginAt(user.getLastLoginAt())
                        .registeredAt(user.getRegisteredAt())
                        .build())
                .collect(Collectors.toList());

        return new AdminUserPageVO(userPage.getTotal(), userPage.getCurrent(), userPage.getSize(), list);
    }

    @Override
    public void banUser(Long adminId, Long userId) {
        // 检查管理员权限
        UserAccount admin = userAccountMapper.selectById(adminId);
        if (admin.getIsAdmin() != 1) {
            throw new BusinessException("无权限操作");
        }

        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setUserStatus("banned");
        userAccountMapper.updateById(user);
    }

    @Override
    public void unbanUser(Long adminId, Long userId) {
        UserAccount admin = userAccountMapper.selectById(adminId);
        if (admin.getIsAdmin() != 1) {
            throw new BusinessException("无权限操作");
        }

        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setUserStatus("active");
        userAccountMapper.updateById(user);
    }

    @Override
    public void toggleCanBuy(Long userId, Boolean canBuy) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setCanBuy(canBuy ? 1 : 0);
        userAccountMapper.updateById(user);
    }

    @Override
    public void toggleCanSell(Long userId, Boolean canSell) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setCanSell(canSell ? 1 : 0);
        userAccountMapper.updateById(user);
    }

    //验证码相关
    @Override
    public void sendSmsCode(String phone) {
        // 检查发送频率（1分钟内只能发送1次）
        Long lastSendTime = smsCodeSendTime.get(phone);
        if (lastSendTime != null && System.currentTimeMillis() - lastSendTime < 60000) {
            throw new BusinessException("请勿频繁发送验证码，请稍后再试");
        }

        // 生成6位随机验证码
        String code = String.format("%06d", new java.util.Random().nextInt(999999));

        // 存储验证码（有效期5分钟）
        smsCodeCache.put(phone, code);
        smsCodeSendTime.put(phone, System.currentTimeMillis());

        // 模拟发送短信：在控制台打印验证码
        System.out.println("==========================================");
        System.out.println("【模拟短信】手机号：" + phone + "，验证码：" + code);
        System.out.println("验证码5分钟内有效，请在控制台查看");
        System.out.println("==========================================");

        // 5分钟后自动清除验证码
        new Thread(() -> {
            try {
                Thread.sleep(300000); // 5分钟
                smsCodeCache.remove(phone);
                smsCodeSendTime.remove(phone);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public LoginVO smsLogin(SmsLoginDTO smsLoginDTO) {
        // 1. 验证验证码
        String savedCode = smsCodeCache.get(smsLoginDTO.getPhone());
        if (savedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (!savedCode.equals(smsLoginDTO.getVerifyCode())) {
            throw new BusinessException("验证码错误");
        }

        // 2. 验证成功后删除验证码（防止重复使用）
        smsCodeCache.remove(smsLoginDTO.getPhone());
        smsCodeSendTime.remove(smsLoginDTO.getPhone());

        // 3. 根据手机号查找用户
        LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAccount::getPhone, smsLoginDTO.getPhone());
        UserAccount user = userAccountMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("用户不存在，请先注册");
        }

        // 4. 检查用户状态
        if ("banned".equals(user.getUserStatus())) {
            throw new BusinessException("账号已被封禁");
        }
        if ("deleted".equals(user.getUserStatus())) {
            throw new BusinessException("账号已被删除");
        }

        // 5. 更新最后登录时间
        userAccountMapper.updateLastLoginTime(user.getId());

        // 6. 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 7. 获取头像URL
        UserProfile profile = userProfileMapper.findByUserId(user.getId());
        String avatarUrl = profile != null ? profile.getAvatarUrl() : null;

        // 8. 构建返回结果
        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatarUrl(avatarUrl)
                .userStatus(user.getUserStatus())
                .lastLoginAt(LocalDateTime.now())
                .registeredAt(user.getRegisteredAt())
                .build();

        return LoginVO.builder()
                .token(token)
                .userInfo(userVO)
                .build();
    }

    //认证相关
    @Override
    public VerificationDetailVO getVerificationDetail(Long userId, Long verificationId) {
        UserVerification verification = userVerificationMapper.selectById(verificationId);
        if (verification == null) {
            throw new BusinessException("认证记录不存在");
        }
        // 只能查询自己的认证记录
        if (!verification.getUserId().equals(userId)) {
            throw new BusinessException("无权查看他人的认证记录");
        }

        // 身份证号脱敏处理
        String idCardNumber = verification.getIdCardNumber();
        String maskedIdCard = null;
        if (idCardNumber != null && idCardNumber.length() >= 10) {
            maskedIdCard = idCardNumber.substring(0, 6) + "****" + idCardNumber.substring(idCardNumber.length() - 4);
        }

        return VerificationDetailVO.builder()
                .id(verification.getId())
                .verifyType(verification.getVerifyType())
                .realName(verification.getRealName())
                .idCardNumber(maskedIdCard)
                .verifyStatus(verification.getVerifyStatus())
                .rejectReason(verification.getRejectReason())
                .submittedAt(verification.getSubmittedAt() != null ? verification.getSubmittedAt().toString() : null)
                .reviewedAt(verification.getReviewedAt() != null ? verification.getReviewedAt().toString() : null)
                .build();
    }

    @Override
    @Transactional
    public void resubmitVerification(Long userId, Long verificationId, RealNameVerificationDTO verificationDTO) {
        UserVerification verification = userVerificationMapper.selectById(verificationId);
        if (verification == null) {
            throw new BusinessException("认证记录不存在");
        }
        if (!verification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人的认证记录");
        }
        // 只有被驳回的记录才能重新提交
        if (!"rejected".equals(verification.getVerifyStatus())) {
            throw new BusinessException("只有被驳回的认证记录才能重新提交");
        }

        // 更新认证信息
        verification.setRealName(verificationDTO.getRealName());
        verification.setIdCardNumber(verificationDTO.getIdCardNumber());
        verification.setVerifyStatus("pending");
        verification.setSubmittedAt(LocalDateTime.now());
        verification.setReviewedBy(null);
        verification.setReviewedAt(null);
        verification.setRejectReason(null);

        userVerificationMapper.updateById(verification);
    }

    //信誉相关
    @Override
    public ReputationVO getSellerReputation(Long sellerId) {
        // 获取卖家基础信息
        UserProfile profile = userProfileMapper.findByUserId(sellerId);
        if (profile == null) {
            throw new BusinessException("卖家信息不存在");
        }

        // 获取最新快照
        SellerReputationSnapshot snapshot = sellerReputationSnapshotMapper.findLatestBySellerId(sellerId);

        Integer totalOrders = snapshot != null ? snapshot.getTotalOrders() : 0;
        Integer completedOrders = snapshot != null ? snapshot.getCompletedOrders() : 0;

        return ReputationVO.builder()
                .creditScore(profile.getCreditScore())
                .positiveRate(profile.getPositiveRate())
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .totalReviewCount(profile.getTotalReviewCount())
                .build();
    }

    @Override
    public List<ReputationHistoryVO> getReputationHistory(Long sellerId, Integer days) {
        if (days == null || days <= 0) {
            days = 30; // 默认30天
        }
        if (days > 90) {
            days = 90; // 最多90天
        }

        List<SellerReputationSnapshot> snapshots = sellerReputationSnapshotMapper.findHistorySnapshots(sellerId, days);

        return snapshots.stream()
                .map(s -> ReputationHistoryVO.builder()
                        .snapshotDate(s.getSnapshotDate())
                        .creditScore(s.getCreditScore())
                        .positiveRate(s.getPositiveRate())
                        .totalOrders(s.getTotalOrders())
                        .completedOrders(s.getCompletedOrders())
                        .build())
                .collect(Collectors.toList());
    }
}