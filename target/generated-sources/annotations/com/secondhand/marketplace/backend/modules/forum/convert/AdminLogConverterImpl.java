package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.AdminLog;
import com.secondhand.marketplace.backend.modules.forum.vo.AdminLogVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-15T22:21:10+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class AdminLogConverterImpl implements AdminLogConverter {

    @Override
    public AdminLogVO toVo(AdminLog entity) {
        if ( entity == null ) {
            return null;
        }

        AdminLogVO adminLogVO = new AdminLogVO();

        adminLogVO.setAction( entity.getAction() );
        adminLogVO.setCreatedAt( entity.getCreatedAt() );
        adminLogVO.setId( entity.getId() );
        adminLogVO.setIpAddress( entity.getIpAddress() );
        adminLogVO.setReason( entity.getReason() );
        adminLogVO.setTargetId( entity.getTargetId() );
        adminLogVO.setTargetType( entity.getTargetType() );

        return adminLogVO;
    }

    @Override
    public List<AdminLogVO> toVoList(List<AdminLog> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AdminLogVO> list = new ArrayList<AdminLogVO>( entities.size() );
        for ( AdminLog adminLog : entities ) {
            list.add( toVo( adminLog ) );
        }

        return list;
    }
}
