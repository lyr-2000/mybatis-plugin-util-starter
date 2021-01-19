package com.lyr.mybatisjpaplugin.interceptor;

import com.lyr.mybatisjpaplugin.annotation.UnEscape;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.web.util.HtmlUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

/**
 */
@Intercepts(
        @Signature(type = Executor.class,method = "update",args = { MappedStatement.class, Object.class })
)
public class EscapeXssPlugin implements Interceptor {

    // @Retention(RetentionPolicy.RUNTIME)
    // @Target({ElementType.FIELD})
    // public @interface UnEscapeHtml{
    //
    // }



    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getArgs().length<2)
            return invocation.proceed();

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        // 获取 SQL 命令
        // SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 获取参数
        Object parameter = invocation.getArgs()[1];
        if (parameter==null)
            //直接加速，防止反射调用 性能下降
            return invocation.proceed();

        if (!(parameter instanceof Map) ) {
            // 获取私有成员变量
            handleBean(parameter);

        }else {
            //如果是 Map 的话
            handleMap((Map<Object, Object>) parameter);

        }



        return invocation.proceed();
    }


    /**
     * 创建代理对象

     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    /**
     * 插件注册时候，  property 属性注入进来
     * 可以拿到插件配置信息

     */
    @Override
    public void setProperties(Properties properties) {

    }



    private void handleBean(Object parameter) throws IllegalAccessException {
        Field[] declaredFields = parameter.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            field.setAccessible(true);

            if (String.class.isAssignableFrom(field.getType())   && field.getAnnotation(UnEscape.class)==null) {
                //如果没有标记，默认全部对 HTML转义
                String fieldValue = (String) field.get(parameter);
                if (fieldValue!=null) {
                    //escape xss value

                    field.set(parameter,escape(fieldValue));
                }
            }
        }
    }


    private void handleMap(Map<Object, Object> parameter) {
        for (Map.Entry entry:  parameter.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                entry.setValue(escape((String) value));
            }
        }
    }


    private String escape(String html) {
        return HtmlUtils.htmlEscape(html);
    }
}
