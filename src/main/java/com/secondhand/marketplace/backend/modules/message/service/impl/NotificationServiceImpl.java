package com.secondhand.marketplace.backend.modules.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.message.dto.NoticeQueryDTO;
import com.secondhand.marketplace.backend.modules.message.dto.PublishNoticeDTO;
import com.secondhand.marketplace.backend.modules.message.dto.UserNoticeQueryDTO;
import com.secondhand.marketplace.backend.modules.message.entity.SystemNotice;
import com.secondhand.marketplace.backend.modules.message.entity.UserNoticeInbox;
import com.secondhand.marketplace.backend.modules.message.mapper.SystemNoticeMapper;
import com.secondhand.marketplace.backend.modules.message.mapper.UserNoticeInboxMapper;
import com.secondhand.marketplace.backend.modules.message.service.NotificationService;
import com.secondhand.marketplace.backend.modules.message.vo.SystemNoticeVO;
import com.secondhand.marketplace.backend.modules.message.vo.UnreadCountVO;
import com.secondhand.marketplace.backend.modules.message.vo.UserNoticeVO;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SystemNoticeMapper systemNoticeMapper;
    private final UserNoticeInboxMapper userNoticeInboxMapper;
    private final UserAccountMapper userAccountMapper;

    // ==================== 管理员通知管理 ====================

    @Override
    @Transactional
    public void publishNotice(Long adminId, PublishNoticeDTO publishNoticeDTO) {
        // 1. 验证管理员权限
        UserAccount admin = userAccountMapper.selectById(adminId);
        if (admin == null || admin.getIsAdmin() != 1) {
            throw new BusinessException("无权限操作，仅管理员可发布通知");
        }

        // 2. 参数校验
        if ("role".equals(publishNoticeDTO.getTargetScope()) && publishNoticeDTO.getTargetRoleCode() == null) {
            throw new BusinessException("指定角色范围时，目标角色编码不能为空");
        }
        if ("user".equals(publishNoticeDTO.getTargetScope()) && publishNoticeDTO.getTargetUserId() == null) {
            throw new BusinessException("指定用户范围时，目标用户ID不能为空");
        }

        // 3. 创建通知
        SystemNotice notice = new SystemNotice();
        notice.setNoticeType(publishNoticeDTO.getNoticeType());
        notice.setTitle(publishNoticeDTO.getTitle());
        notice.setContent(publishNoticeDTO.getContent());
        notice.setTargetScope(publishNoticeDTO.getTargetScope());
        notice.setTargetRoleCode(publishNoticeDTO.getTargetRoleCode());
        notice.setTargetUserId(publishNoticeDTO.getTargetUserId());
        notice.setPublishStatus("published");
        notice.setPublishedAt(LocalDateTime.now());
        notice.setCreatedBy(adminId);

        systemNoticeMapper.insert(notice);

        // 4. 投递通知到用户收件箱
        deliverNotice(notice);

        log.info("管理员 {} 发布了通知: {}", adminId, notice.getTitle());
    }

    @Override
    public List<SystemNoticeVO> getNoticeList(NoticeQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null) queryDTO.setPageNum(1);
        if (queryDTO.getPageSize() == null) queryDTO.setPageSize(10);
        if (queryDTO.getPageSize() > 100) queryDTO.setPageSize(100);

        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();

        List<SystemNotice> notices = systemNoticeMapper.findByConditions(
                queryDTO.getNoticeType(),
                queryDTO.getPublishStatus(),
                offset,
                queryDTO.getPageSize()
        );

        return notices.stream()
                .map(this::buildSystemNoticeVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeNotice(Long adminId, Long noticeId) {
        // 验证管理员权限
        UserAccount admin = userAccountMapper.selectById(adminId);
        if (admin == null || admin.getIsAdmin() != 1) {
            throw new BusinessException("无权限操作，仅管理员可撤回通知");
        }

        // 检查通知是否存在
        SystemNotice notice = systemNoticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new BusinessException("通知不存在");
        }

        // 只有已发布的通知才能撤回
        if (!"published".equals(notice.getPublishStatus())) {
            throw new BusinessException("只有已发布的通知才能撤回");
        }

        // 撤回通知
        int affected = systemNoticeMapper.revokeNotice(noticeId);
        if (affected == 0) {
            throw new BusinessException("撤回失败");
        }

        log.info("管理员 {} 撤回了通知: {}", adminId, notice.getTitle());
    }

    @Override
    public SystemNoticeVO getNoticeDetail(Long noticeId) {
        SystemNotice notice = systemNoticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new BusinessException("通知不存在");
        }
        return buildSystemNoticeVO(notice);
    }

    // ==================== 用户通知中心 ====================

    @Override
    public List<UserNoticeVO> getUserNoticeList(Long userId, UserNoticeQueryDTO queryDTO) {
        if (queryDTO.getPageNum() == null) queryDTO.setPageNum(1);
        if (queryDTO.getPageSize() == null) queryDTO.setPageSize(10);
        if (queryDTO.getPageSize() > 100) queryDTO.setPageSize(100);

        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();

        List<UserNoticeInbox> inboxList = userNoticeInboxMapper.findByUserId(
                userId,
                queryDTO.getReadStatus(),
                offset,
                queryDTO.getPageSize()
        );

        return inboxList.stream()
                .map(this::buildUserNoticeVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNoticeRead(Long userId, Long inboxId) {
        int affected = userNoticeInboxMapper.markAsRead(inboxId, userId);
        if (affected == 0) {
            throw new BusinessException("通知记录不存在或无权操作");
        }
    }

    @Override
    @Transactional
    public void markAllNoticeRead(Long userId) {
        int affected = userNoticeInboxMapper.markAllAsRead(userId);
        log.info("用户 {} 标记所有通知为已读，共 {} 条", userId, affected);
    }

    @Override
    public UnreadCountVO getUnreadCount(Long userId) {
        Long unreadCount = userNoticeInboxMapper.countUnreadByUserId(userId);
        return UnreadCountVO.builder()
                .unreadCount(unreadCount)
                .build();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 投递通知给目标用户
     */
    private void deliverNotice(SystemNotice notice) {
        List<Long> targetUserIds = getTargetUserIds(notice);

        if (targetUserIds.isEmpty()) {
            log.warn("通知 {} 没有目标用户", notice.getId());
            return;
        }

        int deliveredCount = 0;
        for (Long userId : targetUserIds) {
            try {
                // 检查是否已投递过
                if (userNoticeInboxMapper.checkExists(notice.getId(), userId) == 0) {
                    UserNoticeInbox inbox = new UserNoticeInbox();
                    inbox.setNoticeId(notice.getId());
                    inbox.setUserId(userId);
                    inbox.setReadStatus("unread");
                    inbox.setDeliveredAt(LocalDateTime.now());
                    userNoticeInboxMapper.insert(inbox);
                    deliveredCount++;
                }
            } catch (Exception e) {
                log.error("投递通知 {} 给用户 {} 失败: {}", notice.getId(), userId, e.getMessage());
            }
        }

        log.info("通知 {} 投递完成，目标用户数: {}, 实际投递: {}", notice.getId(), targetUserIds.size(), deliveredCount);
    }

    /**
     * 根据通知的目标范围获取目标用户ID列表
     */
    private List<Long> getTargetUserIds(SystemNotice notice) {
        List<Long> userIds = new ArrayList<>();

        switch (notice.getTargetScope()) {
            case "all":
                // 获取所有活跃用户
                LambdaQueryWrapper<UserAccount> allWrapper = new LambdaQueryWrapper<>();
                allWrapper.eq(UserAccount::getUserStatus, "active");
                userIds = userAccountMapper.selectList(allWrapper)
                        .stream()
                        .map(UserAccount::getId)
                        .collect(Collectors.toList());
                break;

            case "role":
                // 根据角色获取用户
                // TODO: 根据实际角色表实现，这里暂时返回空
                // 如果需要实现角色功能，需要添加 user_role 表和相关逻辑
                log.warn("角色通知功能暂未实现，请先完善用户角色系统");
                break;

            case "user":
                if (notice.getTargetUserId() != null) {
                    // 验证用户是否存在且未封禁
                    UserAccount user = userAccountMapper.selectById(notice.getTargetUserId());
                    if (user != null && !"banned".equals(user.getUserStatus())) {
                        userIds.add(notice.getTargetUserId());
                    }
                }
                break;

            default:
                log.warn("未知的目标范围: {}", notice.getTargetScope());
        }

        return userIds;
    }

    /**
     * 构建 SystemNoticeVO
     */
    private SystemNoticeVO buildSystemNoticeVO(SystemNotice notice) {
        return SystemNoticeVO.builder()
                .id(notice.getId())
                .noticeType(notice.getNoticeType())
                .title(notice.getTitle())
                .content(notice.getContent())
                .targetScope(notice.getTargetScope())
                .targetRoleCode(notice.getTargetRoleCode())
                .targetUserId(notice.getTargetUserId())
                .publishStatus(notice.getPublishStatus())
                .publishedAt(notice.getPublishedAt())
                .createdBy(notice.getCreatedBy())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    /**
     * 构建 UserNoticeVO
     */
    private UserNoticeVO buildUserNoticeVO(UserNoticeInbox inbox) {
        SystemNotice notice = systemNoticeMapper.selectById(inbox.getNoticeId());

        return UserNoticeVO.builder()
                .inboxId(inbox.getId())
                .noticeId(inbox.getNoticeId())
                .title(notice != null ? notice.getTitle() : "通知已删除")
                .content(notice != null ? notice.getContent() : "该通知内容已被管理员删除")
                .noticeType(notice != null ? notice.getNoticeType() : "system")
                .readStatus(inbox.getReadStatus())
                .deliveredAt(inbox.getDeliveredAt())
                .readAt(inbox.getReadAt())
                .build();
    }
}