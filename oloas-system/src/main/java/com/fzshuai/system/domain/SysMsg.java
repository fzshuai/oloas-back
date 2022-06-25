package com.fzshuai.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author fzshuai
 * @date 2022/05/28 22:41
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_msg")
public class SysMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息id")
    @TableId(value = "msg_id")
    private Integer msgId;

    @ApiModelProperty("0表示群发消息")
    private Integer type;

    @ApiModelProperty("这条消息是给谁的")
    private Integer userId;

    @ApiModelProperty("0 未读 1 已读")
    private Integer state;


}

