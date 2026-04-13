package com.secondhand.marketplace.backend.modules.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {

    @Select("SELECT * FROM payment_order WHERE id = #{id} FOR UPDATE")
    PaymentOrder selectByIdForUpdate(Long id);

    @Select("SELECT * FROM payment_order WHERE order_id = #{orderId} LIMIT 1")
    PaymentOrder selectByOrderId(Long orderId);

    @Select("SELECT * FROM payment_order WHERE order_id = #{orderId} LIMIT 1 FOR UPDATE")
    PaymentOrder selectByOrderIdForUpdate(Long orderId);

    @Select("SELECT * FROM payment_order WHERE payment_no = #{paymentNo} LIMIT 1 FOR UPDATE")
    PaymentOrder selectByPaymentNoForUpdate(String paymentNo);
}

