package com.secondhand.marketplace.backend.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class UserProfile {

    @TableId
    private Long userId;

    private String avatarUrl;
    private String gender;
    private LocalDate birthday;
    private String bio;
    private String city;
    private String district;

    private Integer creditScore;
    private BigDecimal positiveRate;
    private Integer totalReviewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}