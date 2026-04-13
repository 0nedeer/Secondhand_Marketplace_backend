package com.secondhand.marketplace.backend.modules.aftersale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.aftersale.entity.DisputeCase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DisputeCaseMapper extends BaseMapper<DisputeCase> {

    @Select("SELECT * FROM dispute_case WHERE id = #{id} LIMIT 1 FOR UPDATE")
    DisputeCase selectByIdForUpdate(Long id);

    @Select({
            "<script>",
            "SELECT * FROM dispute_case",
            "WHERE order_id = #{orderId}",
            "<if test='afterSaleId != null'> AND after_sale_id = #{afterSaleId}</if>",
            "<if test='afterSaleId == null'> AND after_sale_id IS NULL</if>",
            "AND current_status IN ('open','investigating','waiting_evidence')",
            "ORDER BY created_at DESC, id DESC LIMIT 1",
            "</script>"
    })
    DisputeCase selectActiveCase(@Param("orderId") Long orderId, @Param("afterSaleId") Long afterSaleId);

    @Select({
            "<script>",
            "SELECT * FROM dispute_case",
            "WHERE 1 = 1",
            "<if test='orderId != null'> AND order_id = #{orderId}</if>",
            "<if test='afterSaleId != null'> AND after_sale_id = #{afterSaleId}</if>",
            "<if test='currentStatus != null and currentStatus != \"\"'> AND current_status = #{currentStatus}</if>",
            "<if test='buyerId != null'> AND buyer_id = #{buyerId}</if>",
            "<if test='sellerId != null'> AND seller_id = #{sellerId}</if>",
            "ORDER BY created_at DESC, id DESC",
            "</script>"
    })
    List<DisputeCase> selectByFilters(@Param("orderId") Long orderId,
                                      @Param("afterSaleId") Long afterSaleId,
                                      @Param("currentStatus") String currentStatus,
                                      @Param("buyerId") Long buyerId,
                                      @Param("sellerId") Long sellerId);
}
