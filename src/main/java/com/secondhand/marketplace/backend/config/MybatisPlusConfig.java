package com.secondhand.marketplace.backend.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan({
        "com.secondhand.marketplace.backend.modules.user.mapper",
        "com.secondhand.marketplace.backend.modules.goods.mapper",
        "com.secondhand.marketplace.backend.modules.trade.mapper",
        "com.secondhand.marketplace.backend.modules.forum.mapper"
})
public class MybatisPlusConfig {
}