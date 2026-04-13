package com.secondhand.marketplace.backend.modules.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.PaymentTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentTransactionMapper extends BaseMapper<PaymentTransaction> {

    @Select("SELECT * FROM payment_transaction WHERE payment_order_id = #{paymentOrderId} ORDER BY occurred_at DESC, id DESC")
    List<PaymentTransaction> selectByPaymentOrderId(Long paymentOrderId);
}

