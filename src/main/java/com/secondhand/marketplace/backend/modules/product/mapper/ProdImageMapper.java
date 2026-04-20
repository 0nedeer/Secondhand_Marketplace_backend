package com.secondhand.marketplace.backend.modules.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.product.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProdImageMapper extends BaseMapper<ProductImage> {
}
