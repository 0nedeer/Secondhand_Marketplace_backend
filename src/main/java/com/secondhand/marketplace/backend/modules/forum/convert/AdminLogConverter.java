package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.AdminLog;
import com.secondhand.marketplace.backend.modules.forum.vo.AdminLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminLogConverter {
    
    AdminLogConverter INSTANCE = Mappers.getMapper(AdminLogConverter.class);
    
    /**
     * Entity转AdminLogVO
     */
    @Mapping(target = "adminInfo", ignore = true)
    AdminLogVO toVo(AdminLog entity);
    
    /**
     * Entity列表转AdminLogVO列表
     */
    List<AdminLogVO> toVoList(List<AdminLog> entities);
}