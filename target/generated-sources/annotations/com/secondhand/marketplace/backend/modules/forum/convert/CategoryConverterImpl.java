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
    date = "2026-04-09T21:51:27+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class CategoryConverterImpl implements CategoryConverter {

    @Override
    public ForumCategory toEntity(CategoryCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ForumCategory.ForumCategoryBuilder forumCategory = ForumCategory.builder();

        forumCategory.parentId( dto.getParentId() );
        forumCategory.name( dto.getName() );
        forumCategory.icon( dto.getIcon() );
        forumCategory.sortOrder( dto.getSortOrder() );

        forumCategory.isEnabled( 1 );

        return forumCategory.build();
    }

    @Override
    public void updateEntity(CategoryUpdateDTO dto, ForumCategory entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.getName() );
        entity.setIcon( dto.getIcon() );
        entity.setSortOrder( dto.getSortOrder() );
        entity.setIsEnabled( dto.getIsEnabled() );
    }

    @Override
    public CategoryVO toVo(ForumCategory entity) {
        if ( entity == null ) {
            return null;
        }

        CategoryVO categoryVO = new CategoryVO();

        categoryVO.setId( entity.getId() );
        categoryVO.setName( entity.getName() );
        categoryVO.setIcon( entity.getIcon() );
        categoryVO.setParentId( entity.getParentId() );
        categoryVO.setSortOrder( entity.getSortOrder() );
        categoryVO.setIsEnabled( entity.getIsEnabled() );

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
