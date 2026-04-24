package com.secondhand.marketplace.backend.modules.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.secondhand.marketplace.backend.modules.product.dto.ProductCreateDTO;
import com.secondhand.marketplace.backend.modules.product.dto.ProductPageQueryDTO;
import com.secondhand.marketplace.backend.modules.product.dto.ProductUpdateDTO;
import com.secondhand.marketplace.backend.modules.product.entity.Product;
import com.secondhand.marketplace.backend.modules.product.vo.PageResult;
import com.secondhand.marketplace.backend.modules.product.vo.ProductVO;

public interface ProductService extends IService<Product> {
    boolean addViewCount(Long id);

    ProductVO createProduct(ProductCreateDTO dto, Long sellerId);

    ProductVO updateProduct(ProductUpdateDTO dto, Long sellerId);

    boolean deleteProduct(Long id, Long sellerId);
    
    ProductVO getProductDetail(Long id);
    
    PageResult<ProductVO> getProductPage(ProductPageQueryDTO queryDTO, Long currentUserId);
}
