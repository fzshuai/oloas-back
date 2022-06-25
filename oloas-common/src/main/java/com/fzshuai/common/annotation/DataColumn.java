package com.fzshuai.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限
 *
 * @author fzshuai
 * @version 3.5.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataColumn {

    /**
     * 占位符关键字
     */
    String key() default "deptName";

    /**
     * 占位符替换值
     */
    String value() default "dept_id";

}
