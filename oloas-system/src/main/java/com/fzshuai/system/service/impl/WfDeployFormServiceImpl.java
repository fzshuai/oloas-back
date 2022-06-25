package com.fzshuai.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fzshuai.common.exception.ServiceException;
import com.fzshuai.system.domain.WfDeployForm;
import com.fzshuai.system.domain.WfForm;
import com.fzshuai.system.domain.vo.WfFormVo;
import com.fzshuai.system.mapper.WfDeployFormMapper;
import com.fzshuai.system.mapper.WfFormMapper;
import com.fzshuai.system.service.IWfDeployFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 流程实例关联表单Service业务层处理
 *
 * @author fzshuai
 * @createTime 2022/3/7 22:07
 */
@RequiredArgsConstructor
@Service
public class WfDeployFormServiceImpl implements IWfDeployFormService {

    private final WfDeployFormMapper baseMapper;

    private final WfFormMapper formMapper;

    /**
     * 新增流程实例关联表单
     *
     * @param deployForm 流程实例关联表单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWfDeployForm(WfDeployForm deployForm) {
        // 删除部署流程和表单的关联关系
        baseMapper.delete(new LambdaQueryWrapper<WfDeployForm>().eq(WfDeployForm::getDeployId, deployForm.getDeployId()));
        // 新增部署流程和表单关系
        return baseMapper.insert(deployForm);
    }

    /**
     * 查询流程挂着的表单
     *
     * @param deployId
     * @return
     */
    @Override
    public WfFormVo selectDeployFormByDeployId(String deployId) {
        QueryWrapper<WfForm> wrapper = Wrappers.query();
        wrapper.eq("t2.deploy_id", deployId);
        List<WfFormVo> list = formMapper.selectFormVoList(wrapper);
        if (ObjectUtil.isNotEmpty(list)) {
            if (list.size() != 1) {
                throw new ServiceException("表单信息查询错误");
            } else {
                return list.get(0);
            }
        } else {
            return null;
        }
    }
}
