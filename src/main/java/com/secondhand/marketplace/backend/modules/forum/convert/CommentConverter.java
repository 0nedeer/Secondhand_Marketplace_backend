package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.dto.CommentCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CommentUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumComment;
import com.secondhand.marketplace.backend.modules.forum.vo.CommentVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentConverter {
    
    /**
     * CreateDTO转Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commenterId", ignore = true)
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "replyCount", constant = "0")
    @Mapping(target = "isDeleted", constant = "0")
    @Mapping(target = "auditStatus", constant = "pending")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ForumComment toEntity(CommentCreateDTO dto);
    
    /**
     * UpdateDTO更新Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "parentCommentId", ignore = true)
    @Mapping(target = "replyToUserId", ignore = true)
    @Mapping(target = "commenterId", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "auditStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(CommentUpdateDTO dto, @MappingTarget ForumComment entity);
    
    /**
     * Entity转CommentVO
     */
    @Mapping(target = "commenterInfo", ignore = true)
    @Mapping(target = "replyToUserInfo", ignore = true)
    @Mapping(target = "replyList", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    CommentVO toVo(ForumComment entity);
    
    /**
     * Entity列表转CommentVO列表
     */
    List<CommentVO> toVoList(List<ForumComment> entities);
}