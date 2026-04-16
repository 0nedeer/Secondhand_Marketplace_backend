package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.CategoryCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CategoryUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumCategory;
import com.secondhand.marketplace.backend.modules.forum.vo.CategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryConverter {
    
    /**
     * CreateDTO转Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isEnabled", constant = "1")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ForumCategory toEntity(CategoryCreateDTO dto);
    
    /**
     * UpdateDTO更新Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(CategoryUpdateDTO dto, @MappingTarget ForumCategory entity);
    
    /**
     * Entity转CategoryVO
     */
    CategoryVO toVo(ForumCategory entity);
    
    /**
     * Entity列表转CategoryVO列表
     */
    List<CategoryVO> toVoList(List<ForumCategory> entities);
}