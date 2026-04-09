package com.secondhand.marketplace.backend.modules.forum.service.impl;

import com.secondhand.marketplace.backend.modules.forum.convert.PostConverter;
import com.secondhand.marketplace.backend.modules.forum.dto.PostCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostSearchDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.entity.*;
import com.secondhand.marketplace.backend.modules.forum.mapper.*;
import com.secondhand.marketplace.backend.modules.forum.service.PostService;
import com.secondhand.marketplace.backend.modules.forum.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
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
    private final UserMapper userMapper;
    private final ForumTagMapper tagMapper;
    private final ForumCategoryMapper categoryMapper;
    private final PostConverter postConverter;
    
    @Override
    public Long createPost(Long userId, PostCreateDTO dto) {
        // 1. 校验用户是否被禁言
        User user = userMapper.selectById(userId);
        if (user.getIsMuted() == 1) {
            if (user.getMuteExpireAt() != null && user.getMuteExpireAt().isAfter(LocalDateTime.now())) {
                throw new RuntimeException("您已被禁言，无法发布帖子");
            }
        }
        
        // 2. DTO转Entity
        ForumPost post = postConverter.toEntity(dto);
        post.setAuthorId(userId);
        
        // 3. 保存帖子
        postMapper.insert(post);
        
        // 4. 保存标签关联
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
        
        log.info("用户 {} 创建帖子成功，帖子ID：{}", userId, post.getId());
        return post.getId();
    }
    
    @Override
    public boolean updatePost(Long userId, PostUpdateDTO dto) {
        // 1. 查询原帖子
        ForumPost post = postMapper.selectById(dto.getId());
        if (post == null) {
            return false;
        }
        
        // 2. 权限校验：只有作者或管理员可以编辑
        if (!post.getAuthorId().equals(userId)) {
            User user = userMapper.selectById(userId);
            if (!"admin".equals(user.getRole()) && !"super_admin".equals(user.getRole())) {
                throw new RuntimeException("无权编辑此帖子");
            }
        }
        
        // 3. 已发布的帖子编辑后需要重新审核
        if ("approved".equals(post.getAuditStatus())) {
            post.setAuditStatus("pending");
        }
        
        // 4. 更新帖子
        postConverter.updateEntity(dto, post);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        // 5. 更新标签关联（先删后增）
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
        
        log.info("用户 {} 更新帖子成功，帖子ID：{}", userId, post.getId());
        return true;
    }
    
    @Override
    public boolean deletePost(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        
        // 权限校验
        if (!post.getAuthorId().equals(userId)) {
            User user = userMapper.selectById(userId);
            if (!"admin".equals(user.getRole()) && !"super_admin".equals(user.getRole())) {
                throw new RuntimeException("无权删除此帖子");
            }
        }
        
        // 软删除
        post.setIsDeleted(1);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        log.info("用户 {} 删除帖子成功，帖子ID：{}", userId, postId);
        return true;
    }
    
    @Override
    public PostVO getPostDetail(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return null;
        }
        
        // 转换基本信息
        PostVO vo = postConverter.toVo(post);
        
        // 填充分类名称
        ForumCategory category = categoryMapper.selectById(post.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        // 填充作者信息
        User author = userMapper.selectById(post.getAuthorId());
        if (author != null) {
            UserInfoVO authorInfo = new UserInfoVO();
            authorInfo.setId(author.getId());
            authorInfo.setUsername(author.getUsername());
            authorInfo.setAvatar(author.getAvatar());
            authorInfo.setBio(author.getBio());
            authorInfo.setCreditScore(author.getCreditScore());
            vo.setAuthorInfo(authorInfo);
        }
        
        // 填充标签列表
        List<ForumPostTag> postTags = postTagMapper.selectByPostId(postId);
        if (!postTags.isEmpty()) {
            List<Long> tagIds = postTags.stream().map(ForumPostTag::getTagId).collect(Collectors.toList());
            // 逐个查询标签
            List<TagVO> tagVOs = new ArrayList<>();
            for (Long tagId : tagIds) {
                ForumTag tag = tagMapper.selectById(tagId);
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
        
        // 填充媒体附件
        List<ForumPostMedia> mediaList = postMediaMapper.selectByPostId(postId);
        List<PostVO.MediaVO> mediaVOs = mediaList.stream().map(m -> {
            PostVO.MediaVO mediaVO = new PostVO.MediaVO();
            mediaVO.setId(m.getId());
            mediaVO.setMediaType(m.getMediaType());
            mediaVO.setMediaUrl(m.getMediaUrl());
            mediaVO.setCoverUrl(m.getCoverUrl());
            mediaVO.setSortNo(m.getSortNo());
            return mediaVO;
        }).collect(Collectors.toList());
        vo.setMediaList(mediaVOs);
        
        // 当前用户的互动状态
        if (userId != null) {
            // 是否点赞
            ForumReaction like = reactionMapper.selectByUserAndTarget(userId, "post", postId);
            vo.setIsLiked(like != null && "like".equals(like.getReactionType()));
            
            // 是否收藏
            ForumCollect collect = collectMapper.selectByUserAndPost(userId, postId);
            vo.setIsCollected(collect != null);
        }
        
        return vo;
    }
    
    @Override
    public PageResult<PostListVO> listPosts(Long userId, PostSearchDTO searchDTO) {
        // 计算offset
        int offset = (searchDTO.getPageNum() - 1) * searchDTO.getPageSize();
        
        // 查询列表
        List<ForumPost> posts = postMapper.selectPageList(
                searchDTO.getCategoryId(),
                searchDTO.getPostType(),
                "approved",  // 只查已审核通过的
                searchDTO.getDisplayStatus(),
                searchDTO.getKeyword(),
                null,  // authorId
                searchDTO.getSortBy(),
                searchDTO.getOrder(),
                offset,
                searchDTO.getPageSize()
        );
        
        // 查询总数
        long total = postMapper.countByCondition(
                searchDTO.getCategoryId(),
                searchDTO.getPostType(),
                "approved",
                searchDTO.getDisplayStatus(),
                searchDTO.getKeyword(),
                null  // authorId
        );
        
        // 转换为VO
        List<PostListVO> voList = new ArrayList<>();
        for (ForumPost post : posts) {
            PostListVO vo = postConverter.toListVo(post);
            
            // 填充作者信息
            User author = userMapper.selectById(post.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getUsername());
                vo.setAuthorAvatar(author.getAvatar());
            }
            
            // 填充首张图片
            List<ForumPostMedia> mediaList = postMediaMapper.selectByPostId(post.getId());
            if (!mediaList.isEmpty()) {
                vo.setFirstMediaUrl(mediaList.get(0).getMediaUrl());
            }
            
            // 填充标签名称
            List<ForumPostTag> postTags = postTagMapper.selectByPostId(post.getId());
            if (!postTags.isEmpty()) {
                List<Long> tagIds = postTags.stream().map(ForumPostTag::getTagId).collect(Collectors.toList());
                List<String> tagNames = new ArrayList<>();
                for (Long tagId : tagIds) {
                    ForumTag tag = tagMapper.selectById(tagId);
                    if (tag != null) {
                        tagNames.add(tag.getTagName());
                    }
                }
                vo.setTagNames(tagNames);
            }
            
            voList.add(vo);
        }
        
        return new PageResult<>(total, searchDTO.getPageNum(), searchDTO.getPageSize(), voList);
    }
    
    @Override
    public PageResult<PostListVO> listUserPosts(Long currentUserId, Long authorId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        
        List<ForumPost> posts = postMapper.selectByAuthorId(authorId, offset, pageSize);
        long total = postMapper.countByCondition(null, null, null, null, null, authorId);
        
        // 转换为VO
        List<PostListVO> voList = posts.stream().map(post -> {
            PostListVO vo = postConverter.toListVo(post);
            User author = userMapper.selectById(post.getAuthorId());
            if (author != null) {
                vo.setAuthorName(author.getUsername());
                vo.setAuthorAvatar(author.getAvatar());
            }
            
            // 填充首张图片
            List<ForumPostMedia> mediaList = postMediaMapper.selectByPostId(post.getId());
            if (!mediaList.isEmpty()) {
                vo.setFirstMediaUrl(mediaList.get(0).getMediaUrl());
            }
            
            // 填充标签名称
            List<ForumPostTag> postTags = postTagMapper.selectByPostId(post.getId());
            if (!postTags.isEmpty()) {
                List<Long> tagIds = postTags.stream().map(ForumPostTag::getTagId).collect(Collectors.toList());
                List<String> tagNames = new ArrayList<>();
                for (Long tagId : tagIds) {
                    ForumTag tag = tagMapper.selectById(tagId);
                    if (tag != null) {
                        tagNames.add(tag.getTagName());
                    }
                }
                vo.setTagNames(tagNames);
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return new PageResult<>(total, pageNum, pageSize, voList);
    }
    
    @Override
    public boolean auditPost(Long adminId, Long postId, Boolean approved, String rejectReason) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        
        if (approved) {
            post.setAuditStatus("approved");
            post.setPublishedAt(LocalDateTime.now());
            post.setRejectReason(null);
        } else {
            post.setAuditStatus("rejected");
            post.setRejectReason(rejectReason);
        }
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        log.info("管理员 {} 审核帖子 {}，结果：{}", adminId, postId, approved ? "通过" : "驳回");
        return true;
    }
    
    @Override
    public boolean topPost(Long adminId, Long postId, Boolean top) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        
        post.setDisplayStatus(top ? "top" : "normal");
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        log.info("管理员 {} {}帖子 {}", adminId, top ? "置顶" : "取消置顶", postId);
        return true;
    }
    
    @Override
    public boolean featurePost(Long adminId, Long postId, Boolean featured) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return false;
        }
        
        post.setDisplayStatus(featured ? "featured" : "normal");
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        log.info("管理员 {} {}精华帖 {}", adminId, featured ? "设为" : "取消", postId);
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
            // 取消点赞
            reactionMapper.deleteById(existing.getId());
            post.setLikeCount(post.getLikeCount() - 1);
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.updateById(post);
            return post.getLikeCount();
        } else {
            // 点赞
            ForumReaction reaction = ForumReaction.builder()
                    .targetType("post")
                    .targetId(postId)
                    .userId(userId)
                    .reactionType("like")
                    .createdAt(LocalDateTime.now())
                    .build();
            reactionMapper.insert(reaction);
            post.setLikeCount(post.getLikeCount() + 1);
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.updateById(post);
            return post.getLikeCount();
        }
    }
    
    @Override
    public Integer collectPost(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return null;
        }
        
        ForumCollect existing = collectMapper.selectByUserAndPost(userId, postId);
        
        if (existing != null) {
            // 取消收藏
            collectMapper.deleteById(existing.getId());
            post.setCollectCount(post.getCollectCount() - 1);
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.updateById(post);
            return post.getCollectCount();
        } else {
            // 收藏
            ForumCollect collect = ForumCollect.builder()
                    .userId(userId)
                    .postId(postId)
                    .createdAt(LocalDateTime.now())
                    .build();
            collectMapper.insert(collect);
            post.setCollectCount(post.getCollectCount() + 1);
            post.setUpdatedAt(LocalDateTime.now());
            postMapper.updateById(post);
            return post.getCollectCount();
        }
    }
    
    @Override
    public void recordView(Long userId, Long postId) {
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            return;
        }
        
        // 更新帖子浏览数
        post.setViewCount(post.getViewCount() + 1);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        // 记录日统计
        LocalDate today = LocalDateTime.now().toLocalDate();
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
                // 简化UV统计
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
        
        // 记录转发
        ForumPostShare share = ForumPostShare.builder()
                .postId(postId)
                .userId(userId)
                .shareChannel(channel)
                .createdAt(LocalDateTime.now())
                .build();
        shareMapper.insert(share);
        
        // 更新转发数
        post.setShareCount(post.getShareCount() + 1);
        post.setUpdatedAt(LocalDateTime.now());
        postMapper.updateById(post);
        
        log.info("用户 {} 转发帖子 {} 到 {}", userId, postId, channel);
        return true;
    }
}