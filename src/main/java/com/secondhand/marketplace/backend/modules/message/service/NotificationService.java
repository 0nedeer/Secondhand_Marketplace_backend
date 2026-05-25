package com.secondhand.marketplace.backend.modules.message.service;

import com.secondhand.marketplace.backend.modules.message.dto.*;
import com.secondhand.marketplace.backend.modules.message.vo.*;

import java.util.List;

public interface NotificationService {

    // ========== 管理员通知管理 ==========

    // 管理员发布通知
    void publishNotice(Long adminId, PublishNoticeDTO publishNoticeDTO);

    // 管理员获取通知列表
    List<SystemNoticeVO> getNoticeList(NoticeQueryDTO queryDTO);

    // 管理员撤回已发布的通知
    void revokeNotice(Long adminId, Long noticeId);

    // 管理员获取某条通知详情
    SystemNoticeVO getNoticeDetail(Long noticeId);

    // ========== 用户通知中心 ==========

    // 获取当前用户的通知列表
    List<UserNoticeVO> getUserNoticeList(Long userId, UserNoticeQueryDTO queryDTO);

    // 标记某条个人通知为已读
    void markNoticeRead(Long userId, Long inboxId);

    // 标记所有个人通知为已读
    void markAllNoticeRead(Long userId);

    // 获取当前用户未读通知数量
    UnreadCountVO getUnreadCount(Long userId);
}