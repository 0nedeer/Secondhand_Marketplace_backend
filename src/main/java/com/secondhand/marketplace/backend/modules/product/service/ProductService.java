package com.secondhand.marketplace.backend.modules.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.secondhand.marketplace.backend.modules.product.entity.Product;

public interface ProductService extends IService<Product> {
    void addViewCount(Long id);
}
