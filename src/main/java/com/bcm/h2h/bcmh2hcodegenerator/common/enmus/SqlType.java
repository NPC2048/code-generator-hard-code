package com.bcm.h2h.bcmh2hcodegenerator.common.enmus;

import lombok.AllArgsConstructor;

/**
 * @author yuelong.liang
 */

@AllArgsConstructor
public enum SqlType {

    MSSQL(JavaType.STRING, "varchar2");

    private JavaType dataType;

    private String sqlType;
}
