package com.fzshuai.web.controller.workflow;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fzshuai.common.annotation.Log;
import com.fzshuai.common.core.controller.BaseController;
import com.fzshuai.common.core.domain.PageQuery;
import com.fzshuai.common.core.domain.R;
import com.fzshuai.common.core.page.TableDataInfo;
import com.fzshuai.common.core.validate.QueryGroup;
import com.fzshuai.common.enums.BusinessType;
import com.fzshuai.common.utils.poi.ExcelUtil;
import com.fzshuai.system.domain.WfDeployForm;
import com.fzshuai.system.domain.bo.WfFormBo;
import com.fzshuai.system.domain.vo.WfFormVo;
import com.fzshuai.system.service.IWfDeployFormService;
import com.fzshuai.system.service.IWfFormService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 流程表单Controller
 *
 * @author fzshuai
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/workflow/form")
public class WfFormController extends BaseController {

    private final IWfFormService formService;

    private final IWfDeployFormService deployFormService;

    /**
     * 查询流程表单列表
     */
    @SaCheckPermission("workflow:form:list")
    @GetMapping("/list")
    public TableDataInfo<WfFormVo> list(@Validated(QueryGroup.class) WfFormBo bo, PageQuery pageQuery) {
        return formService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出流程表单列表
     */
    @SaCheckPermission("workflow:form:export")
    @Log(title = "流程表单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(@Validated WfFormBo bo, HttpServletResponse response) {
        List<WfFormVo> list = formService.queryList(bo);
        ExcelUtil.exportExcel(list, "流程表单", WfFormVo.class, response);
    }

    /**
     * 获取流程表单详细信息
     */
    @SaCheckPermission("workflow:form:query")
    @GetMapping(value = "/{formId}")
    public R<WfFormVo> getInfo(@ApiParam("主键") @NotNull(message = "主键不能为空") @PathVariable("formId") Long formId) {
        return R.ok(formService.queryById(formId));
    }

    /**
     * 新增流程表单
     */
    @SaCheckPermission("workflow:form:add")
    @Log(title = "流程表单", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@RequestBody WfFormBo bo) {
        return toAjax(formService.insertForm(bo));
    }

    /**
     * 修改流程表单
     */
    @SaCheckPermission("workflow:form:edit")
    @Log(title = "流程表单", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@RequestBody WfFormBo bo) {
        return toAjax(formService.updateForm(bo));
    }

    /**
     * 删除流程表单
     */
    @SaCheckPermission("workflow:form:remove")
    @Log(title = "流程表单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{formIds}")
    public R<Void> remove(@ApiParam("主键串") @NotEmpty(message = "主键不能为空") @PathVariable Long[] formIds) {
        return toAjax(formService.deleteWithValidByIds(Arrays.asList(formIds)) ? 1 : 0);
    }


    /**
     * 挂载流程表单
     */
    @Log(title = "流程表单", businessType = BusinessType.INSERT)
    @PostMapping("/addDeployForm")
    public R<Void> addDeployForm(@RequestBody WfDeployForm deployForm) {
        return toAjax(deployFormService.insertWfDeployForm(deployForm));
    }
}
