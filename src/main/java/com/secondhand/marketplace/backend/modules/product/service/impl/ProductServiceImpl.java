package com.secondhand.marketplace.backend.modules.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondhand.marketplace.backend.modules.product.entity.Product;
import com.secondhand.marketplace.backend.modules.product.mapper.ProductMapper;
import com.secondhand.marketplace.backend.modules.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public void addViewCount(Long id) {
        Product p = this.getById(id);
        if (p != null) {
            p.setViewCount((p.getViewCount() == null ? 0 : p.getViewCount()) + 1);
            this.updateById(p);
        }
    }
}
