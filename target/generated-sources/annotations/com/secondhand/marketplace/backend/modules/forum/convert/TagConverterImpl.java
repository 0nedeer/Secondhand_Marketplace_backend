package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.TagCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.TagUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumTag;
import com.secondhand.marketplace.backend.modules.forum.vo.TagVO;
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
public class TagConverterImpl implements TagConverter {

    @Override
    public ForumTag toEntity(TagCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ForumTag.ForumTagBuilder forumTag = ForumTag.builder();

        forumTag.sortOrder( dto.getSortOrder() );
        forumTag.tagIcon( dto.getTagIcon() );
        forumTag.tagName( dto.getTagName() );

        forumTag.isEnabled( 1 );

        return forumTag.build();
    }

    @Override
    public void updateEntity(TagUpdateDTO dto, ForumTag entity) {
        if ( dto == null ) {
            return;
        }

        entity.setIsEnabled( dto.getIsEnabled() );
        entity.setSortOrder( dto.getSortOrder() );
        entity.setTagIcon( dto.getTagIcon() );
        entity.setTagName( dto.getTagName() );
    }

    @Override
    public TagVO toVo(ForumTag entity) {
        if ( entity == null ) {
            return null;
        }

        TagVO tagVO = new TagVO();

        tagVO.setId( entity.getId() );
        tagVO.setSortOrder( entity.getSortOrder() );
        tagVO.setTagIcon( entity.getTagIcon() );
        tagVO.setTagName( entity.getTagName() );

        return tagVO;
    }

    @Override
    public List<TagVO> toVoList(List<ForumTag> entities) {
        if ( entities == null ) {
            return null;
        }

        List<TagVO> list = new ArrayList<TagVO>( entities.size() );
        for ( ForumTag forumTag : entities ) {
            list.add( toVo( forumTag ) );
        }

        return list;
    }
}
