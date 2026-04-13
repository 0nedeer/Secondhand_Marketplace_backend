package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.CommentCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CommentUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumComment;
import com.secondhand.marketplace.backend.modules.forum.vo.CommentVO;
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
public class CommentConverterImpl implements CommentConverter {

    @Override
    public ForumComment toEntity(CommentCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ForumComment.ForumCommentBuilder forumComment = ForumComment.builder();

        forumComment.postId( dto.getPostId() );
        forumComment.parentCommentId( dto.getParentCommentId() );
        forumComment.replyToUserId( dto.getReplyToUserId() );
        forumComment.content( dto.getContent() );

        forumComment.isDeleted( 0 );
        forumComment.auditStatus( "pending" );

        return forumComment.build();
    }

    @Override
    public void updateEntity(CommentUpdateDTO dto, ForumComment entity) {
        if ( dto == null ) {
            return;
        }

        entity.setContent( dto.getContent() );
    }

    @Override
    public CommentVO toVo(ForumComment entity) {
        if ( entity == null ) {
            return null;
        }

        CommentVO commentVO = new CommentVO();

        commentVO.setId( entity.getId() );
        commentVO.setContent( entity.getContent() );
        commentVO.setLikeCount( entity.getLikeCount() );
        commentVO.setReplyCount( entity.getReplyCount() );
        commentVO.setCreatedAt( entity.getCreatedAt() );

        return commentVO;
    }

    @Override
    public List<CommentVO> toVoList(List<ForumComment> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CommentVO> list = new ArrayList<CommentVO>( entities.size() );
        for ( ForumComment forumComment : entities ) {
            list.add( toVo( forumComment ) );
        }

        return list;
    }
}
