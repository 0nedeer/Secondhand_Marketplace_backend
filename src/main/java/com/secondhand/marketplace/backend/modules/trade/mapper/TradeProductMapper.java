package com.secondhand.marketplace.backend.modules.trade.mapper;

import com.secondhand.marketplace.backend.modules.trade.entity.TradeProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TradeProductMapper {

    @Select("SELECT id, seller_id, title, selling_price, stock, publish_status FROM product WHERE id = #{id} FOR UPDATE")
    TradeProduct selectByIdForUpdate(Long id);

    @Update("UPDATE product SET stock = stock - #{quantity}, updated_at = NOW() WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE product SET stock = stock + #{quantity}, updated_at = NOW() WHERE id = #{productId}")
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("UPDATE product SET publish_status = #{publishStatus}, updated_at = NOW() WHERE id = #{productId}")
    int updatePublishStatus(@Param("productId") Long productId, @Param("publishStatus") String publishStatus);
}
