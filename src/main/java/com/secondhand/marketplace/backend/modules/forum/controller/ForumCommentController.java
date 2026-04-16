package com.secondhand.marketplace.backend.modules.forum.controller;

import com.secondhand.marketplace.backend.common.api.Result;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.config.TestConfig;
import com.secondhand.marketplace.backend.modules.forum.dto.CommentCreateDTO;
import com.secondhand.marketplace.backend.modules.forum.dto.CommentUpdateDTO;
import com.secondhand.marketplace.backend.modules.forum.service.CommentService;
import com.secondhand.marketplace.backend.modules.forum.vo.CommentVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
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
@RequestMapping("/api/forum/comment")
@RequiredArgsConstructor
@Tag(name = "论坛评论管理")
public class ForumCommentController {

    private final CommentService commentService;
    private final TestConfig testConfig;

    private Long getCurrentUserId() {
        // 从UserContext获取当前登录用户ID
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            return userId;
        }
        
        // 测试模式：使用默认用户ID
        if (testConfig.isEnabled()) {
            return testConfig.getDefaultUserId();
        }
        
        return null;
    }

    @PostMapping("/create")
    @Operation(summary = "发表评论")
    public Result<Long> createComment(
            @Valid @RequestBody CommentCreateDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Long commentId = commentService.createComment(userId, dto);
        return Result.success(commentId);
    }

    @PutMapping("/update")
    @Operation(summary = "编辑评论")
    public Result<Void> updateComment(
            @Valid @RequestBody CommentUpdateDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        commentService.updateComment(userId, dto);
        return Result.success();
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(
            @PathVariable @NotNull Long commentId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        commentService.deleteComment(userId, commentId);
        return Result.success();
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "获取评论详情")
    public Result<CommentVO> getCommentDetail(
            @PathVariable @NotNull Long commentId) {
        Long userId = getCurrentUserId();
        CommentVO vo = commentService.getCommentDetail(userId, commentId);
        return Result.success(vo);
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "获取帖子的评论列表")
    public Result<PageResult<CommentVO>> listComments(
            @PathVariable @NotNull Long postId,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        Long userId = getCurrentUserId();
        PageResult<CommentVO> result = commentService.listComments(userId, postId, pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping("/{commentId}/like")
    @Operation(summary = "点赞/取消点赞评论")
    public Result<Integer> likeComment(
            @PathVariable @NotNull Long commentId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        Integer likeCount = commentService.likeComment(userId, commentId);
        return Result.success(likeCount);
    }

    // ==================== 管理员接口 ====================

    @PutMapping("/admin/audit/{commentId}")
    @Operation(summary = "审核评论（管理员）")
    public Result<Void> auditComment(
            @PathVariable @NotNull Long commentId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String rejectReason) {
        Long adminId = getCurrentUserId();
        if (adminId == null) {
            return Result.error(401, "请先登录");
        }
        commentService.auditComment(adminId, commentId, approved, rejectReason);
        return Result.success();
    }
}