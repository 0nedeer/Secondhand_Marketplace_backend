package com.secondhand.marketplace.backend.modules.aftersale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.aftersale.entity.AfterSaleEvidence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AfterSaleEvidenceMapper extends BaseMapper<AfterSaleEvidence> {

    @Select("SELECT * FROM after_sale_evidence WHERE after_sale_id = #{afterSaleId} ORDER BY created_at ASC, id ASC")
    List<AfterSaleEvidence> selectByAfterSaleId(Long afterSaleId);
}
