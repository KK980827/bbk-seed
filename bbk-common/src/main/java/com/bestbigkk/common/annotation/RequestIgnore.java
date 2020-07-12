package com.bestbigkk.common.annotation;

import java.lang.annotation.*;

/**
* @author: 开
* @date: 2020-04-26 12:12:06
* @describe: 作用在一个实现FieldIgnore接口的实体类上面，指定具体要被切面拦截并置为Null的字段。
*/
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestIgnore {
    String[] ignoreProperties();
}
