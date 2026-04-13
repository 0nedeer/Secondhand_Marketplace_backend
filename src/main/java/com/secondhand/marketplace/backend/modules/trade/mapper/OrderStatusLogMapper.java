package com.secondhand.marketplace.backend.modules.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderStatusLogMapper extends BaseMapper<OrderStatusLog> {

    @Select("SELECT * FROM order_status_log WHERE order_id = #{orderId} ORDER BY changed_at ASC, id ASC")
    List<OrderStatusLog> selectByOrderId(Long orderId);
}

