package com.secondhand.marketplace.backend.modules.wallet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.wallet.entity.WithdrawalRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WithdrawalRequestMapper extends BaseMapper<WithdrawalRequest> {

    @Select("SELECT * FROM withdrawal_request WHERE id = #{id} FOR UPDATE")
    WithdrawalRequest selectByIdForUpdate(Long id);
}

