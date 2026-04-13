package com.secondhand.marketplace.backend.modules.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.trade.entity.LogisticsTrace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LogisticsTraceMapper extends BaseMapper<LogisticsTrace> {

    @Select("SELECT * FROM logistics_trace WHERE shipment_id = #{shipmentId} ORDER BY trace_time DESC, id DESC")
    List<LogisticsTrace> selectByShipmentId(Long shipmentId);
}
