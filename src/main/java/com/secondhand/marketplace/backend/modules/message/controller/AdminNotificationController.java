package com.secondhand.marketplace.backend.modules.message.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.message.dto.NoticeQueryDTO;
import com.secondhand.marketplace.backend.modules.message.dto.PublishNoticeDTO;
import com.secondhand.marketplace.backend.modules.message.service.NotificationService;
import com.secondhand.marketplace.backend.modules.message.vo.SystemNoticeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员通知管理接口
 * 所有接口都需要登录，且需要管理员权限
 */
@RestController
@RequestMapping("/api/admin/notification")
@RequiredArgsConstructor
@Validated
public class AdminNotificationController {

    private final NotificationService notificationService;

    /**
     * 管理员发布通知
     * POST /api/admin/notification/publish
     */
    @PostMapping("/publish")
    public CommonResult<Void> publishNotice(@Valid @RequestBody PublishNoticeDTO publishNoticeDTO) {
        Long adminId = UserContext.getCurrentUserId();
        if (adminId == null) {
            return CommonResult.error("请先登录");
        }
        notificationService.publishNotice(adminId, publishNoticeDTO);
        return CommonResult.success();
    }

    /**
     * 管理员获取通知列表（支持分页和筛选）
     * GET /api/admin/notification/list?noticeType=system&publishStatus=published&pageNum=1&pageSize=10
     */
    @GetMapping("/list")
    public CommonResult<List<SystemNoticeVO>> getNoticeList(@Valid NoticeQueryDTO queryDTO) {
        Long adminId = UserContext.getCurrentUserId();
        if (adminId == null) {
            return CommonResult.error("请先登录");
        }
        // 注意：这里只做了登录校验，实际应该增加管理员权限校验
        List<SystemNoticeVO> notices = notificationService.getNoticeList(queryDTO);
        return CommonResult.success(notices);
    }

    /**
     * 管理员撤回已发布的通知
     * POST /api/admin/notification/revoke/{noticeId}
     */
    @PostMapping("/revoke/{noticeId}")
    public CommonResult<Void> revokeNotice(@PathVariable Long noticeId) {
        Long adminId = UserContext.getCurrentUserId();
        if (adminId == null) {
            return CommonResult.error("请先登录");
        }
        notificationService.revokeNotice(adminId, noticeId);
        return CommonResult.success();
    }

    /**
     * 管理员获取某条通知的详细信息
     * GET /api/admin/notification/{noticeId}
     */
    @GetMapping("/{noticeId}")
    public CommonResult<SystemNoticeVO> getNoticeDetail(@PathVariable Long noticeId) {
        Long adminId = UserContext.getCurrentUserId();
        if (adminId == null) {
            return CommonResult.error("请先登录");
        }
        SystemNoticeVO notice = notificationService.getNoticeDetail(noticeId);
        return CommonResult.success(notice);
    }
}