package com.secondhand.marketplace.backend.modules.trade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductImageMapper {

    @Select("SELECT image_url FROM product_image WHERE product_id = #{productId} ORDER BY is_cover DESC, sort_no ASC, id ASC LIMIT 1")
    String selectCoverImageByProductId(Long productId);
}

