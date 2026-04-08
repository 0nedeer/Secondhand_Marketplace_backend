package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumCollect;
import com.secondhand.marketplace.backend.modules.forum.vo.CollectVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CollectConverter {
    
    CollectConverter INSTANCE = Mappers.getMapper(CollectConverter.class);
    
    /**
     * Entity转CollectVO
     */
    @Mapping(target = "postTitle", ignore = true)
    @Mapping(target = "postAuthor", ignore = true)
    @Mapping(target = "postCreatedAt", ignore = true)
    @Mapping(target = "postViewCount", ignore = true)
    @Mapping(target = "postCommentCount", ignore = true)
    CollectVO toVo(ForumCollect entity);
    
    /**
     * Entity列表转CollectVO列表
     */
    List<CollectVO> toVoList(List<ForumCollect> entities);
}