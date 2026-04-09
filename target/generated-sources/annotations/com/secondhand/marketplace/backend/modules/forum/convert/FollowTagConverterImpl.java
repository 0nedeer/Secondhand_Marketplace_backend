package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumFollowTag;
import com.secondhand.marketplace.backend.modules.forum.vo.FollowTagVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-08T10:36:53+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class FollowTagConverterImpl implements FollowTagConverter {

    @Override
    public FollowTagVO toVo(ForumFollowTag entity) {
        if ( entity == null ) {
            return null;
        }

        FollowTagVO followTagVO = new FollowTagVO();

        followTagVO.setCreatedAt( entity.getCreatedAt() );
        followTagVO.setId( entity.getId() );
        followTagVO.setTagId( entity.getTagId() );

        return followTagVO;
    }

    @Override
    public List<FollowTagVO> toVoList(List<ForumFollowTag> entities) {
        if ( entities == null ) {
            return null;
        }

        List<FollowTagVO> list = new ArrayList<FollowTagVO>( entities.size() );
        for ( ForumFollowTag forumFollowTag : entities ) {
            list.add( toVo( forumFollowTag ) );
        }

        return list;
    }
}
