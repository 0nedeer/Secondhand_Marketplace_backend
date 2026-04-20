package com.secondhand.marketplace.backend.modules.forum.convert;

import com.secondhand.marketplace.backend.modules.forum.entity.User;
import com.secondhand.marketplace.backend.modules.forum.vo.UserInfoVO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-20T20:02:29+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class UserConverterImpl implements UserConverter {

    @Override
    public UserInfoVO toVo(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserInfoVO userInfoVO = new UserInfoVO();

        userInfoVO.setId( entity.getId() );
        userInfoVO.setUsername( entity.getUsername() );
        userInfoVO.setAvatar( entity.getAvatar() );
        userInfoVO.setBio( entity.getBio() );
        userInfoVO.setCreditScore( entity.getCreditScore() );
        userInfoVO.setRole( entity.getRole() );
        userInfoVO.setStatus( entity.getStatus() );
        userInfoVO.setCreatedAt( entity.getCreatedAt() );

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
