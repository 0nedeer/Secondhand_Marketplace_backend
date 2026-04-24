package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.AdminLog;
import com.secondhand.marketplace.backend.modules.forum.vo.AdminLogVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-21T14:10:59+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class AdminLogConverterImpl implements AdminLogConverter {

    @Override
    public AdminLogVO toVo(AdminLog entity) {
        if ( entity == null ) {
            return null;
        }

        AdminLogVO adminLogVO = new AdminLogVO();

        adminLogVO.setId( entity.getId() );
        adminLogVO.setTargetType( entity.getTargetType() );
        adminLogVO.setTargetId( entity.getTargetId() );
        adminLogVO.setAction( entity.getAction() );
        adminLogVO.setReason( entity.getReason() );
        adminLogVO.setIpAddress( entity.getIpAddress() );
        adminLogVO.setCreatedAt( entity.getCreatedAt() );

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
