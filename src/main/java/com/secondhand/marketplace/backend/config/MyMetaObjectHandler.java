package com.secondhand.marketplace.backend.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());

        //消息模块表
        this.strictInsertFill(metaObject, "sentAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "deliveredAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "publishedAt", LocalDateTime.class, null);
        this.strictInsertFill(metaObject, "lastReadAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}