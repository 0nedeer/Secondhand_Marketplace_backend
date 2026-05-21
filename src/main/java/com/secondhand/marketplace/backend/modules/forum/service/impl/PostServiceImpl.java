package com.secondhand.marketplace.backend.modules.forum.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.admin.service.AdminAuthService;
import com.secondhand.marketplace.backend.modules.forum.convert.PostConverter;
import com.secondhand.marketplace.backend.modules.forum.dto.PostCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostSearchDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.AdminLog;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumCategory;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumCollect;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumPost;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumPostMedia;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumPostShare;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumPostTag;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumPostViewDaily;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumReaction;
import com.secondhand.marketplace.backend.modules.forum.entity.ForumTag;
import com.secondhand.marketplace.backend.modules.forum.mapper.AdminLogMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumCategoryMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumCollectMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumPostMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumPostMediaMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumPostShareMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumPostTagMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumPostViewDailyMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumReactionMapper;
import com.secondhand.marketplace.backend.modules.forum.mapper.ForumTagMapper;
import com.secondhand.marketplace.backend.modules.forum.service.PostService;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.forum.vo.PostListVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PostVO;
import com.secondhand.marketplace.backend.modules.forum.vo.TagVO;
import com.secondhand.marketplace.backend.modules.forum.vo.UserInfoVO;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class PostServiceImpl implements PostService {

    private final ForumPostMapper postMapper;
    private final ForumPostTagMapper postTagMapper;
    private final ForumPostMediaMapper postMediaMapper;
    private final ForumReactionMapper reactionMapper;
    private final ForumCollectMapper collectMapper;
    private final ForumPostShareMapper shareMapper;
    private final ForumPostViewDailyMapper viewDailyMapper;
    private final ForumTagMapper tagMapper;
    private final ForumCategoryMapper categoryMapper;
    private final AdminLogMapper adminLogMapper;
    private final UserAccountMapper userAccountMapper;
    private final PostConverter postConverter;
    private final AdminAuthService adminAuthService;

    @Override
    public Long createPost(Long userId, PostCreateDTO dto) {
        UserAccount user = userAccountMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "请先登录");
        }
        if ("banned".equals(user.getUserStatus())) {
            throw new BusinessException(403, "账号已被封禁，无法发帖");
        }

        ForumPost post = postConverter.toEntity(dto);
        post.setAuthorId(userId);
        postMapper.insert(post);

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            for (Long tagId : dto.getTagIds()) {
                ForumPostTag postTag = ForumPostTag.builder()
                        .postId(post.getId())
                        .tagId(tagId)
                        .createdAt(LocalDateTime.now())
                        .build();
                postTagMapper.insert(postTag);
            }
        }
        return post.getId();
    }

    @Override
    public boolean updatePost(Long userId, PostUpdateDTO dto) {
        ForumPost post = postMapper.selectById(dto.getId());
        if (post == null) {
            return false;
        }
        if (!post.getAuthorId().equals(userId) && !adminAuthService.isAdmin(userId)) {
            throw new BusinessException(403, "无权编辑此帖子");
        }
        if ("approved".equals(post.getAuditStatus()) && !adminAuthService.isAdmin(userId)) {
            post.setAuditStatus("pending");
        }
        postConverter.updateEntity(dto, post);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);

        if (dto.getTagIds() != null) {
            postTagMapper.deleteByPostId(post.getId());
            for (Long tagId : dto.getTagIds()) {
                ForumPostTag postTag = ForumPostTag.builder()
                        .postId(post.getId())
                        .tagId(tagId)
                        .createdAt(LocalDateTime.now())
                        .build();
                postTagMapper.insert(postTag);
            }
        }
        return true;
    }

    @Override
    public boolean deletePost(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        boolean admin = adminAuthService.isAdmin(userId);
        if (!post.getAuthorId().equals(userId) && !admin) {
            throw new BusinessException(403, "无权删除此帖子");
        }

        String beforeData = snapshot(post);
        post.setIsDeleted(1);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        if (admin && !post.getAuthorId().equals(userId)) {
            recordAdminLog(userId, "post", postId, "delete_post", "管理员删除帖子", beforeData, snapshot(post));
        }
        return true;
    }

    @Override
    public PostVO getPostDetail(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null || Integer.valueOf(1).equals(post.getIsDeleted())) {
            return null;
        }

        PostVO vo = postConverter.toVo(post);
        vo.setCategoryId(post.getCategoryId());
        vo.setStatus(post.getAuditStatus());
        vo.setIsTop("top".equals(post.getDisplayStatus()));
        vo.setIsFeatured("featured".equals(post.getDisplayStatus()));

        ForumCategory category = categoryMapper.selectById(post.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        UserAccount author = userAccountMapper.selectById(post.getAuthorId());
        if (author != null) {
            UserInfoVO authorInfo = new UserInfoVO();
            authorInfo.setId(author.getId());
            authorInfo.setUsername(author.getNickname() == null ? author.getUsername() : author.getNickname());
            vo.setAuthorInfo(authorInfo);
        }

        List<ForumPostTag> postTags = postTagMapper.selectByPostId(postId);
        if (!postTags.isEmpty()) {
            List<TagVO> tagVOs = new ArrayList<>();
            for (ForumPostTag postTag : postTags) {
                ForumTag tag = tagMapper.selectById(postTag.getTagId());
                if (tag != null) {
                    TagVO tagVO = new TagVO();
                    tagVO.setId(tag.getId());
                    tagVO.setTagName(tag.getTagName());
                    tagVO.setTagIcon(tag.getTagIcon());
                    tagVOs.add(tagVO);
                }
            }
            vo.setTags(tagVOs);
        }

        List<PostVO.MediaVO> mediaVOs = postMediaMapper.selectByPostId(postId).stream().map(media -> {
            PostVO.MediaVO mediaVO = new PostVO.MediaVO();
            mediaVO.setId(media.getId());
            mediaVO.setMediaType(media.getMediaType());
            mediaVO.setMediaUrl(media.getMediaUrl());
            mediaVO.setCoverUrl(media.getCoverUrl());
            mediaVO.setSortNo(media.getSortNo());
            return mediaVO;
        }).collect(Collectors.toList());
        vo.setMediaList(mediaVOs);

        if (userId != null) {
            ForumReaction like = reactionMapper.selectByUserAndTarget(userId, "post", postId);
            vo.setIsLiked(like != null && "like".equals(like.getReactionType()));
            ForumCollect collect = collectMapper.selectByUserAndPost(userId, postId);
            vo.setIsCollected(collect != null);
        }
        return vo;
    }

    @Override
    public PageResult<PostListVO> listPosts(Long userId, PostSearchDTO searchDTO) {
        int pageNum = searchDTO.getPageNum() == null || searchDTO.getPageNum() < 1 ? 1 : searchDTO.getPageNum();
        int pageSize = searchDTO.getPageSize() == null || searchDTO.getPageSize() < 1 ? 10 : searchDTO.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        boolean admin = adminAuthService.isAdmin(userId);
        String auditStatus = admin ? firstText(searchDTO.getStatus(), searchDTO.getAuditStatus()) : "approved";

        List<ForumPost> posts = postMapper.selectPageList(
                searchDTO.getCategoryId(),
                searchDTO.getPostType(),
                auditStatus,
                searchDTO.getDisplayStatus(),
                searchDTO.getKeyword(),
                null,
                sanitizeSortBy(searchDTO.getSortBy()),
                sanitizeOrder(searchDTO.getOrder()),
                offset,
                pageSize
        );

        long total = postMapper.countByCondition(
                searchDTO.getCategoryId(),
                searchDTO.getPostType(),
                auditStatus,
                searchDTO.getDisplayStatus(),
                searchDTO.getKeyword(),
                null
        );

        List<PostListVO> voList = posts.stream().map(this::toListVO).collect(Collectors.toList());
        return new PageResult<>(total, pageNum, pageSize, voList);
    }

    @Override
    public PageResult<PostListVO> listUserPosts(Long currentUserId, Long authorId, Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (safePageNum - 1) * safePageSize;
        List<ForumPost> posts = postMapper.selectByAuthorId(authorId, offset, safePageSize);
        long total = postMapper.countByCondition(null, null, null, null, null, authorId);
        List<PostListVO> voList = posts.stream().map(this::toListVO).collect(Collectors.toList());
        return new PageResult<>(total, safePageNum, safePageSize, voList);
    }

    @Override
    public boolean auditPost(Long adminId, Long postId, Boolean approved, String rejectReason) {
        adminAuthService.requireAdmin(adminId);
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        if (Boolean.FALSE.equals(approved) && (rejectReason == null || rejectReason.isBlank())) {
            throw new BusinessException(400, "驳回原因不能为空");
        }

        String beforeData = snapshot(post);
        if (Boolean.TRUE.equals(approved)) {
            post.setAuditStatus("approved");
            post.setPublishedAt(LocalDateTime.now());
            post.setRejectReason(null);
        } else {
            post.setAuditStatus("rejected");
            post.setRejectReason(rejectReason);
        }
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        recordAdminLog(adminId, "post", postId, Boolean.TRUE.equals(approved) ? "approve_post" : "reject_post",
                rejectReason, beforeData, snapshot(post));
        return true;
    }

    @Override
    public boolean topPost(Long adminId, Long postId, Boolean top) {
        adminAuthService.requireAdmin(adminId);
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        String beforeData = snapshot(post);
        post.setDisplayStatus(Boolean.TRUE.equals(top) ? "top" : "normal");
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        recordAdminLog(adminId, "post", postId, Boolean.TRUE.equals(top) ? "top_post" : "untop_post",
                null, beforeData, snapshot(post));
        return true;
    }

    @Override
    public boolean featurePost(Long adminId, Long postId, Boolean featured) {
        adminAuthService.requireAdmin(adminId);
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        String beforeData = snapshot(post);
        post.setDisplayStatus(Boolean.TRUE.equals(featured) ? "featured" : "normal");
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        recordAdminLog(adminId, "post", postId, Boolean.TRUE.equals(featured) ? "feature_post" : "unfeature_post",
                null, beforeData, snapshot(post));
        return true;
    }

    @Override
    public Integer likePost(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return null;
        }
        ForumReaction existing = reactionMapper.selectByUserAndTarget(userId, "post", postId);
        if (existing != null) {
            reactionMapper.deleteById(existing.getId());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        } else {
            ForumReaction reaction = ForumReaction.builder()
                    .targetType("post")
                    .targetId(postId)
                    .userId(userId)
                    .reactionType("like")
                    .createdAt(LocalDateTime.now())
                    .build();
            reactionMapper.insert(reaction);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        return post.getLikeCount();
    }

    @Override
    public Integer collectPost(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return null;
        }
        ForumCollect existing = collectMapper.selectByUserAndPost(userId, postId);
        if (existing != null) {
            collectMapper.deleteById(existing.getId());
            post.setCollectCount(Math.max(0, post.getCollectCount() - 1));
        } else {
            ForumCollect collect = ForumCollect.builder()
                    .userId(userId)
                    .postId(postId)
                    .createdAt(LocalDateTime.now())
                    .build();
            collectMapper.insert(collect);
            post.setCollectCount(post.getCollectCount() + 1);
        }
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        return post.getCollectCount();
    }

    @Override
    public void recordView(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return;
        }
        post.setViewCount(post.getViewCount() + 1);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);

        LocalDate today = LocalDate.now();
        ForumPostViewDaily daily = viewDailyMapper.selectByPostIdAndDate(postId, today.toString());
        if (daily == null) {
            daily = ForumPostViewDaily.builder()
                    .postId(postId)
                    .statDate(today)
                    .uvCount(userId != null ? 1 : 0)
                    .pvCount(1)
                    .createdAt(LocalDateTime.now())
                    .build();
            viewDailyMapper.insert(daily);
        } else {
            daily.setPvCount(daily.getPvCount() + 1);
            if (userId != null) {
                daily.setUvCount(daily.getUvCount() + 1);
            }
            viewDailyMapper.updateById(daily);
        }
    }

    @Override
    public boolean sharePost(Long userId, Long postId, String channel) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        ForumPostShare share = ForumPostShare.builder()
                .postId(postId)
                .userId(userId)
                .shareChannel(channel)
                .createdAt(LocalDateTime.now())
                .build();
        shareMapper.insert(share);
        post.setShareCount(post.getShareCount() + 1);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        return true;
    }

    private PostListVO toListVO(ForumPost post) {
        PostListVO vo = postConverter.toListVo(post);
        vo.setAuthorId(post.getAuthorId());
        vo.setCategoryId(post.getCategoryId());
        vo.setStatus(post.getAuditStatus());
        vo.setIsTop("top".equals(post.getDisplayStatus()));
        vo.setIsFeatured("featured".equals(post.getDisplayStatus()));
        vo.setUpdatedAt(post.getUpdatedAt());
        if (post.getContent() != null) {
            vo.setContent(post.getContent().length() > 120 ? post.getContent().substring(0, 120) : post.getContent());
        }

        UserAccount author = userAccountMapper.selectById(post.getAuthorId());
        if (author != null) {
            vo.setAuthorName(author.getNickname() == null ? author.getUsername() : author.getNickname());
        }
        ForumCategory category = categoryMapper.selectById(post.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        List<ForumPostMedia> mediaList = postMediaMapper.selectByPostId(post.getId());
        if (!mediaList.isEmpty()) {
            vo.setFirstMediaUrl(mediaList.get(0).getMediaUrl());
        }
        List<ForumPostTag> postTags = postTagMapper.selectByPostId(post.getId());
        if (!postTags.isEmpty()) {
            List<String> tagNames = new ArrayList<>();
            for (ForumPostTag postTag : postTags) {
                ForumTag tag = tagMapper.selectById(postTag.getTagId());
                if (tag != null) {
                    tagNames.add(tag.getTagName());
                }
            }
            vo.setTagNames(tagNames);
        }
        return vo;
    }

    private String firstText(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first.trim();
        }
        if (second != null && !second.isBlank()) {
            return second.trim();
        }
        return null;
    }

    private String sanitizeSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return null;
        }
        Set<String> allowed = Set.of("created_at", "published_at", "like_count", "view_count", "comment_count");
        String normalized = sortBy.trim();
        return allowed.contains(normalized) ? normalized : "created_at";
    }

    private String sanitizeOrder(String order) {
        if (order == null || order.isBlank()) {
            return null;
        }
        String normalized = order.trim().toUpperCase(Locale.ROOT);
        return "ASC".equals(normalized) ? "ASC" : "DESC";
    }

    private void recordAdminLog(Long adminId,
                                String targetType,
                                Long targetId,
                                String action,
                                String reason,
                                String beforeData,
                                String afterData) {
        AdminLog log = new AdminLog();
        log.setAdminId(adminId);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setAction(action);
        log.setReason(reason);
        log.setBeforeData(beforeData);
        log.setAfterData(afterData);
        log.setCreatedAt(LocalDateTime.now());
        adminLogMapper.insert(log);
    }

    private String snapshot(ForumPost post) {
        if (post == null) {
            return null;
        }
        return String.format(Locale.ROOT,
                "{\"id\":%d,\"auditStatus\":\"%s\",\"displayStatus\":\"%s\",\"isDeleted\":%d}",
                post.getId(), post.getAuditStatus(), post.getDisplayStatus(), post.getIsDeleted());
    }
}
