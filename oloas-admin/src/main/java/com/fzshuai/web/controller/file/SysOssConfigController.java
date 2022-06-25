package com.fzshuai.web.controller.file;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fzshuai.common.annotation.Log;
import com.fzshuai.common.annotation.RepeatSubmit;
import com.fzshuai.common.core.controller.BaseController;
import com.fzshuai.common.core.domain.PageQuery;
import com.fzshuai.common.core.domain.R;
import com.fzshuai.common.core.page.TableDataInfo;
import com.fzshuai.common.core.validate.AddGroup;
import com.fzshuai.common.core.validate.EditGroup;
import com.fzshuai.common.core.validate.QueryGroup;
import com.fzshuai.common.enums.BusinessType;
import com.fzshuai.system.domain.bo.SysOssConfigBo;
import com.fzshuai.system.domain.vo.SysOssConfigVo;
import com.fzshuai.system.service.ISysOssConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * 对象存储配置Controller
 *
 * @author fzshuai
 */
@Validated
@Api(value = "对象存储配置控制器", tags = {"对象存储配置管理"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/oss/config")
public class SysOssConfigController extends BaseController {

    private final ISysOssConfigService sysOssConfigService;

    /**
     * 查询对象存储配置列表
     */
    @ApiOperation("查询对象存储配置列表")
    @SaCheckPermission("system:oss:list")
    @GetMapping("/list")
    public TableDataInfo<SysOssConfigVo> list(@Validated(QueryGroup.class) SysOssConfigBo bo, PageQuery pageQuery) {
        return sysOssConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取对象存储配置详细信息
     */
    @ApiOperation("获取对象存储配置详细信息")
    @SaCheckPermission("system:oss:query")
    @GetMapping("/{ossConfigId}")
    public R<SysOssConfigVo> getInfo(@ApiParam("OSS配置ID")
                                              @NotNull(message = "主键不能为空")
                                              @PathVariable("ossConfigId") Long ossConfigId) {
        return R.ok(sysOssConfigService.queryById(ossConfigId));
    }

    /**
     * 新增对象存储配置
     */
    @ApiOperation("新增对象存储配置")
    @SaCheckPermission("system:oss:add")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysOssConfigBo bo) {
        return toAjax(sysOssConfigService.insertByBo(bo) ? 1 : 0);
    }

    /**
     * 修改对象存储配置
     */
    @ApiOperation("修改对象存储配置")
    @SaCheckPermission("system:oss:edit")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysOssConfigBo bo) {
        return toAjax(sysOssConfigService.updateByBo(bo) ? 1 : 0);
    }

    /**
     * 删除对象存储配置
     */
    @ApiOperation("删除对象存储配置")
    @SaCheckPermission("system:oss:remove")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossConfigIds}")
    public R<Void> remove(@ApiParam("OSS配置ID串")
                                   @NotEmpty(message = "主键不能为空")
                                   @PathVariable Long[] ossConfigIds) {
        return toAjax(sysOssConfigService.deleteWithValidByIds(Arrays.asList(ossConfigIds), true) ? 1 : 0);
    }

    /**
     * 状态修改
     */
    @ApiOperation("状态修改")
    @SaCheckPermission("system:oss:edit")
    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysOssConfigBo bo) {
        return toAjax(sysOssConfigService.updateOssConfigStatus(bo));
    }
}
