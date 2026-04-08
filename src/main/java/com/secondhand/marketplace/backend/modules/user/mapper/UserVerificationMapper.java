package com.secondhand.marketplace.backend.modules.user.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondhand.marketplace.backend.modules.user.entity.UserVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserVerificationMapper extends BaseMapper<UserVerification> {

    @Select("SELECT * FROM user_verification WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserVerification> findByUserId(Long userId);

    @Select("SELECT * FROM user_verification WHERE user_id = #{userId} AND verify_type = #{verifyType} AND verify_status = 'approved'")
    UserVerification findApprovedByUserIdAndType(@Param("userId") Long userId, @Param("verifyType") String verifyType);

    //根据ID查询认证记录
    @Select("SELECT * FROM user_verification WHERE id = #{id}")
    UserVerification selectById(@Param("id") Long id);

    //查询某个用户某个类型的认证记录（包括非approved）
    @Select("SELECT * FROM user_verification WHERE user_id = #{userId} AND verify_type = #{verifyType} ORDER BY created_at DESC LIMIT 1")
    UserVerification findLatestByUserIdAndType(@Param("userId") Long userId, @Param("verifyType") String verifyType);

}