package com.bcm.h2h.bcmh2hcodegenerator.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.VelocityContext;

import java.util.*;

/**
 * @author yuelong.liang
 */
public class MyVelocityUtils {

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<JSONObject> getTemplateList(JSONObject config) {
        List<JSONObject> templates = new ArrayList<>();
        JSONArray vm = config.getJSONArray("vm");
        for (int i = 0; i < vm.size(); i++) {
            JSONObject obj = vm.getJSONObject(i);
            templates.add(obj);
        }
        return templates;
    }

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(JSONObject object) {

        String functionName = object.getString("functionName");
//        JSONArray importList = object.getJSONArray("importList");
        Set<String> importList = getImportList(object);
        VelocityContext velocityContext = new VelocityContext();
        Set<Map.Entry<String, Object>> entrySet = object.entrySet();
        // 添加变量到 context
        for (Map.Entry<String, Object> entry : entrySet) {
            velocityContext.put(entry.getKey(), entry.getValue());
        }
        velocityContext.put("config", object);
        velocityContext.put("tableName", object.getString("tableName"));
        velocityContext.put("functionName", StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("showName", object.getString("showName"));
        velocityContext.put("ClassName", object.getString("ClassName"));
        velocityContext.put("className", StringUtils.uncapitalize(object.getString("ClassName")));
        velocityContext.put("author", object.getString("author"));
        velocityContext.put("datetime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        velocityContext.put("pkColumn", object.getString("pkCoumn"));
        velocityContext.put("importList", importList.toArray());
//        velocityContext.put("permissionPrefix" , getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns", object.getJSONArray("columns"));
        return velocityContext;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        return StringUtils.substring(packageName, 0, lastIndex);
    }

    /**
     * 从 vm list 获取对应模块名，文件名，生成绝对路径名
     *
     * @param vm
     * @param config
     * @return
     */
    public static String getFileName(JSONObject vm, JSONObject config) {
        // 生成路径
        String genPath = config.getString("genPath");
        // 模块名
        String moduleName = vm.getString("moduleName");
        // 文件名
        String fileName = vm.getString("fileName");

        if (fileName.endsWith(".java") || fileName.endsWith("Mapper.xml")) {
            // 大写类名
            String className = config.getString("className");
            // 基础包名替换为路径名
            String packagePath = StringUtils.replace(config.getString("packageName"), ".", "/");
            fileName = StringUtils.format(fileName, className);
            return StringUtils.joinWith("/", genPath, moduleName, config.getString("javaPath"), packagePath, fileName);
        }
        // 业务名称
        String businessName = config.getString("businessName");
        fileName = StringUtils.format(fileName, businessName);
        if (fileName.endsWith(".html") || fileName.endsWith(".jsp") || fileName.endsWith(".ftl")) {
            return StringUtils.joinWith("/", genPath, moduleName, config.getString("htmlPath"), fileName);
        }
        if (fileName.endsWith(".js")) {
            return StringUtils.joinWith("/", genPath, moduleName, config.getString("jsPath"), fileName);
        }
        if (fileName.endsWith(".sql")) {
            return StringUtils.joinWith("/", genPath, moduleName, fileName);
        }
        throw new RuntimeException("未知文件格式处理类型: " + fileName);
    }

    public static Set<String> getImportList(JSONObject config) {
        JSONArray columns = config.getJSONArray("columns");
        Set<String> importList = new HashSet<>();
        for (int i = 0; i < columns.size(); i++) {
            JSONObject column = columns.getJSONObject(i);
            String type = column.getString("javaType");
            switch (type) {
                case "Date":
                    importList.add("java.util.Date");
                    break;
                case "BigDecimal":
                    importList.add("java.math.BigDecimal");
                    break;
                default:
            }
        }
        return importList;
    }

}
