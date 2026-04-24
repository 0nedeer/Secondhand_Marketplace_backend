package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminUserPageVO {
    private Long total;
    private Long page;
    private Long pageSize;
    private List<AdminUserItemVO> list;
}
