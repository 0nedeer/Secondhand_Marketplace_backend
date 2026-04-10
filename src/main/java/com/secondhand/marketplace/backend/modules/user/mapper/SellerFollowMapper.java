package com.secondhand.marketplace.backend.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.user.entity.SellerFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SellerFollowMapper extends BaseMapper<SellerFollow> {

    @Select("SELECT COUNT(*) FROM seller_follow WHERE buyer_id = #{buyerId}")
    Integer countFollowsByBuyerId(Long buyerId);

    @Select("SELECT COUNT(*) FROM seller_follow WHERE seller_id = #{sellerId}")
    Integer countFollowersBySellerId(Long sellerId);

    @Select("SELECT seller_id FROM seller_follow WHERE buyer_id = #{buyerId}")
    List<Long> findSellerIdsByBuyerId(Long buyerId);
}