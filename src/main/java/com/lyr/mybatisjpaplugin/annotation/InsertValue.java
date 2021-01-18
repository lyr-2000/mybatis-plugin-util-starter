package com.lyr.mybatisjpaplugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打到 枚举类定义上，用于填充默认值
 * @Author lyr
 * @create 2020/12/20 18:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InsertValue {
    int whenFieldAbsent() ;
}
