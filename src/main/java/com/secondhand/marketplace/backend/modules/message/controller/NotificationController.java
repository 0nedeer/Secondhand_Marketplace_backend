package com.secondhand.marketplace.backend.modules.message.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.message.dto.UserNoticeQueryDTO;
import com.secondhand.marketplace.backend.modules.message.service.NotificationService;
import com.secondhand.marketplace.backend.modules.message.vo.UnreadCountVO;
import com.secondhand.marketplace.backend.modules.message.vo.UserNoticeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户通知中心接口
 * 所有接口都需要登录
 */
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取当前用户的通知列表
     * GET /api/notice/list?readStatus=unread&pageNum=1&pageSize=10
     */
    @GetMapping("/list")
    public CommonResult<List<UserNoticeVO>> getUserNoticeList(@Valid UserNoticeQueryDTO queryDTO) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        List<UserNoticeVO> notices = notificationService.getUserNoticeList(userId, queryDTO);
        return CommonResult.success(notices);
    }

    /**
     * 标记某条通知为已读
     * PUT /api/notice/{inboxId}/read
     */
    @PutMapping("/{inboxId}/read")
    public CommonResult<Void> markNoticeRead(@PathVariable Long inboxId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        notificationService.markNoticeRead(userId, inboxId);
        return CommonResult.success();
    }

    /**
     * 标记所有通知为已读
     * PUT /api/notice/read-all
     */
    @PutMapping("/read-all")
    public CommonResult<Void> markAllNoticeRead() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        notificationService.markAllNoticeRead(userId);
        return CommonResult.success();
    }

    /**
     * 获取未读通知数量
     * GET /api/notice/unread-count
     */
    @GetMapping("/unread-count")
    public CommonResult<UnreadCountVO> getUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return CommonResult.error("请先登录");
        }
        UnreadCountVO unreadCount = notificationService.getUnreadCount(userId);
        return CommonResult.success(unreadCount);
    }
}