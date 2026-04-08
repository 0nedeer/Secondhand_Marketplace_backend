package com.secondhand.marketplace.backend.modules.user.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.modules.user.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final long CODE_EXPIRE_TIME = 300; // 5分钟

    @Override
    public boolean sendVerifyCode(String phone) {
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(999999));

        // 存储到Redis，有效期5分钟
        String key = SMS_CODE_PREFIX + phone;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_TIME, TimeUnit.SECONDS);

        // TODO: 调用短信服务商API发送短信
        // 示例：阿里云短信、腾讯云短信等
        boolean sendResult = sendSms(phone, code);

        if (!sendResult) {
            throw new BusinessException("验证码发送失败");
        }

        // 开发测试环境可以打印日志方便测试
        System.out.println("验证码：" + code + " 已发送到手机：" + phone);

        return true;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        String key = SMS_CODE_PREFIX + phone;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }

        if (!savedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }

        // 验证成功后删除验证码（防止重复使用）
        redisTemplate.delete(key);

        return true;
    }

    /**
     * 实际调用短信服务商API
     * 这里只是示例，需要根据实际短信服务商实现
     */
    private boolean sendSms(String phone, String code) {
        // 实际项目中替换为真实的短信发送逻辑
        // 例如：
        // 1. 阿里云短信服务
        // 2. 腾讯云短信服务
        // 3. 第三方短信平台

        // 模拟发送成功
        return true;
    }
}
