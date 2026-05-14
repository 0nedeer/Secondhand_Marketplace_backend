package com.secondhand.marketplace.backend.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.product.dto.ProductCreateDTO;
import com.secondhand.marketplace.backend.modules.product.dto.ProductImageDTO;
import com.secondhand.marketplace.backend.modules.product.dto.ProductPageQueryDTO;
import com.secondhand.marketplace.backend.modules.product.dto.ProductUpdateDTO;
import com.secondhand.marketplace.backend.modules.product.entity.Product;
import com.secondhand.marketplace.backend.modules.product.entity.ProductImage;
import com.secondhand.marketplace.backend.modules.product.mapper.CategoryMapper;
import com.secondhand.marketplace.backend.modules.product.mapper.ProductMapper;
import com.secondhand.marketplace.backend.modules.product.service.ProductImageService;
import com.secondhand.marketplace.backend.modules.product.service.ProductService;
import com.secondhand.marketplace.backend.modules.product.vo.PageResult;
import com.secondhand.marketplace.backend.modules.product.vo.ProductImageVO;
import com.secondhand.marketplace.backend.modules.product.vo.ProductVO;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private static final String STATUS_ON_SALE = "on_sale";
    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_PENDING_REVIEW = "pending_review";
    private static final String STATUS_OFF_SHELF = "off_shelf";
    private static final String STATUS_REJECT = "rejected";
    private static final String STATUS_SOLD = "sold";

    private static final Set<String> PUBLIC_QUERYABLE_STATUSES = Set.of(STATUS_ON_SALE, STATUS_SOLD);

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Override
    public boolean addViewCount(Long id) {
        Product p = this.getById(id);
        if (p == null) {
            return false;
        }
        p.setViewCount((p.getViewCount() == null ? 0 : p.getViewCount()) + 1);  
        this.updateById(p);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO createProduct(ProductCreateDTO dto, Long sellerId) {
        if (dto.getCategoryId() != null) {
            com.secondhand.marketplace.backend.modules.product.entity.Category category = categoryMapper.selectById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException(400, "指定的商品分类不存在");
            }
        }

        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setSellerId(sellerId);
        
        // 状态机处理
        if (Boolean.TRUE.equals(dto.getIsDraft())) {
            product.setPublishStatus(STATUS_DRAFT);
        } else {
            product.setPublishStatus(STATUS_PENDING_REVIEW);
        }
        product.setViewCount(0);
        product.setFavoriteCount(0);
        product.setStock(1);
        product.setCreatedAt(LocalDateTime.now());
        
        this.save(product);
        saveImagesForProduct(product.getId(), dto.getImages());
        return getProductDetail(product.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO updateProduct(ProductUpdateDTO dto, Long sellerId) {
        Product existing = this.getById(dto.getId());
        if (existing == null) {
            return null;
        }

        if (!isAdmin(sellerId)) {
            if (!existing.getSellerId().equals(sellerId)) {
                throw new BusinessException(403, "无权修改他人的商品");
            }
            if (STATUS_ON_SALE.equals(existing.getPublishStatus()) || STATUS_SOLD.equals(existing.getPublishStatus())) {
                throw new BusinessException(400, "当前状态不允许修改");
            }
        }

        if (dto.getCategoryId() != null && categoryMapper.selectById(dto.getCategoryId()) == null) {
            throw new BusinessException(400, "指定的商品分类不存在");
        }

        BeanUtils.copyProperties(dto, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        
        if (Boolean.TRUE.equals(dto.getIsDraft())) {
            existing.setPublishStatus(STATUS_DRAFT);
            // 管理员无条件放过
        } else if (!isAdmin(sellerId)) {
            existing.setPublishStatus(STATUS_PENDING_REVIEW);
        }
        
        this.updateById(existing);
        saveImagesForProduct(existing.getId(), dto.getImages());
        
        return getProductDetail(existing.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long id, Long sellerId) {
        Product existing = this.getById(id);
        if (existing == null) {
            return false;
        }

        if (existing.getSellerId().equals(sellerId)) {
            // 假设任意时刻都能够下架
            // fall through
        } else if (!isAdmin(sellerId)) {
            throw new BusinessException(403, "无权下架他人商品");
        }

        // 状态机流转为下架
        existing.setPublishStatus(STATUS_OFF_SHELF);
        existing.setOffShelfAt(LocalDateTime.now());
        this.updateById(existing);
        return true;
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product p = this.getById(id);
        if (p == null) {
            return null;
        }
        return convertToVO(p);
    }

    @Override
    public PageResult<ProductVO> getProductPage(ProductPageQueryDTO queryDTO, Long currentUserId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Product::getCategoryId, queryDTO.getCategoryId());
        }

        String requestedStatus = queryDTO.getPublishStatus();
        if (StringUtils.hasText(requestedStatus)) {
            requestedStatus = requestedStatus.trim();
        }

        if (isAdmin(currentUserId)) {
            if (StringUtils.hasText(requestedStatus)) {
                queryWrapper.eq(Product::getPublishStatus, requestedStatus);
            }
        } else {
            if (StringUtils.hasText(requestedStatus) && !PUBLIC_QUERYABLE_STATUSES.contains(requestedStatus)) {
                throw new BusinessException(403, "无权限查询该商品状态");
            }
            queryWrapper.eq(Product::getPublishStatus,
                    StringUtils.hasText(requestedStatus) ? requestedStatus : STATUS_ON_SALE);
        }

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper.like(Product::getTitle, queryDTO.getKeyword())
                    .or().like(Product::getDescription, queryDTO.getKeyword()));
        }

        if (queryDTO.getConditionLevels() != null && !queryDTO.getConditionLevels().isEmpty()) {
            queryWrapper.in(Product::getConditionLevel, queryDTO.getConditionLevels());
        }

        if (queryDTO.getPickupCities() != null && !queryDTO.getPickupCities().isEmpty()) {
            queryWrapper.in(Product::getPickupCity, queryDTO.getPickupCities());
        }

        if (queryDTO.getTradeMode() != null && !queryDTO.getTradeMode().isEmpty()) {
            queryWrapper.eq(Product::getTradeMode, queryDTO.getTradeMode());
        }

        if (queryDTO.getMinPrice() != null) {
            queryWrapper.ge(Product::getSellingPrice, queryDTO.getMinPrice());
        }

        if (queryDTO.getMaxPrice() != null) {
            queryWrapper.le(Product::getSellingPrice, queryDTO.getMaxPrice());
        }
        
        queryWrapper.orderByDesc(Product::getCreatedAt);

        Page<Product> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        this.page(page, queryWrapper);

        List<ProductVO> vos = page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), vos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditProduct(Long productId, Long adminId, Boolean approved, String rejectReason) {
        if (!isAdmin(adminId)) {
            throw new BusinessException(403, "无权审核商品");
        }
        Product product = this.getById(productId);
        if (product == null) {
            return false;
        }

        // 可以将 草稿 或 待审核 状态直接变更为 上架 或 驳回
        if (Boolean.TRUE.equals(approved)) {
            product.setPublishStatus(STATUS_ON_SALE);
            product.setRejectReason(null);
            product.setUpdatedAt(LocalDateTime.now());
        } else {
            product.setPublishStatus(STATUS_REJECT);
            product.setRejectReason(rejectReason);
            product.setUpdatedAt(LocalDateTime.now());
        }
        
        return this.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitForReview(Long id, Long sellerId) {
        Product product = this.getById(id);
        if (product == null || (!product.getSellerId().equals(sellerId) && !isAdmin(sellerId))) return false;
        if (STATUS_DRAFT.equals(product.getPublishStatus()) || STATUS_REJECT.equals(product.getPublishStatus())) {
            product.setPublishStatus(STATUS_PENDING_REVIEW);
            product.setUpdatedAt(LocalDateTime.now());
            return this.updateById(product);
        }
        throw new BusinessException(400, "仅草稿或已拒绝状态可提交审核");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeReview(Long id, Long sellerId) {
        Product product = this.getById(id);
        if (product == null || (!product.getSellerId().equals(sellerId) && !isAdmin(sellerId))) return false;
        if (STATUS_PENDING_REVIEW.equals(product.getPublishStatus())) {
            product.setPublishStatus(STATUS_DRAFT);
            product.setUpdatedAt(LocalDateTime.now());
            return this.updateById(product);
        }
        throw new BusinessException(400, "仅待审核状态可撤销");
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean takeOffShelf(Long id, Long sellerId) {
//        Product product = this.getById(id);
//        if (product == null || !product.getSellerId().equals(sellerId)) return false;
//        if (STATUS_ON_SALE.equals(product.getPublishStatus())) {
//            product.setPublishStatus(STATUS_OFF_SHELF);
//            product.setUpdatedAt(LocalDateTime.now());
//            return this.updateById(product);
//        }
//        throw new BusinessException(400, "仅上架状态可下架");
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean relistProduct(Long id, Long sellerId) {
        Product product = this.getById(id);
        if (product == null || (!product.getSellerId().equals(sellerId) && !isAdmin(sellerId))) return false;
        if (STATUS_OFF_SHELF.equals(product.getPublishStatus())) {
            product.setPublishStatus(STATUS_PENDING_REVIEW);
            product.setUpdatedAt(LocalDateTime.now());
            return this.updateById(product);
        }
        throw new BusinessException(400, "仅下架状态可重新上架");
    }

    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        UserAccount user = userAccountMapper.selectById(userId);
        return user != null && Integer.valueOf(1).equals(user.getIsAdmin());
    }

    private void saveImagesForProduct(Long productId, List<ProductImageDTO> images) {
        if (images != null) {
            List<ProductImage> imageEntities = images.stream().map(img -> {
                ProductImage pi = new ProductImage();
                BeanUtils.copyProperties(img, pi);
                pi.setCreatedAt(LocalDateTime.now());
                return pi;
            }).collect(Collectors.toList());
            productImageService.saveImages(productId, imageEntities);
        }
    }

    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        List<ProductImage> images = productImageService.getImagesByProductId(product.getId());
        List<ProductImageVO> imgVos = images.stream().map(img -> {
            ProductImageVO ivo = new ProductImageVO();
            BeanUtils.copyProperties(img, ivo);
            return ivo;
        }).collect(Collectors.toList());
        vo.setImages(imgVos);
        return vo;
    }
}

