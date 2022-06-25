package com.fzshuai.oss.exception;

/**
 * OSS异常类
 *
 * @author fzshuai
 */
public class OssException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OssException(String msg) {
        super(msg);
    }

}
