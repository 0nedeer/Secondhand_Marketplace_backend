package com.secondhand.marketplace.backend.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan("com.secondhand.marketplace.backend.modules.**.mapper")
public class MyBatisConfig {
}