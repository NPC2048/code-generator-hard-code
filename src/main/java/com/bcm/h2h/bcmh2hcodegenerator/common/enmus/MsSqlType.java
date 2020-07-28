package com.bcm.h2h.bcmh2hcodegenerator.common.enmus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yuelong.liang
 */

@AllArgsConstructor
public enum MsSqlType {

    TINYINT("tinyint"),
    SMALLINT("smallint"),
    INT("int"),
    BIGINT("bigint"),
    VARCHAR2("varchar2"),
    NVARCHAR("nvarchar"),
    NTEXT("ntext"),
    DECIMAL("decimal"),
    DATETIME("datetime"),
    DATETIME2("datetime2");

    @Getter
    private String type;

}
