package com.fzshuai.system.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author fzshuai
 * @date 2022/05/28 23:03
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysChatMsg {

    private String from;
    private String to;
    private String content;
    private LocalDateTime date;
    private String formNickName;
}
