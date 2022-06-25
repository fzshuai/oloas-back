package com.fzshuai.common.core.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 通用 系统访问日志
 *
 * @author fzshuai
 */
public interface LogininforService {

    void recordLogininfor(String username, String status, String message,
                          HttpServletRequest request, Object... args);
}
