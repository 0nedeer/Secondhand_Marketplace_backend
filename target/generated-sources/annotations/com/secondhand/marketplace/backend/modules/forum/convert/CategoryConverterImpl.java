package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.CategoryCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CategoryUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumCategory;
import com.secondhand.marketplace.backend.modules.forum.vo.CategoryVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T00:31:46+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CategoryConverterImpl implements CategoryConverter {

    @Override
    public ForumCategory toEntity(CategoryCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ForumCategory.ForumCategoryBuilder forumCategory = ForumCategory.builder();

        forumCategory.icon( dto.getIcon() );
        forumCategory.name( dto.getName() );
        forumCategory.parentId( dto.getParentId() );
        forumCategory.sortOrder( dto.getSortOrder() );

        forumCategory.isEnabled( 1 );

        return forumCategory.build();
    }

    @Override
    public void updateEntity(CategoryUpdateDTO dto, ForumCategory entity) {
        if ( dto == null ) {
            return;
        }

        entity.setIcon( dto.getIcon() );
        entity.setIsEnabled( dto.getIsEnabled() );
        entity.setName( dto.getName() );
        entity.setSortOrder( dto.getSortOrder() );
    }

    @Override
    public CategoryVO toVo(ForumCategory entity) {
        if ( entity == null ) {
            return null;
        }

        CategoryVO categoryVO = new CategoryVO();

        categoryVO.setIcon( entity.getIcon() );
        categoryVO.setId( entity.getId() );
        categoryVO.setIsEnabled( entity.getIsEnabled() );
        categoryVO.setName( entity.getName() );
        categoryVO.setParentId( entity.getParentId() );
        categoryVO.setSortOrder( entity.getSortOrder() );

        return categoryVO;
    }

    @Override
    public List<CategoryVO> toVoList(List<ForumCategory> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CategoryVO> list = new ArrayList<CategoryVO>( entities.size() );
        for ( ForumCategory forumCategory : entities ) {
            list.add( toVo( forumCategory ) );
        }

        return list;
    }
}
