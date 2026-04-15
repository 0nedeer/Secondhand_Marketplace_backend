package com.secondhand.marketplace.backend.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    @Select("SELECT * FROM user_account WHERE username = #{account} OR phone = #{account} OR email = #{account}")
    UserAccount findByAccount(String account);

    @Update("UPDATE user_account SET last_login_at = NOW() WHERE id = #{userId}")
    void updateLastLoginTime(Long userId);
}