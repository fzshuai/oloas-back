package com.fzshuai.common.excel;

import com.alibaba.excel.read.listener.ReadListener;

/**
 * Excel 导入监听
 *
 * @author fzshuai
 */
public interface ExcelListener<T> extends ReadListener<T> {

    ExcelResult<T> getExcelResult();

}
