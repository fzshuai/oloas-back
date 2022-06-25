package com.fzshuai.system.mapper;

import com.fzshuai.common.core.mapper.BaseMapperPlus;
import com.fzshuai.system.domain.SysUserRole;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author fzshuai
 */
public interface SysUserRoleMapper extends BaseMapperPlus<SysUserRoleMapper, SysUserRole, SysUserRole> {

    List<Long> selectUserIdsByRoleId(Long roleId);

}
