package com.bcm.h2h.bcmh2hcodegenerator.common.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author yuelong.liang
 */
public class GenUtils {

    public static void handleColumns(JSONObject config) {
        Object columns = config.get("columns");
        Object arr = config.getJSONArray("columns");
        System.exit(0);
    }

}
