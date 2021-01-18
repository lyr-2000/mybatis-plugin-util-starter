package com.lyr.mybatisjpaplugin.config;

// import com.example.demo.interceptor.*;
import com.lyr.mybatisjpaplugin.interceptor.*;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author lyr
 * @create 2020/12/20 0:02
 */

@Configuration
public class MybatisPluginXssConfig {

    // @Bean
    // @ConditionalOnMissingBean(EscapeXssPlugin.class)
    // public EscapeXssPlugin escapexssplugin() {
    //     return new EscapeXssPlugin();
    // }



    @Bean
    ConfigurationCustomizer mybatisConfigurationCustomizer_xss_plugin() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                //自动设置时间
                configuration.addInterceptor(new EscapeXssPlugin());
            }
        };
    }

    // @Resource
    // private TableIdPlugin tableIdPlugin;

    // /**
    //  * @return 注册 mybatis 拦截器
    //  */
    // @Bean
    // ConfigurationCustomizer mybatisConfigurationCustomizer() {
    //     return new ConfigurationCustomizer() {
    //         @Override
    //         public void customize(org.apache.ibatis.session.Configuration configuration) {
    //             //自动设置时间
    //             configuration.addInterceptor(new UpdateTimePlugin());
    //             //自动过滤 XSS 脚本
    //             configuration.addInterceptor(new EscapeXssPlugin());
    //             //生成分布式ID
    //             // configuration.addInterceptor(new TableIdPlugin());
    //             //自动插入值
    //             configuration.addInterceptor(new EnumInsertFillPlugin());
    //             //慢查询日志分析
    //             configuration.addInterceptor(new MybatisQueryLogPlugin(true));
    //             configuration.addInterceptor(new OptimisticPlugin());
    //         }
    //     };
    // }
}
