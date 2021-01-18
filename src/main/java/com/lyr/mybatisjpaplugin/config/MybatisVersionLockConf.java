package com.lyr.mybatisjpaplugin.config;

import com.lyr.mybatisjpaplugin.interceptor.OptimisticPlugin;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author lyr
 * @create 2021/1/18 16:29
 */
@Configuration
public class MybatisVersionLockConf {
    // @Bean
    // public OptimisticPlugin optimisticPlugin() {
    //     return new OptimisticPlugin();
    // }

    @Bean
    ConfigurationCustomizer mybatisConfigurationCustomizer_version_lock_plugin() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                //自动设置时间
                configuration.addInterceptor(new OptimisticPlugin());
            }
        };
    }



}
