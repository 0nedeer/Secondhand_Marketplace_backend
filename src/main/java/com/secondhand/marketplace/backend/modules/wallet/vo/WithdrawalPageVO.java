package com.secondhand.marketplace.backend.modules.wallet.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WithdrawalPageVO {
    private Long total;
    private Long page;
    private Long pageSize;
    private List<WithdrawalVO> list;
}

