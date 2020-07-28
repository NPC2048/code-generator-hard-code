package com.bcm.h2h.bcmh2hcodegenerator.common.enmus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 查询类型
 *
 * @author yuelong.liang
 */
@AllArgsConstructor
public enum QueryType {

    EQ("="),
    NE("!="),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    LIKE("like"),
    BETWEEN("between")
    ;

    @Getter
    private String code;

}
