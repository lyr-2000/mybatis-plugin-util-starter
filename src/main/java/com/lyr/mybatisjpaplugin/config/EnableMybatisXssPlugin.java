package com.lyr.mybatisjpaplugin.config;

import com.lyr.mybatisjpaplugin.interceptor.EscapeXssPlugin;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author lyr
 * @create 2021/1/18 16:16
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration(MybatisPluginXssConfig.class)
public @interface EnableMybatisXssPlugin {

}
