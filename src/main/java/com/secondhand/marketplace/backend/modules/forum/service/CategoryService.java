package com.secondhand.marketplace.backend.modules.forum.service;

import com.secondhand.marketplace.backend.modules.forum.dto.CategoryCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CategoryUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.vo.CategoryVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {
    
    /**
     * 创建分类
     * @param adminId 管理员ID
     * @param dto 分类信息
     * @return 分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    Long createCategory(Long adminId, CategoryCreateDTO dto);
    
    /**
     * 更新分类
     * @param adminId 管理员ID
     * @param dto 更新信息
     */
    @Transactional(rollbackFor = Exception.class)
    void updateCategory(Long adminId, CategoryUpdateDTO dto);
    
    /**
     * 删除分类
     * @param adminId 管理员ID
     * @param categoryId 分类ID
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteCategory(Long adminId, Long categoryId);
    
    /**
     * 获取分类详情
     * @param categoryId 分类ID
     * @return 分类详情VO
     */
    CategoryVO getCategoryById(Long categoryId);
    
    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    List<CategoryVO> listCategories();
    
    /**
     * 获取所有启用的分类列表
     * @return 启用的分类列表
     */
    List<CategoryVO> listEnabledCategories();
    
    /**
     * 获取分类树形结构
     * @return 分类树形结构
     */
    List<CategoryVO> getCategoryTree();
    
    /**
     * 根据父分类ID获取子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<CategoryVO> listByParentId(Long parentId);
}