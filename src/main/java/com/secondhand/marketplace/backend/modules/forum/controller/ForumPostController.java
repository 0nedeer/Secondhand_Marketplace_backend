package com.secondhand.marketplace.backend.modules.forum.controller;

import com.secondhand.marketplace.backend.common.api.Result;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.config.TestConfig;
import com.secondhand.marketplace.backend.modules.forum.dto.PostCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostSearchDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.PostUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.service.PostService;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.forum.vo.PostListVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/forum/post")
@RequiredArgsConstructor
@Tag(name = "论坛帖子管理")
public class ForumPostController {

    private final PostService postService;
    private final TestConfig testConfig;

    /**
     * 获取当前登录用户ID（从上下文获取）
     */
    private Long getCurrentUserId() {
        // 从UserContext获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            log.info("从UserContext获取到用户ID: {}", userId);
            return userId;
        }
        
        // 测试模式：使用默认用户ID
        if (testConfig.isEnabled()) {
            log.info("测试模式启用，使用默认用户ID: {}", testConfig.getDefaultUserId());
            return testConfig.getDefaultUserId();
        }
        
        log.warn("未获取到用户ID");
        return null;
    }

    @PostMapping("/create")
    @Operation(summary = "发布帖子")
    public Result<Long> createPost(
            @Valid @RequestBody PostCreateDTO dto) {
        log.info("收到发布帖子请求");
        log.info("请求体: {}", dto);
        Long userId = getCurrentUserId();
        log.info("获取到的用户ID: {}", userId);
        if (userId == null) {
            log.warn("用户未登录，返回401错误");
            return Result.error(401, "请先登录");
        }
        log.info("用户已登录，开始创建帖子");
        Long postId = postService.createPost(userId, dto);
        log.info("帖子创建成功，ID: {}", postId);
        return Result.success(postId);
    }

    @PutMapping("/update")
    @Operation(summary = "编辑帖子")
    public Result<Void> updatePost(
            @Valid @RequestBody PostUpdateDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = postService.updatePost(userId, dto);
        if (!success) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success();
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "删除帖子")
    public Result<Void> deletePost(
            @PathVariable @NotNull Long postId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = postService.deletePost(userId, postId);
        if (!success) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success();
    }

    @GetMapping("/{postId}")
    @Operation(summary = "获取帖子详情")
    public Result<PostVO> getPostDetail(
            @PathVariable @NotNull Long postId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.warn("用户未登录，返回401错误");
            return Result.error(401, "请先登录");
        }
        PostVO vo = postService.getPostDetail(userId, postId);
        if (vo == null) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success(vo);
    }

    @PostMapping("/list")
    @Operation(summary = "分页查询帖子列表")
    public Result<PageResult<PostListVO>> listPosts(
            @RequestBody PostSearchDTO searchDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.warn("用户未登录，返回401错误");
            return Result.error(401, "请先登录");
        }
        PageResult<PostListVO> result = postService.listPosts(userId, searchDTO);
        return Result.success(result);
    }

    @GetMapping("/user/{authorId}")
    @Operation(summary = "查询用户发布的帖子")
    public Result<PageResult<PostListVO>> listUserPosts(
            @PathVariable @NotNull Long authorId,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.warn("用户未登录，返回401错误");
            return Result.error(401, "请先登录");
        }
        PageResult<PostListVO> result = postService.listUserPosts(userId, authorId, pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "点赞/取消点赞帖子")
    public Result<Integer> likePost(
            @PathVariable @NotNull Long postId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Integer likeCount = postService.likePost(userId, postId);
        if (likeCount == null) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success(likeCount);
    }

    @PostMapping("/{postId}/collect")
    @Operation(summary = "收藏/取消收藏帖子")
    public Result<Integer> collectPost(
            @PathVariable @NotNull Long postId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Integer collectCount = postService.collectPost(userId, postId);
        if (collectCount == null) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success(collectCount);
    }

    @PostMapping("/{postId}/share")
    @Operation(summary = "转发帖子")
    public Result<Void> sharePost(
            @PathVariable @NotNull Long postId,
            @RequestParam String channel) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = postService.sharePost(userId, postId, channel);
        if (!success) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success();
    }

    // ==================== 管理员接口 ====================

    @PutMapping("/admin/audit/{postId}")
    @Operation(summary = "审核帖子（管理员）")
    public Result<Void> auditPost(
            @PathVariable @NotNull Long postId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String rejectReason) {
        Long adminId = getCurrentUserId();
        if (adminId == null) {
            return Result.error(401, "请先登录");
        }
        // TODO: 校验管理员权限
        boolean success = postService.auditPost(adminId, postId, approved, rejectReason);
        if (!success) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success();
    }

    @PutMapping("/admin/top/{postId}")
    @Operation(summary = "置顶/取消置顶帖子（管理员）")
    public Result<Void> topPost(
            @PathVariable @NotNull Long postId,
            @RequestParam Boolean top) {
        Long adminId = getCurrentUserId();
        if (adminId == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = postService.topPost(adminId, postId, top);
        if (!success) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success();
    }

    @PutMapping("/admin/feature/{postId}")
    @Operation(summary = "设为/取消精华帖（管理员）")
    public Result<Void> featurePost(
            @PathVariable @NotNull Long postId,
            @RequestParam Boolean featured) {
        Long adminId = getCurrentUserId();
        if (adminId == null) {
            return Result.error(401, "请先登录");
        }
        boolean success = postService.featurePost(adminId, postId, featured);
        if (!success) {
            return Result.error(404, "帖子不存在");
        }
        return Result.success();
    }
}