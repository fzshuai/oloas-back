package com.fzshuai.oss.properties;

import lombok.Data;

/**
 * OSS对象存储 配置属性
 *
 * @author fzshuai
 */
@Data
public class OssProperties {

    /**
     * 域名
     */
    private String endpoint;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * ACCESS_KEY
     */
    private String accessKey;

    /**
     * SECRET_KEY
     */
    private String secretKey;

    /**
     * 存储空间名
     */
    private String bucketName;

    /**
     * 存储区域
     */
    private String region;

    /**
     * 是否https（Y=是,N=否）
     */
    private String isHttps;

}
