package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.PostCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumPost;
import com.secondhand.marketplace.backend.modules.forum.vo.PostListVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PostVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostConverter {
    
    /**
     * CreateDTO转Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "shareCount", ignore = true)
    @Mapping(target = "collectCount", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "isDeleted", constant = "0")
    @Mapping(target = "auditStatus", constant = "pending")
    @Mapping(target = "displayStatus", constant = "normal")
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "rejectReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ForumPost toEntity(PostCreateDTO dto);
    
    /**
     * UpdateDTO更新Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "postType", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "shareCount", ignore = true)
    @Mapping(target = "collectCount", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    @Mapping(target = "displayStatus", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "rejectReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(PostUpdateDTO dto, @MappingTarget ForumPost entity);
    
    /**
     * Entity转PostVO
     */
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "authorInfo", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "mediaList", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "isCollected", ignore = true)
    PostVO toVo(ForumPost entity);
    
    /**
     * Entity转PostListVO
     */
    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "authorAvatar", ignore = true)
    @Mapping(target = "firstMediaUrl", ignore = true)
    @Mapping(target = "tagNames", ignore = true)
    PostListVO toListVo(ForumPost entity);
    
    /**
     * Entity列表转PostListVO列表
     */
    List<PostListVO> toListVoList(List<ForumPost> entities);
    
    /**
     * 标签ID列表转标签名称字符串
     */
    @Named("tagIdsToNames")
    default String tagIdsToNames(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return "";
        }
        // 实际使用时需要注入TagService查询
        return tagIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}