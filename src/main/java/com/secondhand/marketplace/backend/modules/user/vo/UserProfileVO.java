package com.secondhand.marketplace.backend.modules.user.vo;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class UserProfileVO {
    private String nickname;
    private String avatarUrl;
    private String gender;
    private LocalDate birthday;
    private String bio;
    private String city;
    private String district;
    private Integer creditScore;
    private BigDecimal positiveRate;
    private Integer totalReviewCount;
}