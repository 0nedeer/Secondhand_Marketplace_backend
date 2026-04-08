package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumFollowTag;
import com.secondhand.marketplace.backend.modules.forum.vo.FollowTagVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FollowTagConverter {
    
    FollowTagConverter INSTANCE = Mappers.getMapper(FollowTagConverter.class);
    
    /**
     * Entity转FollowTagVO
     */
    @Mapping(target = "tagName", ignore = true)
    @Mapping(target = "tagIcon", ignore = true)
    @Mapping(target = "postCount", ignore = true)
    FollowTagVO toVo(ForumFollowTag entity);
    
    /**
     * Entity列表转FollowTagVO列表
     */
    List<FollowTagVO> toVoList(List<ForumFollowTag> entities);
}