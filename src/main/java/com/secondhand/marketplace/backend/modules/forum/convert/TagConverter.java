package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.TagCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.TagUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumTag;
import com.secondhand.marketplace.backend.modules.forum.vo.TagVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TagConverter {
    
    TagConverter INSTANCE = Mappers.getMapper(TagConverter.class);
    
    /**
     * CreateDTO转Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isEnabled", constant = "1")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ForumTag toEntity(TagCreateDTO dto);
    
    /**
     * UpdateDTO更新Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(TagUpdateDTO dto, @MappingTarget ForumTag entity);
    
    /**
     * Entity转TagVO
     */
    @Mapping(target = "isFollowed", ignore = true)
    TagVO toVo(ForumTag entity);
    
    /**
     * Entity列表转TagVO列表
     */
    List<TagVO> toVoList(List<ForumTag> entities);
}