package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.ReportCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumReport;
import com.secondhand.marketplace.backend.modules.forum.vo.ReportVO;
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
public class ReportConverterImpl implements ReportConverter {

    @Override
    public ForumReport toEntity(ReportCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ForumReport.ForumReportBuilder forumReport = ForumReport.builder();

        forumReport.evidenceUrls( dto.getEvidenceUrls() );
        forumReport.reportDetail( dto.getReportDetail() );
        forumReport.reportReason( dto.getReportReason() );
        forumReport.targetId( dto.getTargetId() );
        forumReport.targetType( dto.getTargetType() );

        forumReport.reportStatus( "pending" );

        return forumReport.build();
    }

    @Override
    public ReportVO toVo(ForumReport entity) {
        if ( entity == null ) {
            return null;
        }

        ReportVO reportVO = new ReportVO();

        reportVO.setCreatedAt( entity.getCreatedAt() );
        reportVO.setEvidenceUrls( entity.getEvidenceUrls() );
        reportVO.setHandleResult( entity.getHandleResult() );
        reportVO.setHandledAt( entity.getHandledAt() );
        reportVO.setId( entity.getId() );
        reportVO.setReportDetail( entity.getReportDetail() );
        reportVO.setReportReason( entity.getReportReason() );
        reportVO.setReportStatus( entity.getReportStatus() );
        reportVO.setTargetId( entity.getTargetId() );
        reportVO.setTargetType( entity.getTargetType() );

        return reportVO;
    }

    @Override
    public List<ReportVO> toVoList(List<ForumReport> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ReportVO> list = new ArrayList<ReportVO>( entities.size() );
        for ( ForumReport forumReport : entities ) {
            list.add( toVo( forumReport ) );
        }

        return list;
    }
}
