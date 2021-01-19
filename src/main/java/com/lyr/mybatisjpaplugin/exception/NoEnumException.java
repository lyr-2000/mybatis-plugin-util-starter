package com.lyr.mybatisjpaplugin.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @Author lyr
 * @create 2021/1/19 13:11
 */
@Data
// @AllArgsConstructor
@RequiredArgsConstructor
public class NoEnumException extends RuntimeException{
    String message;


    public NoEnumException(String message) {
        super(message);
    }
}
