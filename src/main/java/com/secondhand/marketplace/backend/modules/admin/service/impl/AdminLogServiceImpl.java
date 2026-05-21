package com.secondhand.marketplace.backend.modules.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.admin.service.AdminAuthService;
import com.secondhand.marketplace.backend.modules.admin.service.AdminLogService;
import com.secondhand.marketplace.backend.modules.forum.entity.AdminLog;
import com.secondhand.marketplace.backend.modules.forum.mapper.AdminLogMapper;
import com.secondhand.marketplace.backend.modules.forum.vo.AdminLogVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import com.secondhand.marketplace.backend.modules.user.entity.UserAccount;
import com.secondhand.marketplace.backend.modules.user.mapper.UserAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminLogServiceImpl implements AdminLogService {

    private final AdminAuthService adminAuthService;
    private final AdminLogMapper adminLogMapper;
    private final UserAccountMapper userAccountMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResult<AdminLogVO> listLogs(Long currentUserId,
                                           Integer pageNum,
                                           Integer pageSize,
                                           Long adminId,
                                           String targetType,
                                           String action,
                                           String startTime,
                                           String endTime) {
        adminAuthService.requireAdmin(currentUserId);
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        int offset = (safePageNum - 1) * safePageSize;
        LocalDateTime start = parseDateTime(startTime);
        LocalDateTime end = parseDateTime(endTime);

        List<AdminLogVO> list = adminLogMapper
                .selectPage(adminId, normalize(targetType), normalize(action), start, end, offset, safePageSize)
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        long total = adminLogMapper.countByCondition(adminId, normalize(targetType), normalize(action), start, end);
        return new PageResult<>(total, safePageNum, safePageSize, list);
    }

    @Override
    public AdminLogVO getLogDetail(Long currentUserId, Long logId) {
        adminAuthService.requireAdmin(currentUserId);
        AdminLog log = adminLogMapper.selectById(logId);
        if (log == null) {
            throw new BusinessException(404, "操作日志不存在");
        }
        return toVO(log);
    }

    private AdminLogVO toVO(AdminLog log) {
        AdminLogVO vo = new AdminLogVO();
        vo.setId(log.getId());
        vo.setAdminId(log.getAdminId());
        vo.setTargetType(log.getTargetType());
        vo.setTargetId(log.getTargetId());
        vo.setAction(log.getAction());
        vo.setReason(log.getReason());
        vo.setIpAddress(log.getIpAddress());
        vo.setCreatedAt(log.getCreatedAt());
        vo.setBeforeData(parseJson(log.getBeforeData()));
        vo.setAfterData(parseJson(log.getAfterData()));

        UserAccount admin = userAccountMapper.selectById(log.getAdminId());
        vo.setAdminName(admin == null ? null : admin.getNickname());
        return vo;
    }

    private Object parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception ignored) {
            return json;
        }
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        try {
            if (normalized.length() == 10) {
                return LocalDateTime.parse(normalized + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (normalized.contains("T")) {
                return LocalDateTime.parse(normalized);
            }
            return LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new BusinessException(400, "时间格式错误，请使用 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
