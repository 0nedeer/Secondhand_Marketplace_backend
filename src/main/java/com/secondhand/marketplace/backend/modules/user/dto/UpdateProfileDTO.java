package com.secondhand.marketplace.backend.modules.user.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class UpdateProfileDTO {

    private String nickname;
    private String avatarUrl;
    private String gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String bio;
    private String city;
    private String district;
}