package com.secondhand.marketplace.backend.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.user.entity.SellerReputationSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SellerReputationSnapshotMapper extends BaseMapper<SellerReputationSnapshot> {

    //查询卖家最新的信誉快照（今天）
    @Select("SELECT * FROM seller_reputation_snapshot WHERE seller_id = #{sellerId} AND snapshot_date = CURDATE()")
    SellerReputationSnapshot findTodaySnapshot(@Param("sellerId") Long sellerId);

    //查询卖家最近N天的信誉历史
    @Select("SELECT * FROM seller_reputation_snapshot WHERE seller_id = #{sellerId} ORDER BY snapshot_date DESC LIMIT #{days}")
    List<SellerReputationSnapshot> findHistorySnapshots(@Param("sellerId") Long sellerId, @Param("days") int days);

    //查询卖家最新的一条快照（如果没有今天的，返回最近的）
    @Select("SELECT * FROM seller_reputation_snapshot WHERE seller_id = #{sellerId} ORDER BY snapshot_date DESC LIMIT 1")
    SellerReputationSnapshot findLatestBySellerId(@Param("sellerId") Long sellerId);
}