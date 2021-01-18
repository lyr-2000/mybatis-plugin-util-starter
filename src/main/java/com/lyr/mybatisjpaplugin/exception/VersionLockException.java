package com.lyr.mybatisjpaplugin.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author lyr
 * @create 2021/1/18 18:54
 */
@AllArgsConstructor
@Data
public class VersionLockException extends RuntimeException{
    private String exceptionDetail;
}
