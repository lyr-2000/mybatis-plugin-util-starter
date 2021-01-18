package com.lyr.mybatisjpaplugin.interceptor;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;

import javax.persistence.Column;
import javax.persistence.Version;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author lyr
 * @create 2020/12/21 14:21
 */
@Slf4j
@Intercepts(
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
)
public class OptimisticPlugin implements Interceptor {
    //设置 concurrentHashMap
    // private ConcurrentHashMap<String, String> versionMap = new ConcurrentHashMap<>();

    final static int mapped_statement_index = 0;
    final static int parameter_index = 1;
    final static int rowbounds_index = 2;
    final static int result_handler_index = 3;

    /**
     * 类的乐观锁字段 的缓存
     */
    final private Map<Class, Object> cache = new ConcurrentHashMap<>();
    /**
     * 表示是否访问过
     */
    final private Integer empty = -1;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] queryArgs = invocation.getArgs();
        // 获取 StatementHandler，实际是 RoutingStatementHandler
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = queryArgs[parameter_index];
        if (parameter instanceof Map || parameter == null) {
            return invocation.proceed();
        }
        Object field = cache.get(parameter.getClass());
        log.info("field {}", field);
        if (field == empty) {
            //如果之前扫描过类了，并且没有乐观锁字段的话，直接加速
            return invocation.proceed();
        }
        if (field != null) {
            return handleFieldParameter(parameter, (Field) field, invocation, queryArgs, mappedStatement);
        }
        return handleBean(parameter, queryArgs, invocation, mappedStatement);
        //如果是 bean的话，进行处理
        // long start = System.nanoTime();
        // Object result =  handleBean(parameter,queryArgs,invocation,mappedStatement);
        // long end = System.nanoTime();
        // long r = end - start;
        // // System.out.println(start+" -> "+end);
        // System.out.println("反射 操作执行耗时 "+(r)+"");
        // return result;
    }


    /**
     * 创建代理对象
     *
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 插件注册时候，  property 属性注入进来
     * 可以拿到插件配置信息
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {

    }


    /**
     * 处理传入 的 java bean
     *
     * @param parameter
     * @param queryArgs
     * @param invocation
     * @param mappedStatement
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object handleBean(Object parameter, Object[] queryArgs, Invocation invocation, MappedStatement mappedStatement) throws InvocationTargetException, IllegalAccessException {
        //不是 Map ,而是一个对象
        Field versionField = null;
        //version 值
        Object versionValue = null;

        //注解
        Version remark = null;
        for (Field field : parameter.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Version.class)) {
                //获取字段
                versionValue = field.get(parameter);
                versionField = field;
                remark = field.getAnnotation(Version.class);
                //找到 version字段，然后跳出
                cache.put(parameter.getClass(), field);
                break;
            }
            //设置可访问，检查是否 有 version字段
        }
        //如果没有标记，设置 为 没有乐观锁字段
        cache.putIfAbsent(parameter.getClass(), empty);
        //还要初始化字段
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType.equals(SqlCommandType.INSERT)) {
            //如果是 insert 语句
            if (versionValue == null && remark != null) {

                versionField.set(parameter, 0);
            }
            //insert 语句，直接加速
            return invocation.proceed();
        }

        //获取 了 versoinField 之后，反射修改sql
        if (versionValue == null) {
            //如果 用户没有 设置version 字段，不修改 sql 语句，直接执行
            return invocation.proceed();
        }
        //versionValue != null
        /*
         *
         * 修改 sql 语句
         *
         * */
        //versionValue !=null, and  versionValue instanceOf Long or  instanceOf Integer
        //old Version value
        Object whereAndValue = null;
        //
        // String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        // Configuration configuration = mappedStatement.getConfiguration();

        if (versionValue instanceof Integer ) {
            whereAndValue = versionValue;
            versionValue = ((Integer) versionValue + 1);
        } else if (versionValue instanceof Long) {
            whereAndValue = versionValue;
            versionValue = ((Long) versionValue + 1);
        } else {
            //不知道怎么处理，也是直接执行
            return invocation.proceed();
        }

        versionField.set(parameter, versionValue);
        //原始 sql
        String originSql = boundSql.getSql();
        String newSql = null;
        //数据库的乐观锁字段
        // String versionColumnName = "";
        //
        // if (versionColumnName.length()<=1)
        // {
        //如果 注解是空串的话
        String versionColumnName = null;
        if (versionField.isAnnotationPresent(Column.class)) {
            Column columnMark = versionField.getAnnotation(Column.class);
            versionColumnName = columnMark.name();
            if (versionColumnName.isEmpty()) {
                versionColumnName = versionField.getName();
            }
        }else{
            //使用 bean 的属性名字
            versionColumnName = versionField.getName();
        }
        // }
        // System.out.println(originSql+"\r\n");
        //格式一定是
        /*
         *
         * update student  set a=b,version = newVersion
         *  where xx = 1  and version = oldversion
         *
         * update student set a=b ,xx
         * where version = old version
         *
         * */
        if (originSql.lastIndexOf(" where ") > 0) {
            newSql = originSql + " and " + versionColumnName + " = " + whereAndValue;
        } else {
            newSql = originSql + " where " + versionColumnName + " = " + whereAndValue;
        }

        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), newSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        // 把新的查询放到statement里
        MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
        queryArgs[mapped_statement_index] = newMs;
        return invocation.proceed();
    }


    private Object handleFieldParameter(Object parameter, Field field, Invocation invocation, Object[] queryArgs, MappedStatement mappedStatement) throws InvocationTargetException, IllegalAccessException {


        if (field == null || parameter == null) {
            //如果传入参数是空的话
            return invocation.proceed();
        }
        // field.setAccessible(true);
        //获取注解信息
        Version remark = field.getAnnotation(Version.class);
        Object versionValue = field.get(parameter);

        //还要初始化字段
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (sqlCommandType.equals(SqlCommandType.INSERT)) {
            //如果是 insert 语句
            if (versionValue == null && remark != null) {
                field.set(parameter, 0);
            }
            //insert 语句，直接加速
            return invocation.proceed();
        }

        //获取 了 versoinField 之后，反射修改sql
        if (versionValue == null) {
            //如果 用户没有 设置version 字段，不修改 sql 语句，直接执行
            return invocation.proceed();
        }
        /*
         *
         * 修改 sql 语句
         *
         * */
        //versionValue !=null, and  versionValue instanceOf Long or  instanceOf Integer
        //old Version value
        Object whereAndValue = null;
        //
        // String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        // Configuration configuration = mappedStatement.getConfiguration();

        if (versionValue instanceof Integer) {
            whereAndValue = versionValue;
            versionValue = ((Integer) versionValue + 1);
        } else if (versionValue instanceof Long) {
            whereAndValue = versionValue;
            versionValue = ((Long) versionValue + 1);
        } else {
            //不知道怎么处理，也是直接执行
            return invocation.proceed();
        }

        field.set(parameter, versionValue);
        //原始 sql
        String originSql = boundSql.getSql();
        String newSql = null;
        //数据库的乐观锁字段
        String versionColumnName = null;


        //如果 注解是空串的话
        versionColumnName = field.getName();

        // System.out.println(originSql+"\r\n");
        //格式一定是
        /*
         *
         * update student  set a=b,version = newVersion
         *  where xx = 1  and version = oldversion
         *
         * update student set a=b ,xx
         * where version = old version
         *
         * */
        if (originSql.lastIndexOf(" where ") > 0) {
            newSql = originSql + " and " + versionColumnName + " = " + whereAndValue;
        } else {
            newSql = originSql + " where " + versionColumnName + " = " + whereAndValue;
        }

        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), newSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        // 把新的查询放到statement里
        MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));
        queryArgs[mapped_statement_index] = newMs;
        return invocation.proceed();
    }


    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }


    private static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }


}
