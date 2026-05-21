package com.secondhand.marketplace.backend.modules.admin.service;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final UserAccountMapper userAccountMapper;

    public UserAccount requireAdmin(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "登录用户不存在");
        }
        if (!Integer.valueOf(1).equals(user.getIsAdmin())) {
            throw new BusinessException(403, "无管理员权限");
        }
        if ("banned".equals(user.getUserStatus())) {
            throw new BusinessException(403, "管理员账号已被封禁");
        }
        return user;
    }

    public boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        UserAccount user = userAccountMapper.selectById(userId);
        return user != null
                && Integer.valueOf(1).equals(user.getIsAdmin())
                && !"banned".equals(user.getUserStatus());
    }
}
