package com.lyr.mybatisjpaplugin.config;

import com.lyr.mybatisjpaplugin.interceptor.EnumInsertFillPlugin;
import com.lyr.mybatisjpaplugin.interceptor.OptimisticPlugin;
import com.lyr.mybatisjpaplugin.interceptor.UpdateTimePlugin;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author lyr
 * @create 2021/1/18 16:23
 */
@Configuration
public class MybatisFieldFillPlugin {
    // @Bean
    // @ConditionalOnMissingBean(UpdateTimePlugin.class)
    // public UpdateTimePlugin mybatisUpdateTimePlugin() {
    //     return new UpdateTimePlugin();
    // }
    //
    // @Bean
    // @ConditionalOnMissingBean(EnumInsertFillPlugin.class)
    // public EnumInsertFillPlugin enumInsertFillPlugin() {
    //     return new EnumInsertFillPlugin();
    // }

    @Bean
    ConfigurationCustomizer mybatisConfigurationCustomizer_auto_field_plugin() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                //自动设置时间
                configuration.addInterceptor(new EnumInsertFillPlugin());
                configuration.addInterceptor(new UpdateTimePlugin());
            }
        };
    }

}
