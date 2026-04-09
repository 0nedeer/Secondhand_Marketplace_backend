package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.User;
import com.secondhand.marketplace.backend.modules.forum.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConverter {
    
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
    
    /**
     * Entity转UserInfoVO（排除敏感字段）
     */
    UserInfoVO toVo(User entity);
    
    /**
     * Entity列表转UserInfoVO列表
     */
    List<UserInfoVO> toVoList(List<User> entities);
}