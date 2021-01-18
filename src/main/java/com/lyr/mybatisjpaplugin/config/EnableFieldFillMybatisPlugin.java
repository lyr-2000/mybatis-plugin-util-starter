package com.lyr.mybatisjpaplugin.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author lyr
 * @create 2021/1/18 16:23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration(MybatisFieldFillPlugin.class)
public @interface EnableFieldFillMybatisPlugin {
}
