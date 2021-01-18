package com.lyr.mybatisjpaplugin.handler;

import com.lyr.mybatisjpaplugin.common.BaseIntEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author lyr
 * @create 2020/12/19 20:48
 */
@MappedJdbcTypes(JdbcType.INTEGER)
@MappedTypes(BaseIntEnum.class)
// @Component
public  class BaseEnumTypeHandler<E extends BaseIntEnum> extends BaseTypeHandler<E> {
    /**
     * 枚举数组的 类
     */
    private final Class<E> enumClazz;
    /**
     * 枚举数组
     */
    private final E[] values;
    /**
     * 遇到空对象是否设置默认值
     */
    // private E defaultEnum;

    private E codeOf(int code) {
        for (E e : values) {
            if (e.getValue() == code) {
                return e;
            }
        }
        return null;
    }


    public BaseEnumTypeHandler(Class<E> enumClazz) {
        if (enumClazz == null) {
            throw new IllegalArgumentException("base enum TypeHandler enum class cannot be null !@!!!");
        }
        this.enumClazz = enumClazz;
        values = enumClazz.getEnumConstants();
        // if (values!=null && values.length>0) {
        //     defaultEnum = (E)values[0].getValueWhenObjectFieldNull();
        //     // System.out.println(defaultEnum);
        //     System.out.println("default value = "+defaultEnum);
        // }

    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E e, JdbcType jdbcType) throws SQLException {
        // preparedStatement.set
        preparedStatement.setInt(i, e.getValue());
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {

        super.setParameter(ps, i, parameter, jdbcType);
    }

    /**
     * 可以空的对象
     *
     * @param resultSet
     * @param s
     * @return
     * @throws SQLException
     */
    @Override
    public E getNullableResult(ResultSet resultSet, String s) throws SQLException {
        int code = resultSet.getInt(s);

        return resultSet.wasNull() ? null : codeOf(code);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int i) throws SQLException {
        int code = resultSet.getInt(i);
        return resultSet.wasNull() ? null : codeOf(code);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        int code = callableStatement.getInt(i);
        return callableStatement.wasNull() ? null : codeOf(code);
    }
}
