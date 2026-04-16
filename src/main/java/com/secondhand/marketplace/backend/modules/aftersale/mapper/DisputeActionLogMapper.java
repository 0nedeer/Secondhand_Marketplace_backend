package com.secondhand.marketplace.backend.modules.aftersale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.aftersale.entity.DisputeActionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DisputeActionLogMapper extends BaseMapper<DisputeActionLog> {

    @Select("SELECT * FROM dispute_action_log WHERE dispute_id = #{disputeId} ORDER BY created_at ASC, id ASC")
    List<DisputeActionLog> selectByDisputeId(Long disputeId);
}
