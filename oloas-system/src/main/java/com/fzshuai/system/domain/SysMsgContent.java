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
import java.time.LocalDateTime;

/**
 * @author fzshuai
 * @date 2022/05/28 22:45
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_msg_content")
@ApiModel(value = "SysMsgContent对象", description = "")
public class SysMsgContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sys_msg_content_id", type = IdType.AUTO)
    private Integer msgContentId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String message;

    @ApiModelProperty("创建时间")
    private LocalDateTime createDate;
}
