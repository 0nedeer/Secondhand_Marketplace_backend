package com.secondhand.marketplace.backend.modules.admin.controller;

import com.secondhand.marketplace.backend.common.api.CommonResult;
import com.secondhand.marketplace.backend.common.context.UserContext;
import com.secondhand.marketplace.backend.modules.admin.service.AdminLogService;
import com.secondhand.marketplace.backend.modules.forum.vo.AdminLogVO;
import com.secondhand.marketplace.backend.modules.forum.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/log")
public class AdminLogController {

    private final AdminLogService adminLogService;

    @GetMapping("/list")
    public CommonResult<PageResult<AdminLogVO>> listLogs(@RequestParam(defaultValue = "1") Integer pageNum,
                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                         @RequestParam(required = false) Long adminId,
                                                         @RequestParam(required = false) String targetType,
                                                         @RequestParam(required = false) String action,
                                                         @RequestParam(required = false) String startTime,
                                                         @RequestParam(required = false) String endTime) {
        return CommonResult.success(adminLogService.listLogs(
                UserContext.getCurrentUserId(), pageNum, pageSize, adminId, targetType, action, startTime, endTime));
    }

    @GetMapping("/{logId}")
    public CommonResult<AdminLogVO> getLogDetail(@PathVariable Long logId) {
        return CommonResult.success(adminLogService.getLogDetail(UserContext.getCurrentUserId(), logId));
    }
}
