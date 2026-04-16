package com.secondhand.marketplace.backend.modules.aftersale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.aftersale.entity.AfterSaleRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AfterSaleRequestMapper extends BaseMapper<AfterSaleRequest> {

    @Select("SELECT * FROM after_sale_request WHERE id = #{id} LIMIT 1 FOR UPDATE")
    AfterSaleRequest selectByIdForUpdate(Long id);

    @Select({
            "<script>",
            "SELECT * FROM after_sale_request",
            "WHERE order_item_id = #{orderItemId}",
            "AND request_status IN ('pending_seller','pending_admin','approved')",
            "ORDER BY created_at DESC, id DESC LIMIT 1",
            "</script>"
    })
    AfterSaleRequest selectActiveByOrderItemId(Long orderItemId);

    @Select({
            "<script>",
            "SELECT * FROM after_sale_request",
            "WHERE 1 = 1",
            "<if test='orderId != null'> AND order_id = #{orderId}</if>",
            "<if test='requestStatus != null and requestStatus != \"\"'> AND request_status = #{requestStatus}</if>",
            "<if test='requestType != null and requestType != \"\"'> AND request_type = #{requestType}</if>",
            "<if test='buyerId != null'> AND buyer_id = #{buyerId}</if>",
            "<if test='sellerId != null'> AND seller_id = #{sellerId}</if>",
            "ORDER BY created_at DESC, id DESC",
            "</script>"
    })
    List<AfterSaleRequest> selectByFilters(@Param("orderId") Long orderId,
                                           @Param("requestStatus") String requestStatus,
                                           @Param("requestType") String requestType,
                                           @Param("buyerId") Long buyerId,
                                           @Param("sellerId") Long sellerId);
}
