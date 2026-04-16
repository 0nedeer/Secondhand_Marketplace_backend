package com.secondhand.marketplace.backend.modules.aftersale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.aftersale.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    @Select("SELECT * FROM review WHERE id = #{id} LIMIT 1 FOR UPDATE")
    Review selectByIdForUpdate(Long id);

    @Select("SELECT * FROM review WHERE order_item_id = #{orderItemId} LIMIT 1")
    Review selectByOrderItemId(Long orderItemId);

    @Select({
            "<script>",
            "SELECT * FROM review",
            "WHERE 1 = 1",
            "<if test='sellerId != null'> AND seller_id = #{sellerId}</if>",
            "<if test='productId != null'> AND product_id = #{productId}</if>",
            "<if test='orderId != null'> AND order_id = #{orderId}</if>",
            "<if test='buyerId != null'> AND buyer_id = #{buyerId}</if>",
            "ORDER BY created_at DESC, id DESC",
            "</script>"
    })
    List<Review> selectByFilters(@Param("sellerId") Long sellerId,
                                 @Param("productId") Long productId,
                                 @Param("orderId") Long orderId,
                                 @Param("buyerId") Long buyerId);
}
