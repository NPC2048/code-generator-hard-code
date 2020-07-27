package com.bcm.h2h.bcmh2hcodegenerator.common.enmus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据源、表单字段、Java字段映射枚举类
 *
 * @author yuelong.liang
 */
@AllArgsConstructor
public enum JavaType {
    // 暂不考虑基本类型
//    INT_PRIMITIVE("int" , null),
    LONG("Long"),
    STRING("String"),
    INTEGER("Integer"),
    Double("Double"),
    BigDecimal("BigDecimal"),
    Date("Date");

    @Getter
    private String javaType;

}
