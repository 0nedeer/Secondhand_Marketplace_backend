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
import com.secondhand.marketplace.backend.modules.product.mapper.ProductMapper;
import com.secondhand.marketplace.backend.modules.product.service.ProductImageService;
import com.secondhand.marketplace.backend.modules.product.service.ProductService;
import com.secondhand.marketplace.backend.modules.product.vo.PageResult;
import com.secondhand.marketplace.backend.modules.product.vo.ProductImageVO;
import com.secondhand.marketplace.backend.modules.product.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductImageService productImageService;

    @Override
    public void addViewCount(Long id) {
        Product p = this.getById(id);
        if (p != null) {
            p.setViewCount((p.getViewCount() == null ? 0 : p.getViewCount()) + 1);
            this.updateById(p);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO createProduct(ProductCreateDTO dto, Long sellerId) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setSellerId(sellerId);
        
        // 状态机处理
        if (Boolean.TRUE.equals(dto.getIsDraft())) {
            product.setPublishStatus("draft");
        } else {
            product.setPublishStatus("pending_review");
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
            throw new BusinessException(404, "商品不存在");
        }
        if (!existing.getSellerId().equals(sellerId)) {
            throw new BusinessException(403, "无权修改他人的商品");
        }
        // 只能修改未上架的，或下架状态重新变成待审核
        if ("on_sale".equals(existing.getPublishStatus()) || "sold".equals(existing.getPublishStatus())) {
            throw new BusinessException(400, "当前状态不允许修改");
        }
        
        BeanUtils.copyProperties(dto, existing);
        existing.setUpdatedAt(LocalDateTime.now());
        
        if (Boolean.TRUE.equals(dto.getIsDraft())) {
            existing.setPublishStatus("draft");
        } else {
            existing.setPublishStatus("pending_review");
        }
        
        this.updateById(existing);
        saveImagesForProduct(existing.getId(), dto.getImages());
        
        return getProductDetail(existing.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id, Long sellerId) {
        Product existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (!existing.getSellerId().equals(sellerId)) {
            throw new BusinessException(403, "无权下架他人商品");
        }
        
        // 状态机流转为下架
        existing.setPublishStatus("off_shelf");
        existing.setOffShelfAt(LocalDateTime.now());
        this.updateById(existing);
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product p = this.getById(id);
        if (p == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return convertToVO(p);
    }

    @Override
    public PageResult<ProductVO> getProductPage(ProductPageQueryDTO queryDTO) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Product::getCategoryId, queryDTO.getCategoryId());
        }
        if (StringUtils.hasText(queryDTO.getPublishStatus())) {
            queryWrapper.eq(Product::getPublishStatus, queryDTO.getPublishStatus());
        }
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper.like(Product::getTitle, queryDTO.getKeyword())
                    .or().like(Product::getDescription, queryDTO.getKeyword()));
        }
        queryWrapper.orderByDesc(Product::getCreatedAt);

        Page<Product> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        this.page(page, queryWrapper);

        List<ProductVO> vos = page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), vos);
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
