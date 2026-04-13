package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumCollect;
import com.secondhand.marketplace.backend.modules.forum.vo.CollectVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-13T11:29:12+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class CollectConverterImpl implements CollectConverter {

    @Override
    public CollectVO toVo(ForumCollect entity) {
        if ( entity == null ) {
            return null;
        }

        CollectVO collectVO = new CollectVO();

        collectVO.setId( entity.getId() );
        collectVO.setPostId( entity.getPostId() );

        return collectVO;
    }

    @Override
    public List<CollectVO> toVoList(List<ForumCollect> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CollectVO> list = new ArrayList<CollectVO>( entities.size() );
        for ( ForumCollect forumCollect : entities ) {
            list.add( toVo( forumCollect ) );
        }

        return list;
    }
}
