package com.secondhand.marketplace.backend.modules.admin.service;

import com.secondhand.marketplace.backend.modules.forum.vo.AdminLogVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;

public interface AdminLogService {
    PageResult<AdminLogVO> listLogs(Long currentUserId,
                                    Integer pageNum,
                                    Integer pageSize,
                                    Long adminId,
                                    String targetType,
                                    String action,
                                    String startTime,
                                    String endTime);

    AdminLogVO getLogDetail(Long currentUserId, Long logId);
}
