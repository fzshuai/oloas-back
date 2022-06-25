package com.fzshuai.system.service;

import com.fzshuai.common.core.domain.PageQuery;
import com.fzshuai.common.core.page.TableDataInfo;
import com.fzshuai.system.domain.bo.WfFormBo;
import com.fzshuai.system.domain.vo.WfDefinitionVo;
import com.fzshuai.system.domain.vo.WfFormVo;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author fzshuai
 * @createTime 2022/3/10 00:12
 */
public interface IWfDefinitionService {

    boolean exist(String processDefinitionKey);


    /**
     * 流程定义列表
     *
     * @param pageQuery 分页参数
     * @return 流程定义分页列表数据
     */
    TableDataInfo<WfDefinitionVo> list(PageQuery pageQuery);

    /**
     *
     * @param processKey
     * @return
     */
    TableDataInfo<WfDefinitionVo> publishList(String processKey, PageQuery pageQuery);

    /**
     * 导入流程文件
     *
     * @param name
     * @param category
     * @param in
     */
    void importFile(String name, String category, InputStream in);

    /**
     * 读取xml
     * @param definitionId 流程定义ID
     * @return
     */
    String readXml(String definitionId) throws IOException;


    /**
     * 激活或挂起流程定义
     *
     * @param suspended    状态
     * @param definitionId 流程定义ID
     */
    void updateState(Boolean suspended, String definitionId);

    /**
     * 删除流程定义
     *
     * @param deployId 流程部署ID act_ge_bytearray 表中 deployment_id值
     */
    void delete(String deployId);

    /**
     * 读取图片文件
     * @param definitionId 流程定义ID
     * @return
     */
    InputStream readImage(String definitionId);
}
