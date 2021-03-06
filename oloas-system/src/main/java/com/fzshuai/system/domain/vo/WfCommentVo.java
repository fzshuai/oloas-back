package com.fzshuai.system.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @author fzshuai
 * @createTime 2022/4/4 02:03
 */
@Data
@ApiModel("流程批复视图对象")
public class WfCommentVo {

    /**
     * 审批类别
     */
    private String type;

    /**
     * 批复内容
     */
    private String message;

    /**
     * 批复时间
     */
    private Date time;


}
