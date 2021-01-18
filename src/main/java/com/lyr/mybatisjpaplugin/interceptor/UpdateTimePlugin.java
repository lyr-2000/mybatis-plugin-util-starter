package com.lyr.mybatisjpaplugin.interceptor;


import com.lyr.mybatisjpaplugin.annotation.CreateTime;
import com.lyr.mybatisjpaplugin.annotation.UpdateTime;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * 完成插件签名
 *  告诉 mybatis 当前插件用来拦截哪个对象的哪个方法
 * @Author lyr
 * @create 2020/12/19 22:55
 */
// @Configuration
@Intercepts(
        @Signature(type = Executor.class,method = "update",args = { MappedStatement.class, Object.class })
)

@Component
public class UpdateTimePlugin implements Interceptor {
    // @Override
    // public Object intercept(Invocation invocation) throws Throwable {
    //     return invocation.proceed();
    // }




    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        // 获取 SQL 命令
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 获取参数
        Object parameter = invocation.getArgs()[1];

        if (!(parameter instanceof Map)) {
            //如果 不是 HashMap 而是一个对象，才进行反射处理
            handleBean(parameter,sqlCommandType);
        }

        return invocation.proceed();
    }



    /**
     * 创建代理对象
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    /**
     * 插件注册时候，  property 属性注入进来
     * 可以拿到插件配置信息
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {

    }




    private void handleBean(Object parameter,SqlCommandType sqlCommandType) throws IllegalAccessException {
        // 获取私有成员变量
        Field[] declaredFields = parameter.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            field.setAccessible(true);
            //判断是不是 自定义的 接口


            if (field.getAnnotation(CreateTime.class) != null && /*field.get(parameter)==null &&*/ Date.class.isAssignableFrom(field.getType())) {
                if (SqlCommandType.INSERT.equals(sqlCommandType)) { // insert 语句插入 createTime

                    field.set(parameter, new Date());
                }
            }

            else if (field.getAnnotation(UpdateTime.class) != null /*&& field.get(parameter)==null*/ && Date.class.isAssignableFrom(field.getType())) { // insert 或 update 语句插入 updateTime
                if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {

                    field.set(parameter, new Date());
                }
            }
        }
    }
}
