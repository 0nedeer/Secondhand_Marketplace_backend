package com.secondhand.marketplace.backend.modules.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.common.context.UserContext;
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

    private static final Set<String> PUBLIC_QUERYABLE_STATUSES = Set.of(Product.STATUS_ON_SALE, Product.STATUS_SOLD);

    private static final String SORT_ASC = "asc";
    private static final String SORT_DESC = "desc";

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Override
    public boolean addViewCount(Long id) {
        Product p = this.getById(id);
        if (p == null || Product.STATUS_DELETED.equals(p.getPublishStatus())) {
            return false;
        }
        // 原子更新，避免并发竞态
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, id)
                .setSql("view_count = view_count + 1");
        return this.update(updateWrapper);
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
            product.setPublishStatus(Product.STATUS_DRAFT);
        } else {
            product.setPublishStatus(Product.STATUS_PENDING_REVIEW);
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
        if (existing == null || Product.STATUS_DELETED.equals(existing.getPublishStatus())) {
            return null;
        }

        if (!isAdmin(sellerId)) {
            if (!existing.getSellerId().equals(sellerId)) {
                throw new BusinessException(403, "无权修改他人的商品");
            }
            if (Product.STATUS_ON_SALE.equals(existing.getPublishStatus()) || Product.STATUS_SOLD.equals(existing.getPublishStatus())) {
                throw new BusinessException(400, "当前状态不允许修改");
            }
        }

        if (dto.getCategoryId() != null && categoryMapper.selectById(dto.getCategoryId()) == null) {
            throw new BusinessException(400, "指定的商品分类不存在");
        }

        BeanUtils.copyProperties(dto, existing);
        existing.setUpdatedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(dto.getIsDraft())) {
            existing.setPublishStatus(Product.STATUS_DRAFT);
            // 管理员无条件放过
        } else if (!isAdmin(sellerId)) {
            existing.setPublishStatus(Product.STATUS_PENDING_REVIEW);
        }
        
        this.updateById(existing);
        saveImagesForProduct(existing.getId(), dto.getImages());
        
        return getProductDetail(existing.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offShelfProduct(Long id, Long sellerId) {
        Product existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "商品不存在");
        }

        if (!existing.getSellerId().equals(sellerId) && !isAdmin(sellerId)) {
            throw new BusinessException(403, "无权下架他人商品");
        }

        if (Product.STATUS_DELETED.equals(existing.getPublishStatus())) {
            throw new BusinessException(400, "该商品已被删除");
        }

        if (!Product.STATUS_ON_SALE.equals(existing.getPublishStatus()) && !Product.STATUS_SOLD.equals(existing.getPublishStatus())) {
            throw new BusinessException(400, "仅上架中或已售的商品可以下架");
        }

        existing.setPublishStatus(Product.STATUS_OFF_SHELF);
        existing.setOffShelfAt(LocalDateTime.now());
        this.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id, Long sellerId) {
        Product existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "商品不存在");
        }

        // 只有卖家自己或管理员可删除
        if (!existing.getSellerId().equals(sellerId) && !isAdmin(sellerId)) {
            throw new BusinessException(403, "无权删除他人商品");
        }

        // 已删除的不能重复删除
        if (Product.STATUS_DELETED.equals(existing.getPublishStatus())) {
            throw new BusinessException(400, "该商品已被删除");
        }

        existing.setPublishStatus(Product.STATUS_DELETED);
        this.updateById(existing);
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product p = this.getById(id);
        if (p == null || Product.STATUS_DELETED.equals(p.getPublishStatus())) {
            return null;
        }
        
        Long currentUserId = UserContext.getCurrentUserId();
        
        // 只有管理员、商品作者，或者该商品属于公开的查询状态（上架、已售）时才允许查阅。
        boolean isPublic = PUBLIC_QUERYABLE_STATUSES.contains(p.getPublishStatus());
        boolean isOwner = currentUserId != null && currentUserId.equals(p.getSellerId());
        boolean isAdminUser = isAdmin(currentUserId);
        
        if (!isPublic && !isOwner && !isAdminUser) {
            throw new BusinessException(403, "商品未上架，无权查看详情");
        }
        
        return convertToVO(p);
    }

    @Override
    public PageResult<ProductVO> getProductPage(ProductPageQueryDTO queryDTO, Long currentUserId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();

        // 已删除商品对所有用户不可见
        queryWrapper.ne(Product::getPublishStatus, Product.STATUS_DELETED);

        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Product::getCategoryId, queryDTO.getCategoryId());
        }

        String requestedStatus = queryDTO.getPublishStatus();
        if (StringUtils.hasText(requestedStatus)) {
            requestedStatus = requestedStatus.trim();
        }

        if (!isAdmin(currentUserId)) {
            if (!StringUtils.hasText(requestedStatus)) {
                queryWrapper.in(Product::getPublishStatus, PUBLIC_QUERYABLE_STATUSES);
            } else {
                if (!PUBLIC_QUERYABLE_STATUSES.contains(requestedStatus)) {
                    throw new BusinessException(403, "无权限查询该商品状态");
                }

                queryWrapper.eq(Product::getPublishStatus, requestedStatus);
            }

        } else {
            if (StringUtils.hasText(requestedStatus)) {
                queryWrapper.eq(Product::getPublishStatus, requestedStatus);
            }
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

        applySortOrder(queryWrapper, queryDTO.getSortOrder());

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
        if (product == null || Product.STATUS_DELETED.equals(product.getPublishStatus())) {
            return false;
        }

        // 只能审核 草稿 或 待审核 状态的商品
        if (!Product.STATUS_DRAFT.equals(product.getPublishStatus()) && !Product.STATUS_PENDING_REVIEW.equals(product.getPublishStatus())) {
            throw new BusinessException(400, "仅草稿或待审核状态的商品可审核");
        }

        if (Boolean.TRUE.equals(approved)) {
            product.setPublishStatus(Product.STATUS_ON_SALE);
            product.setRejectReason(null);
            // 首次上架记录发布时间
            if (product.getPublishedAt() == null) {
                product.setPublishedAt(LocalDateTime.now());
            }
            product.setUpdatedAt(LocalDateTime.now());
        } else {
            product.setPublishStatus(Product.STATUS_REJECT);
            product.setRejectReason(rejectReason);
            product.setUpdatedAt(LocalDateTime.now());
        }

        return this.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitForReview(Long id, Long sellerId) {
        Product product = this.getById(id);
        if (product == null || Product.STATUS_DELETED.equals(product.getPublishStatus()) || (!product.getSellerId().equals(sellerId) && !isAdmin(sellerId))) return false;
        if (Product.STATUS_DRAFT.equals(product.getPublishStatus()) || Product.STATUS_REJECT.equals(product.getPublishStatus())) {
            product.setPublishStatus(Product.STATUS_PENDING_REVIEW);
            product.setUpdatedAt(LocalDateTime.now());
            return this.updateById(product);
        }
        throw new BusinessException(400, "仅草稿或已拒绝状态可提交审核");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean revokeReview(Long id, Long sellerId) {
        Product product = this.getById(id);
        if (product == null || Product.STATUS_DELETED.equals(product.getPublishStatus()) || (!product.getSellerId().equals(sellerId) && !isAdmin(sellerId))) return false;
        if (Product.STATUS_PENDING_REVIEW.equals(product.getPublishStatus())) {
            product.setPublishStatus(Product.STATUS_DRAFT);
            product.setUpdatedAt(LocalDateTime.now());
            return this.updateById(product);
        }
        throw new BusinessException(400, "仅待审核状态可撤销");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean relistProduct(Long id, Long sellerId) {
        Product product = this.getById(id);
        if (product == null || Product.STATUS_DELETED.equals(product.getPublishStatus()) || (!product.getSellerId().equals(sellerId) && !isAdmin(sellerId))) return false;
        if (Product.STATUS_OFF_SHELF.equals(product.getPublishStatus())) {
            product.setPublishStatus(Product.STATUS_PENDING_REVIEW);
            product.setUpdatedAt(LocalDateTime.now());
            return this.updateById(product);
        }
        throw new BusinessException(400, "仅下架状态可重新上架");
    }

    @Override
    public PageResult<ProductVO> getMyProductPage(ProductPageQueryDTO queryDTO, Long currentUserId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getSellerId, currentUserId);

        // 已删除商品不可见
        queryWrapper.ne(Product::getPublishStatus, Product.STATUS_DELETED);

        // 如果传了具体的状态筛选，如 'draft', 则过滤；否则查出该用户所有状态的商品
        if (StringUtils.hasText(queryDTO.getPublishStatus())) {
            queryWrapper.eq(Product::getPublishStatus, queryDTO.getPublishStatus().trim());
        }

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper.like(Product::getTitle, queryDTO.getKeyword())
                    .or().like(Product::getDescription, queryDTO.getKeyword()));
        }

        applySortOrder(queryWrapper, queryDTO.getSortOrder());

        Page<Product> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        this.page(page, queryWrapper);

        List<ProductVO> vos = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), vos);
    }

    @Override
    public PageResult<ProductVO> getSellerProducts(Long sellerId, ProductPageQueryDTO queryDTO) {
        // 只查询公开的商品
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getSellerId, sellerId);

        // 已删除商品不可见
        queryWrapper.ne(Product::getPublishStatus, Product.STATUS_DELETED);

        String requestedStatus = queryDTO.getPublishStatus();
        if (StringUtils.hasText(requestedStatus)) {
            requestedStatus = requestedStatus.trim();
        }

        if (!isAdmin(UserContext.getCurrentUserId())) {
            if (!StringUtils.hasText(requestedStatus)) {
                queryWrapper.in(Product::getPublishStatus, PUBLIC_QUERYABLE_STATUSES);
            } else {
                if (!PUBLIC_QUERYABLE_STATUSES.contains(requestedStatus)) {
                    throw new BusinessException(403, "无权限查询该商品状态");
                }

                queryWrapper.eq(Product::getPublishStatus, requestedStatus);
            }
        } else {
            if (StringUtils.hasText(requestedStatus)) {
                queryWrapper.eq(Product::getPublishStatus, requestedStatus);
            } else {
                // all
            }
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

        if (StringUtils.hasText(queryDTO.getTradeMode())) {
            queryWrapper.eq(Product::getTradeMode, queryDTO.getTradeMode());
        }

        if (queryDTO.getMinPrice() != null) {
            queryWrapper.ge(Product::getSellingPrice, queryDTO.getMinPrice());
        }

        if (queryDTO.getMaxPrice() != null) {
            queryWrapper.le(Product::getSellingPrice, queryDTO.getMaxPrice());
        }

        applySortOrder(queryWrapper, queryDTO.getSortOrder());

        Page<Product> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        this.page(page, queryWrapper);

        List<ProductVO> vos = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(page.getTotal(), vos);
    }

    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        UserAccount user = userAccountMapper.selectById(userId);
        return user != null && Integer.valueOf(1).equals(user.getIsAdmin());
    }

    /**
     * 根据 sortOrder 决定排序方式
     * @param wrapper    query wrapper
     * @param sortOrder  排序方式: "asc"=售价升序, "desc"=售价降序, null/其他=按创建时间倒序
     */
    private void applySortOrder(LambdaQueryWrapper<Product> wrapper, String sortOrder) {
        if (SORT_ASC.equalsIgnoreCase(sortOrder)) {
            wrapper.orderByAsc(Product::getSellingPrice);
        } else if (SORT_DESC.equalsIgnoreCase(sortOrder)) {
            wrapper.orderByDesc(Product::getSellingPrice);
        } else {
            // 默认：按创建时间倒序
            wrapper.orderByDesc(Product::getCreatedAt);
        }
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

