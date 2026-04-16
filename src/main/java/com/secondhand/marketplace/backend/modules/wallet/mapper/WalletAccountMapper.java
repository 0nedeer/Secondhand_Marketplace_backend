package com.secondhand.marketplace.backend.modules.wallet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.wallet.entity.WalletAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WalletAccountMapper extends BaseMapper<WalletAccount> {

    @Select("SELECT * FROM wallet_account WHERE user_id = #{userId} LIMIT 1")
    WalletAccount selectByUserId(Long userId);

    @Select("SELECT * FROM wallet_account WHERE user_id = #{userId} LIMIT 1 FOR UPDATE")
    WalletAccount selectByUserIdForUpdate(Long userId);

    @Select("SELECT * FROM wallet_account WHERE id = #{id} FOR UPDATE")
    WalletAccount selectByIdForUpdate(Long id);
}

