package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.PostCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumPost;
import com.secondhand.marketplace.backend.modules.forum.vo.PostListVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PostVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-20T20:02:29+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class PostConverterImpl implements PostConverter {

    @Override
    public ForumPost toEntity(PostCreateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ForumPost.ForumPostBuilder forumPost = ForumPost.builder();

        forumPost.categoryId( dto.getCategoryId() );
        forumPost.postType( dto.getPostType() );
        forumPost.productId( dto.getProductId() );
        forumPost.title( dto.getTitle() );
        forumPost.content( dto.getContent() );

        forumPost.isDeleted( 0 );
        forumPost.auditStatus( "pending" );
        forumPost.displayStatus( "normal" );

        return forumPost.build();
    }

    @Override
    public void updateEntity(PostUpdateDTO dto, ForumPost entity) {
        if ( dto == null ) {
            return;
        }

        entity.setCategoryId( dto.getCategoryId() );
        entity.setTitle( dto.getTitle() );
        entity.setContent( dto.getContent() );
    }

    @Override
    public PostVO toVo(ForumPost entity) {
        if ( entity == null ) {
            return null;
        }

        PostVO postVO = new PostVO();

        postVO.setId( entity.getId() );
        postVO.setTitle( entity.getTitle() );
        postVO.setContent( entity.getContent() );
        postVO.setPostType( entity.getPostType() );
        postVO.setLikeCount( entity.getLikeCount() );
        postVO.setCommentCount( entity.getCommentCount() );
        postVO.setShareCount( entity.getShareCount() );
        postVO.setCollectCount( entity.getCollectCount() );
        postVO.setViewCount( entity.getViewCount() );
        postVO.setAuditStatus( entity.getAuditStatus() );
        postVO.setDisplayStatus( entity.getDisplayStatus() );
        postVO.setPublishedAt( entity.getPublishedAt() );
        postVO.setCreatedAt( entity.getCreatedAt() );

        return postVO;
    }

    @Override
    public PostListVO toListVo(ForumPost entity) {
        if ( entity == null ) {
            return null;
        }

        PostListVO postListVO = new PostListVO();

        postListVO.setId( entity.getId() );
        postListVO.setTitle( entity.getTitle() );
        postListVO.setPostType( entity.getPostType() );
        postListVO.setLikeCount( entity.getLikeCount() );
        postListVO.setCommentCount( entity.getCommentCount() );
        postListVO.setViewCount( entity.getViewCount() );
        postListVO.setCreatedAt( entity.getCreatedAt() );

        return postListVO;
    }

    @Override
    public List<PostListVO> toListVoList(List<ForumPost> entities) {
        if ( entities == null ) {
            return null;
        }

        List<PostListVO> list = new ArrayList<PostListVO>( entities.size() );
        for ( ForumPost forumPost : entities ) {
            list.add( toListVo( forumPost ) );
        }

        return list;
    }
}
