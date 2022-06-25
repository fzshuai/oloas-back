package com.fzshuai.system.service;

import com.fzshuai.common.core.domain.PageQuery;
import com.fzshuai.common.core.page.TableDataInfo;
import com.fzshuai.system.domain.SysOss;
import com.fzshuai.system.domain.bo.SysOssBo;
import com.fzshuai.system.domain.vo.SysOssVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

/**
 * 文件上传 服务层
 *
 * @author fzshuai
 */
public interface ISysOssService {

    TableDataInfo<SysOssVo> queryPageList(SysOssBo sysOss, PageQuery pageQuery);

    SysOss getById(Long ossId);

    SysOss upload(MultipartFile file);

    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
