package com.lyr.mybatisjpaplugin.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 */
@AllArgsConstructor
@Data
public class VersionLockException extends RuntimeException{
    private String exceptionDetail;
}
