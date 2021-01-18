package com.lyr.mybatisjpaplugin.interceptor;


import com.lyr.mybatisjpaplugin.annotation.InsertValue;
import com.lyr.mybatisjpaplugin.common.BaseIntEnum;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;

/**
 * 自动insert 枚举字段
 * @Author lyr
 * @create 2020/12/20 18:30
 */
@Intercepts(
        @Signature(type = Executor.class,method = "update",args = { MappedStatement.class, Object.class })
)
public class EnumInsertFillPlugin implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getArgs().length<2) {
            return invocation.proceed();
        }

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

        // 获取 SQL 命令
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        // 获取参数
        Object parameter = invocation.getArgs()[1];

        if(  !(parameter instanceof Map) ) {
            // 获取私有成员变量
            //如果不是 Map 类型，反射 解析出 class 里面 的 枚举属性
            //如果是 insert 语句，填充默认枚举
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

        // insert 语句
        if (sqlCommandType.equals(SqlCommandType.INSERT)) {
            //TODO: 必须是插入语句
            Field[] declaredFields = parameter.getClass().getDeclaredFields();
            //如果不等于 null
            for (Field field : declaredFields) {
                field.setAccessible(true);
                //判断是不是 自定义的 接口
                Class clazz = field.getType();
                if (clazz.isAnnotationPresent(InsertValue.class)) {
                    //获取值
                    Object val = field.get(parameter);
                    //如果 status 不为空，说明赋值过了，否则的话 进行复赋值
                    if (val == null) {
                        //field 的类型是枚举，并且实现了 BaseIntEnum 接口反向查找
                        if (BaseIntEnum.class.isAssignableFrom(clazz)) {
                            //可以设置
                            BaseIntEnum[] values = (BaseIntEnum[]) clazz.getEnumConstants();
                            //获取 insertValue
                            InsertValue insertValue = (InsertValue) clazz.getAnnotation(InsertValue.class);
                            int code = insertValue.whenFieldAbsent();
                            BaseIntEnum xx = BaseIntEnum.valueOf(values,code);
                            //设置默认值
                            field.set(parameter,xx);
                        }
                    }
                }

            }



        }
    }
}
