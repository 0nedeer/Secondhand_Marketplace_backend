package com.secondhand.marketplace.backend.modules.forum.service.impl;

import com.secondhand.marketplace.backend.modules.forum.convert.CategoryConverter;
import com.secondhand.marketplace.backend.modules.forum.dto.CategoryCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CategoryUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumCategory;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumCategoryMapper;
import com.secondhand.marketplace.backend.modules.forum.service.CategoryService;
import com.secondhand.marketplace.backend.modules.forum.vo.CategoryVO;
import com.secondhand.marketplace.backend.modules.user.service.UserService;
import com.secondhand.marketplace.backend.modules.user.vo.UserPermissionsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CategoryServiceImpl implements CategoryService {
    
    private final ForumCategoryMapper categoryMapper;
    private final UserService userService;
    private final CategoryConverter categoryConverter;
    
    @Override
    public Long createCategory(Long adminId, CategoryCreateDTO dto) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限创建分类");
        }
        
        // 校验父分类是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            ForumCategory parentCategory = categoryMapper.selectById(dto.getParentId());
            if (parentCategory == null) {
                throw new RuntimeException("父分类不存在");
            }
        }
        
        // DTO转Entity
        ForumCategory category = categoryConverter.toEntity(dto);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        // 保存分类
        categoryMapper.insert(category);
        
        log.info("管理员 {} 创建分类成功，分类ID：{}", adminId, category.getId());
        return category.getId();
    }
    
    @Override
    public void updateCategory(Long adminId, CategoryUpdateDTO dto) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限更新分类");
        }
        
        // 校验分类是否存在
        ForumCategory category = categoryMapper.selectById(dto.getId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 校验父分类是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            ForumCategory parentCategory = categoryMapper.selectById(dto.getParentId());
            if (parentCategory == null) {
                throw new RuntimeException("父分类不存在");
            }
            // 避免循环依赖
            if (dto.getParentId().equals(dto.getId())) {
                throw new RuntimeException("分类不能作为自己的父分类");
            }
        }
        
        // 更新分类
        categoryConverter.updateEntity(dto, category);
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        
        log.info("管理员 {} 更新分类成功，分类ID：{}", adminId, dto.getId());
    }
    
    @Override
    public void deleteCategory(Long adminId, Long categoryId) {
        // 权限校验
        UserPermissionsVO permissions = userService.getUserPermissions(adminId);
        if (!permissions.getIsAdmin()) {
            throw new RuntimeException("无权限删除分类");
        }
        
        // 校验分类是否存在
        ForumCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 校验是否有子分类
        List<ForumCategory> children = categoryMapper.selectByParentId(categoryId);
        if (!children.isEmpty()) {
            throw new RuntimeException("该分类下存在子分类，无法删除");
        }
        
        // 删除分类
        categoryMapper.deleteById(categoryId);
        
        log.info("管理员 {} 删除分类成功，分类ID：{}", adminId, categoryId);
    }
    
    @Override
    public CategoryVO getCategoryById(Long categoryId) {
        ForumCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return categoryConverter.toVo(category);
    }
    
    @Override
    public List<CategoryVO> listCategories() {
        List<ForumCategory> categories = categoryMapper.selectList();
        return categoryConverter.toVoList(categories);
    }
    
    @Override
    public List<CategoryVO> listEnabledCategories() {
        List<ForumCategory> categories = categoryMapper.selectAllEnabled();
        return categoryConverter.toVoList(categories);
    }
    
    @Override
    public List<CategoryVO> getCategoryTree() {
        // 获取所有分类
        List<ForumCategory> allCategories = categoryMapper.selectList();
        List<CategoryVO> allCategoryVOs = categoryConverter.toVoList(allCategories);
        
        // 构建树形结构
        Map<Long, CategoryVO> categoryMap = new HashMap<>();
        List<CategoryVO> rootCategories = new ArrayList<>();
        
        // 先将所有分类放入Map
        for (CategoryVO category : allCategoryVOs) {
            categoryMap.put(category.getId(), category);
        }
        
        // 构建树形结构
        for (CategoryVO category : allCategoryVOs) {
            if (category.getParentId() == null || category.getParentId() == 0) {
                // 根分类
                rootCategories.add(category);
            } else {
                // 子分类
                CategoryVO parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    parent.getChildren().add(category);
                }
            }
        }
        
        return rootCategories;
    }
    
    @Override
    public List<CategoryVO> listByParentId(Long parentId) {
        List<ForumCategory> categories = categoryMapper.selectByParentId(parentId);
        return categoryConverter.toVoList(categories);
    }
}