package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumAuditLog;
import com.secondhand.marketplace.backend.modules.forum.vo.AuditLogVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-09T21:51:26+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class AuditLogConverterImpl implements AuditLogConverter {

    @Override
    public AuditLogVO toVo(ForumAuditLog entity) {
        if ( entity == null ) {
            return null;
        }

        AuditLogVO auditLogVO = new AuditLogVO();

        auditLogVO.setId( entity.getId() );
        auditLogVO.setTargetType( entity.getTargetType() );
        auditLogVO.setTargetId( entity.getTargetId() );
        auditLogVO.setAction( entity.getAction() );
        auditLogVO.setReason( entity.getReason() );
        auditLogVO.setOldStatus( entity.getOldStatus() );
        auditLogVO.setNewStatus( entity.getNewStatus() );
        auditLogVO.setCreatedAt( entity.getCreatedAt() );

        return auditLogVO;
    }

    @Override
    public List<AuditLogVO> toVoList(List<ForumAuditLog> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AuditLogVO> list = new ArrayList<AuditLogVO>( entities.size() );
        for ( ForumAuditLog forumAuditLog : entities ) {
            list.add( toVo( forumAuditLog ) );
        }

        return list;
    }
}
