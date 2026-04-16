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
    date = "2026-04-16T09:14:29+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class ReportConverterImpl implements ReportConverter {

    @Override
    public ForumReport toEntity(ReportCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ForumReport.ForumReportBuilder forumReport = ForumReport.builder();

        forumReport.targetType( dto.getTargetType() );
        forumReport.targetId( dto.getTargetId() );
        forumReport.reportReason( dto.getReportReason() );
        forumReport.reportDetail( dto.getReportDetail() );
        forumReport.evidenceUrls( dto.getEvidenceUrls() );

        forumReport.reportStatus( "pending" );

        return forumReport.build();
    }

    @Override
    public ReportVO toVo(ForumReport entity) {
        if ( entity == null ) {
            return null;
        }

        ReportVO reportVO = new ReportVO();

        reportVO.setId( entity.getId() );
        reportVO.setTargetType( entity.getTargetType() );
        reportVO.setTargetId( entity.getTargetId() );
        reportVO.setReportReason( entity.getReportReason() );
        reportVO.setReportDetail( entity.getReportDetail() );
        reportVO.setEvidenceUrls( entity.getEvidenceUrls() );
        reportVO.setReportStatus( entity.getReportStatus() );
        reportVO.setHandleResult( entity.getHandleResult() );
        reportVO.setCreatedAt( entity.getCreatedAt() );
        reportVO.setHandledAt( entity.getHandledAt() );

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
