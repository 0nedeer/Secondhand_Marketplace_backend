package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.User;
import com.secondhand.marketplace.backend.modules.forum.vo.UserInfoVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-15T22:21:10+0800",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserConverterImpl implements UserConverter {

    @Override
    public UserInfoVO toVo(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserInfoVO userInfoVO = new UserInfoVO();

        userInfoVO.setAvatar( entity.getAvatar() );
        userInfoVO.setBio( entity.getBio() );
        userInfoVO.setCreatedAt( entity.getCreatedAt() );
        userInfoVO.setCreditScore( entity.getCreditScore() );
        userInfoVO.setId( entity.getId() );
        userInfoVO.setRole( entity.getRole() );
        userInfoVO.setStatus( entity.getStatus() );
        userInfoVO.setUsername( entity.getUsername() );

        return userInfoVO;
    }

    @Override
    public List<UserInfoVO> toVoList(List<User> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UserInfoVO> list = new ArrayList<UserInfoVO>( entities.size() );
        for ( User user : entities ) {
            list.add( toVo( user ) );
        }

        return list;
    }
}
