package com.secondhand.marketplace.backend.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondhand.marketplace.backend.modules.product.entity.ProductImage;
import com.secondhand.marketplace.backend.modules.product.mapper.ProdImageMapper;
import com.secondhand.marketplace.backend.modules.product.service.ProductImageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageServiceImpl extends ServiceImpl<ProdImageMapper, ProductImage> implements ProductImageService {

    @Override
    public List<ProductImage> getImagesByProductId(Long productId) {
        return this.list(new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId)
                .orderByAsc(ProductImage::getSortNo));
    }

    @Override
    public void saveImages(Long productId, List<ProductImage> images) {
        // 先删除旧图片
        deleteByProductId(productId);
        // 保存新图片
        if (images != null && !images.isEmpty()) {
            for (ProductImage img : images) {
                img.setProductId(productId);
            }
            this.saveBatch(images);
        }
    }

    @Override
    public void deleteByProductId(Long productId) {
        this.remove(new LambdaQueryWrapper<ProductImage>().eq(ProductImage::getProductId, productId));
    }
}
