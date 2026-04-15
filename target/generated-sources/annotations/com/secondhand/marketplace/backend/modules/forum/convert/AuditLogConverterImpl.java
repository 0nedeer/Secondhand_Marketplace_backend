package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumAuditLog;
import com.secondhand.marketplace.backend.modules.forum.vo.AuditLogVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T00:31:45+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class AuditLogConverterImpl implements AuditLogConverter {

    @Override
    public AuditLogVO toVo(ForumAuditLog entity) {
        if ( entity == null ) {
            return null;
        }

        AuditLogVO auditLogVO = new AuditLogVO();

        auditLogVO.setAction( entity.getAction() );
        auditLogVO.setCreatedAt( entity.getCreatedAt() );
        auditLogVO.setId( entity.getId() );
        auditLogVO.setNewStatus( entity.getNewStatus() );
        auditLogVO.setOldStatus( entity.getOldStatus() );
        auditLogVO.setReason( entity.getReason() );
        auditLogVO.setTargetId( entity.getTargetId() );
        auditLogVO.setTargetType( entity.getTargetType() );

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
