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
    date = "2026-04-16T00:31:45+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
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
        forumPost.content( dto.getContent() );
        forumPost.postType( dto.getPostType() );
        forumPost.productId( dto.getProductId() );
        forumPost.title( dto.getTitle() );

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
        entity.setContent( dto.getContent() );
        entity.setTitle( dto.getTitle() );
    }

    @Override
    public PostVO toVo(ForumPost entity) {
        if ( entity == null ) {
            return null;
        }

        PostVO postVO = new PostVO();

        postVO.setAuditStatus( entity.getAuditStatus() );
        postVO.setCollectCount( entity.getCollectCount() );
        postVO.setCommentCount( entity.getCommentCount() );
        postVO.setContent( entity.getContent() );
        postVO.setCreatedAt( entity.getCreatedAt() );
        postVO.setDisplayStatus( entity.getDisplayStatus() );
        postVO.setId( entity.getId() );
        postVO.setLikeCount( entity.getLikeCount() );
        postVO.setPostType( entity.getPostType() );
        postVO.setPublishedAt( entity.getPublishedAt() );
        postVO.setShareCount( entity.getShareCount() );
        postVO.setTitle( entity.getTitle() );
        postVO.setViewCount( entity.getViewCount() );

        return postVO;
    }

    @Override
    public PostListVO toListVo(ForumPost entity) {
        if ( entity == null ) {
            return null;
        }

        PostListVO postListVO = new PostListVO();

        postListVO.setCommentCount( entity.getCommentCount() );
        postListVO.setCreatedAt( entity.getCreatedAt() );
        postListVO.setId( entity.getId() );
        postListVO.setLikeCount( entity.getLikeCount() );
        postListVO.setPostType( entity.getPostType() );
        postListVO.setTitle( entity.getTitle() );
        postListVO.setViewCount( entity.getViewCount() );

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
