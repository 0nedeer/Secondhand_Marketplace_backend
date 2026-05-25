package com.secondhand.marketplace.backend.modules.message.dto;

import lombok.Data;

@Data
public class NoticeQueryDTO {

    private String noticeType;

    private String publishStatus;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}