package com.secondhand.marketplace.backend.modules.trade.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderPageVO {
    private Long total;
    private Long page;
    private Long pageSize;
    private List<OrderListItemVO> list;
}

