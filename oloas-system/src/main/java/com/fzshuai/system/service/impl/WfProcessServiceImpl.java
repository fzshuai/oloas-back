package com.fzshuai.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzshuai.common.core.domain.PageQuery;
import com.fzshuai.common.core.domain.entity.SysUser;
import com.fzshuai.common.core.page.TableDataInfo;
import com.fzshuai.common.exception.ServiceException;
import com.fzshuai.common.helper.LoginHelper;
import com.fzshuai.common.utils.DateUtils;
import com.fzshuai.common.utils.StringUtils;
import com.fzshuai.flowable.common.constant.TaskConstants;
import com.fzshuai.flowable.factory.FlowServiceFactory;
import com.fzshuai.flowable.utils.TaskUtil;
import com.fzshuai.system.service.ISysUserService;
import com.fzshuai.system.domain.vo.WfDefinitionVo;
import com.fzshuai.system.domain.vo.WfTaskVo;
import com.fzshuai.system.service.IWfProcessService;
import com.fzshuai.system.service.IWfTaskService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author fzshuai
 * @createTime 2022/3/24 18:57
 */
@RequiredArgsConstructor
@Service
public class WfProcessServiceImpl extends FlowServiceFactory implements IWfProcessService {

    private final IWfTaskService wfTaskService;
    private final ISysUserService userService;

    /**
     * ??????????????????
     *
     * @param pageQuery ????????????
     * @return ??????????????????????????????
     */
    @Override
    public TableDataInfo<WfDefinitionVo> processList(PageQuery pageQuery) {
        Page<WfDefinitionVo> page = new Page<>();
        // ??????????????????????????????
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
            .latestVersion()
            .active()
            .orderByProcessDefinitionKey()
            .asc();
        long pageTotal = processDefinitionQuery.count();
        if (pageTotal <= 0) {
            return TableDataInfo.build();
        }
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<ProcessDefinition> definitionList = processDefinitionQuery.listPage(offset, pageQuery.getPageSize());

        List<WfDefinitionVo> definitionVoList = new ArrayList<>();
        for (ProcessDefinition processDefinition : definitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            WfDefinitionVo vo = new WfDefinitionVo();
            vo.setDefinitionId(processDefinition.getId());
            vo.setProcessKey(processDefinition.getKey());
            vo.setProcessName(processDefinition.getName());
            vo.setVersion(processDefinition.getVersion());
            vo.setCategory(processDefinition.getCategory());
            vo.setDeploymentId(processDefinition.getDeploymentId());
            vo.setSuspended(processDefinition.isSuspended());
            // ??????????????????
            vo.setCategory(deployment.getCategory());
            vo.setDeploymentTime(deployment.getDeploymentTime());
            definitionVoList.add(vo);
        }
        page.setRecords(definitionVoList);
        page.setTotal(pageTotal);
        return TableDataInfo.build(page);
    }

