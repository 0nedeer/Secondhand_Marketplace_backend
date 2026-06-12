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

    // 下架商品：设为 off_shelf，所有者/管理员可见，异常表示失败
    void offShelfProduct(Long id, Long sellerId);

    // 真删除（软删除）：设为 deleted 状态，所有人不可见，异常表示失败
    void deleteProduct(Long id, Long sellerId);

    ProductVO getProductDetail(Long id);
    
    PageResult<ProductVO> getProductPage(ProductPageQueryDTO queryDTO, Long currentUserId);

    PageResult<ProductVO> getMyProductPage(ProductPageQueryDTO queryDTO, Long currentUserId);

    PageResult<ProductVO> getSellerProducts(Long sellerId, ProductPageQueryDTO queryDTO);

    boolean auditProduct(Long productId, Long adminId, Boolean approved, String rejectReason);
    
    // 卖家流程控制
    boolean submitForReview(Long id, Long sellerId);
    boolean revokeReview(Long id, Long sellerId);
    boolean relistProduct(Long id, Long sellerId);
}

