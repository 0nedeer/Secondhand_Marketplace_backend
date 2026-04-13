package com.secondhand.marketplace.backend.modules.aftersale.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.aftersale.entity.ReviewImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewImageMapper extends BaseMapper<ReviewImage> {

    @Select("SELECT * FROM review_image WHERE review_id = #{reviewId} ORDER BY sort_no ASC, id ASC")
    List<ReviewImage> selectByReviewId(Long reviewId);
}
