package com.fzshuai.common.exception.user;

/**
 * 验证码错误异常类
 *
 * @author fzshuai
 */
public class CaptchaException extends UserException {
    private static final long serialVersionUID = 1L;

    public CaptchaException() {
        super("user.jcaptcha.error");
    }
}
