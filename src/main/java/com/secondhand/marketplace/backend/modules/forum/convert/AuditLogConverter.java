package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.ForumAuditLog;
import com.secondhand.marketplace.backend.modules.forum.vo.AuditLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogConverter {
    
    AuditLogConverter INSTANCE = Mappers.getMapper(AuditLogConverter.class);
    
    /**
     * Entity转AuditLogVO
     */
    @Mapping(target = "auditorInfo", ignore = true)
    AuditLogVO toVo(ForumAuditLog entity);
    
    /**
     * Entity列表转AuditLogVO列表
     */
    List<AuditLogVO> toVoList(List<ForumAuditLog> entities);
}