package com.secondhand.marketplace.backend.modules.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.secondhand.marketplace.backend.modules.product.entity.ProductImage;

import java.util.List;

public interface ProductImageService extends IService<ProductImage> {
    List<ProductImage> getImagesByProductId(Long productId);
    void saveImages(Long productId, List<ProductImage> images);
    void deleteByProductId(Long productId);
}
