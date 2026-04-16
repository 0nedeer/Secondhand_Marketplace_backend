package com.secondhand.marketplace.backend.modules.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.OrderShipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderShipmentMapper extends BaseMapper<OrderShipment> {

    @Select("SELECT * FROM order_shipment WHERE order_id = #{orderId} LIMIT 1")
    OrderShipment selectByOrderId(Long orderId);

    @Select("SELECT * FROM order_shipment WHERE order_id = #{orderId} LIMIT 1 FOR UPDATE")
    OrderShipment selectByOrderIdForUpdate(Long orderId);

    @Select("SELECT * FROM order_shipment WHERE id = #{shipmentId} AND order_id = #{orderId} LIMIT 1")
    OrderShipment selectByIdAndOrderId(Long shipmentId, Long orderId);

    @Select("SELECT * FROM order_shipment WHERE id = #{shipmentId} AND order_id = #{orderId} LIMIT 1 FOR UPDATE")
    OrderShipment selectByIdAndOrderIdForUpdate(Long shipmentId, Long orderId);
}
