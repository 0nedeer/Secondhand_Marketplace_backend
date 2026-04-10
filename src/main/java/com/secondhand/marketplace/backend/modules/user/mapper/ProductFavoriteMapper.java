package com.secondhand.marketplace.backend.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.user.entity.ProductFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ProductFavoriteMapper extends BaseMapper<ProductFavorite> {

    @Select("SELECT COUNT(*) FROM product_favorite WHERE user_id = #{userId}")
    Integer countByUserId(Long userId);

    @Select("SELECT product_id FROM product_favorite WHERE user_id = #{userId}")
    List<Long> findProductIdsByUserId(Long userId);
}