    /**
     * ??????????????????ID??????????????????
     *
     * @param procDefId ????????????Id
     * @param variables ????????????
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startProcess(String procDefId, Map<String, Object> variables) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(procDefId).singleResult();
            if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
                throw new ServiceException("???????????????????????????????????????");
            }
            // ?????????????????????Id????????????
            this.buildProcessVariables(variables);
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables);
            // ????????????????????????????????????????????????????????? todo:?????????????????????????????????????????????????????????
            wfTaskService.startFirstTask(processInstance, variables);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("??????????????????");
        }
    }

    /**
     * ??????DefinitionKey????????????
     * @param procDefKey ????????????Key
     * @param variables ????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startProcessByDefKey(String procDefKey, Map<String, Object> variables) {
        try {
            if (StringUtils.isNoneBlank(procDefKey)) {
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(procDefKey).latestVersion().singleResult();
                if (processDefinition != null && processDefinition.isSuspended()) {
                    throw new ServiceException("???????????????????????????????????????");
                }
                this.buildProcessVariables(variables);
                ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(procDefKey, variables);
                wfTaskService.startFirstTask(processInstance, variables);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("??????????????????");
        }
    }

    @Override
    public TableDataInfo<WfTaskVo> queryPageOwnProcessList(PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        Long userId = LoginHelper.getUserId();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
            .startedBy(userId.toString())
            .orderByProcessInstanceStartTime()
            .desc();
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery
            .listPage(offset, pageQuery.getPageSize());
        page.setTotal(historicProcessInstanceQuery.count());
        List<WfTaskVo> taskVoList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            WfTaskVo taskVo = new WfTaskVo();
            taskVo.setCreateTime(hisIns.getStartTime());
            taskVo.setFinishTime(hisIns.getEndTime());
            taskVo.setProcInsId(hisIns.getId());

            // ????????????
            if (Objects.nonNull(hisIns.getEndTime())) {
                taskVo.setDuration(DateUtils.getDatePoor(hisIns.getEndTime(), hisIns.getStartTime()));
            } else {
                taskVo.setDuration(DateUtils.getDatePoor(DateUtils.getNowDate(), hisIns.getStartTime()));
            }
            // ????????????????????????
            Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(hisIns.getDeploymentId()).singleResult();
            taskVo.setDeployId(hisIns.getDeploymentId());
            taskVo.setProcDefId(hisIns.getProcessDefinitionId());
            taskVo.setProcDefName(hisIns.getProcessDefinitionName());
            taskVo.setProcDefVersion(hisIns.getProcessDefinitionVersion());
            taskVo.setCategory(deployment.getCategory());
            // ??????????????????
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollUtil.isNotEmpty(taskList)) {
                taskVo.setTaskId(taskList.get(0).getId());
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                taskVo.setTaskId(historicTaskInstance.get(0).getId());
            }
            taskVoList.add(taskVo);
        }
        page.setRecords(taskVoList);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<WfTaskVo> queryPageTodoProcessList(PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        Long userId = LoginHelper.getUserId();
        TaskQuery taskQuery = taskService.createTaskQuery()
            .active()
            .includeProcessVariables()
            .taskCandidateOrAssigned(userId.toString())
            .taskCandidateGroupIn(TaskUtil.getCandidateGroup())
            .orderByTaskCreateTime().desc();
        page.setTotal(taskQuery.count());
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<Task> taskList = taskQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> flowList = new ArrayList<>();
        for (Task task : taskList) {
            WfTaskVo flowTask = new WfTaskVo();
            // ??????????????????
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
            flowTask.setCreateTime(task.getCreateTime());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // ??????????????????
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(task.getProcessDefinitionId())
                .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            // ?????????????????????
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
            SysUser startUser = userService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserId(startUser.getNickName());
            flowTask.setStartUserName(startUser.getNickName());
            flowTask.setStartDeptName(startUser.getDept().getDeptName());

            // ????????????
            flowTask.setProcVars(this.getProcessVariables(task.getId()));

            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<WfTaskVo> queryPageFinishedProcessList(PageQuery pageQuery) {
        Page<WfTaskVo> page = new Page<>();
        Long userId = LoginHelper.getUserId();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
            .includeProcessVariables()
            .finished()
            .taskAssignee(userId.toString())
            .orderByHistoricTaskInstanceEndTime()
            .desc();
        int offset = pageQuery.getPageSize() * (pageQuery.getPageNum() - 1);
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage(offset, pageQuery.getPageSize());
        List<WfTaskVo> hisTaskList = Lists.newArrayList();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            WfTaskVo flowTask = new WfTaskVo();
            // ??????????????????
            flowTask.setTaskId(histTask.getId());
            // ??????????????????
            flowTask.setCreateTime(histTask.getCreateTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(DateUtil.formatBetween(histTask.getDurationInMillis(), BetweenFormatter.Level.SECOND));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());

            // ??????????????????
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(histTask.getProcessDefinitionId())
                .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());

            // ?????????????????????
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(histTask.getProcessInstanceId())
                .singleResult();
            SysUser startUser = userService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserId(startUser.getNickName());
            flowTask.setStartUserName(startUser.getNickName());
            flowTask.setStartDeptName(startUser.getDept().getDeptName());

            // ????????????
            flowTask.setProcVars(this.getProcessVariables(histTask.getId()));

            hisTaskList.add(flowTask);
        }
        page.setTotal(taskInstanceQuery.count());
        page.setRecords(hisTaskList);
//        Map<String, Object> result = new HashMap<>();
//        result.put("result",page);
//        result.put("finished",true);
        return TableDataInfo.build(page);
    }

    /**
     * ??????????????????
     * @param variables ????????????
     */
    private void buildProcessVariables(Map<String, Object> variables) {
        String userIdStr = LoginHelper.getUserId().toString();
        identityService.setAuthenticatedUserId(userIdStr);
        variables.put(TaskConstants.PROCESS_INITIATOR, userIdStr);
    }


    /**
     * ??????????????????
     *
     * @param taskId ??????ID
     * @return ????????????
     */
    private Map<String, Object> getProcessVariables(String taskId) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
            .includeProcessVariables()
            .finished()
            .taskId(taskId)
            .singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return historicTaskInstance.getProcessVariables();
        }
        return taskService.getVariables(taskId);
    }
}
