package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumFollowTag;
import com.secondhand.marketplace.backend.modules.forum.vo.FollowTagVO;
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
public class FollowTagConverterImpl implements FollowTagConverter {

    @Override
    public FollowTagVO toVo(ForumFollowTag entity) {
        if ( entity == null ) {
            return null;
        }

        FollowTagVO followTagVO = new FollowTagVO();

        followTagVO.setId( entity.getId() );
        followTagVO.setTagId( entity.getTagId() );
        followTagVO.setCreatedAt( entity.getCreatedAt() );

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
