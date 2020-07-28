package com.bcm.h2h.bcmh2hcodegenerator.common.utils;

import org.apache.commons.lang3.CharUtils;

import java.util.*;

/**
 * @author yuelong.liang
 */
public class YamlUtils {

    /**
     * 将 xxx-xxx 的属性名转为 xxxXxx 的驼峰形势
     *
     * @param source source
     * @return newProperties
     */
    public static Properties toHump(Properties source) {
        Properties target = new Properties();
        Map<Object, Object> temp = new HashMap<>(source.size());
        for (Object objKey : source.keySet()) {
            if (!(objKey instanceof String)) {
                continue;
            }
            StringBuilder key = new StringBuilder((String) objKey);
            do {
                int index = key.indexOf("-");
                if (index < 0) {
                    break;
                }
                key.deleteCharAt(index);
                char ch = key.charAt(index);
                if (!CharUtils.isAsciiAlphaUpper(ch)) {
                    ch -= 32;
                }
                key.setCharAt(index, ch);

            } while (true);
            Object value = source.get(objKey);
            objKey = key.toString();
            if (value instanceof Map) {
                Properties newValue = new Properties();
                newValue.putAll((Map<?, ?>) value);
                value = newValue;
            }
            temp.put(objKey, value);
        }
        target.putAll(temp);
        return target;
    }

    public static List<Map<String, Object>> readStringList(String key, Properties properties) {
        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
        int index = 0;
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entrySet) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            String strKey = entry.getKey().toString();
            if (strKey.contains(key) && strKey.contains(String.valueOf(index))) {
                Map<String, Object> map;
                if (mapList.size() < index) {
                    map = new HashMap<>(16);
                    mapList.add(map);
                } else {
                    map = mapList.get(index);
                }
                String sub = strKey.substring(strKey.indexOf('.'));
                map.put(sub, entry.getValue());
            }
        }

        System.out.println(mapList);
        return mapList;
    }


}
