package com.fzshuai.common.core.service;

import com.fzshuai.common.core.domain.dto.OperLogDTO;
import org.springframework.scheduling.annotation.Async;

/**
 * 通用 操作日志
 *
 * @author fzshuai
 */
public interface OperLogService {

    @Async
    void recordOper(OperLogDTO operLogDTO);
}
