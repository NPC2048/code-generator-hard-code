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
    public static List<Map.Entry<String, String>> getTemplateList(JSONObject config) {
        List<Map.Entry<String, String>> templates = new ArrayList<>();
        JSONObject vm = config.getJSONObject("vm");
        Set<Map.Entry<String, Object>> set = vm.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
            Map.Entry<String, String> newEntry = new Map.Entry<String, String>() {
                @Override
                public String getKey() {
                    return entry.getKey();
                }

                @Override
                public String getValue() {
                    return String.valueOf(entry.getValue());
                }

                @Override
                public String setValue(String value) {
                    return String.valueOf(entry.setValue(value));
                }
            };
            templates.add(newEntry);
        }
        return templates;
    }

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(JSONObject object) {

        String moduleName = object.getString("moduleName");
        String businessName = object.getString("bussinessName");
        String packageName = object.getString("packageName");
        String functionName = object.getString("functionName");
        JSONArray importList = object.getJSONArray("importList");

        VelocityContext velocityContext = new VelocityContext();
        Set<Map.Entry<String, Object>> entrySet = object.entrySet();
        // 添加变量到 context
        for (Map.Entry<String, Object> entry : entrySet) {
            velocityContext.put(entry.getKey(), entry.getValue());
        }
        velocityContext.put("root" , object);
        velocityContext.put("tableName" , object.getString("tableName"));
        velocityContext.put("functionName" , StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("showName" , object.getString("showName"));
        velocityContext.put("ClassName" , object.getString("ClassName"));
        velocityContext.put("className" , StringUtils.uncapitalize(object.getString("ClassName")));
        velocityContext.put("moduleName" , moduleName);
        velocityContext.put("businessName" , businessName);
        velocityContext.put("basePackage" , getPackagePrefix(packageName));
        velocityContext.put("packageName" , packageName);
        velocityContext.put("author" , object.getString("author"));
        velocityContext.put("datetime" , DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        velocityContext.put("pkColumn" , object.getString("pkCoumn"));
        velocityContext.put("importList" , importList == null ? null : importList.toArray());
//        velocityContext.put("permissionPrefix" , getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns" , object.getJSONArray("columns"));
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

    public static String getFileName(String template, JSONObject config) {
        // 项目路径
        String projectPath = config.getString("projectPath");
        // mybatis 路径
        String templatesPath = config.getString("templatesPath");
        // 包路径
        String packageName = config.getString("packageName");
        // 模块名
        String moduleName = config.getString("moduleName");
        // 大写类名
        String className = config.getString("className");
        // 业务名称
        String businessName = config.getString("businessName");

        String javaPath = projectPath + "/" + StringUtils.replace(packageName, "." , "/");
        String mybatisPath = config.getString("mybatisPath") + "/" + moduleName;
        String htmlPath = templatesPath + "/" + moduleName + "/" + businessName;
        String jsPath = config.getString("jsPath");
        String jspPath = config.getString("jspPath");
        // 文件名称
        String fileName = "" ;
        if (template.contains("entity.java.vm")) {
            fileName = StringUtils.format("{}/entity/{}.java" , javaPath, className);
        }
        if (template.contains("mapper.java.vm")) {
            fileName = StringUtils.format("{}/mapper/{}Mapper.java" , javaPath, className);
        } else if (template.contains("service.java.vm")) {
            fileName = StringUtils.format("{}/service/I{}Service.java" , javaPath, className);
        } else if (template.contains("serviceImpl.java.vm")) {
            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java" , javaPath, className);
        } else if (template.contains("controller.java.vm")) {
            fileName = StringUtils.format("{}/controller/{}Controller.java" , javaPath, className);
        } else if (template.contains("mapper.xml.vm")) {
            fileName = StringUtils.format("{}/{}Mapper.xml" , mybatisPath, className);
        } else if (template.contains("form.java.vm")) {
            fileName = StringUtils.format("{}/form/{}Form.java" , javaPath, className);
        } else if (template.contains("model.java.vm")) {
            fileName = StringUtils.format("{}/model/{}Model.java" , javaPath, className);
        } else if (template.contains("index.vm")) {
            return StringUtils.format("{}/index.jsp" , jspPath, businessName);
        } else if (template.contains("index.js.vm")) {
            return StringUtils.format("{}/index.js" , jsPath, businessName);
        }
        if (template.contains("list.html.vm")) {
            fileName = StringUtils.format("{}/{}.html" , jspPath, businessName);
        } else if (template.contains("add.html.vm")) {
            fileName = StringUtils.format("{}/add.html" , jspPath);
        } else if (template.contains("edit.html.vm")) {
            fileName = StringUtils.format("{}/edit.html" , jspPath);
        } else if (template.contains("sql.vm")) {
            fileName = businessName + "Menu.sql" ;
        }
        return fileName;
    }

}
