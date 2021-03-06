package com.lyr.mybatisjpaplugin.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.lyr.mybatisjpaplugin.exception.NoEnumException;
import org.springframework.lang.Nullable;

/**
 *  枚举和 int 互转
 */
public interface BaseIntEnum<E extends Enum<E>> {

    /**
     * 存入数据库的值
     * @return xxxoo 枚举返回值
     */

    Integer getValue();



    @JsonValue
    default Integer getJsonValue() {
        return getValue();
    }






    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    @Nullable
    public static  <T extends BaseIntEnum> T valueOf(T[] values, Integer code) {
        // System.out.println("code ="+code);
        if (code == null) {
            return null;
        }
        for (T a:values) {
            if (a.getValue().equals(code)) {
                return a;
            }
        }
        // throw new NoEnumException("枚举参数不对");
        return null;
    }


}
