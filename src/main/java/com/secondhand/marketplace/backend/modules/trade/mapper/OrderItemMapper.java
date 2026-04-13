package com.secondhand.marketplace.backend.modules.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId} ORDER BY id ASC")
    List<OrderItem> selectByOrderId(Long orderId);

    @Select({
            "<script>",
            "SELECT * FROM order_item",
            "WHERE order_id IN",
            "<foreach collection='orderIds' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "ORDER BY order_id ASC, id ASC",
            "</script>"
    })
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
}
