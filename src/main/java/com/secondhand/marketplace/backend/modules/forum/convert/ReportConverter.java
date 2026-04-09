package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.ReportCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumReport;
import com.secondhand.marketplace.backend.modules.forum.vo.ReportVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportConverter {
    
    ReportConverter INSTANCE = Mappers.getMapper(ReportConverter.class);
    
    /**
     * CreateDTO转Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reporterId", ignore = true)
    @Mapping(target = "reportStatus", constant = "pending")
    @Mapping(target = "handledBy", ignore = true)
    @Mapping(target = "handleResult", ignore = true)
    @Mapping(target = "handledAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ForumReport toEntity(ReportCreateDTO dto);
    
    /**
     * Entity转ReportVO
     */
    @Mapping(target = "targetTitle", ignore = true)
    @Mapping(target = "reporterInfo", ignore = true)
    @Mapping(target = "handlerInfo", ignore = true)
    ReportVO toVo(ForumReport entity);
    
    /**
     * Entity列表转ReportVO列表
     */
    List<ReportVO> toVoList(List<ForumReport> entities);
}