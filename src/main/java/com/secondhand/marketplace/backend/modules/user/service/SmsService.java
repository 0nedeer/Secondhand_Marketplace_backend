package com.secondhand.marketplace.backend.modules.user.service;

public interface SmsService {

    /**
     * 发送验证码
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendVerifyCode(String phone);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String phone, String code);
}
