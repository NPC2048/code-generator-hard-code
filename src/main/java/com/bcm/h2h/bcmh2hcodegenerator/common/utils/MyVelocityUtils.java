package com.bcm.h2h.bcmh2hcodegenerator.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yuelong.liang
 */
public class MyVelocityUtils {

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList() {
        List<String> templates = new ArrayList<>();
        templates.add("vm/java/entity.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/controller.java.vm");
        templates.add("vm/java/model/model.java.vm");
        templates.add("vm/java/form/form.java.vm");
        templates.add("vm/java/controller.java.vm");
        templates.add("vm/xml/sqlserver/mapper.xml.vm");
        templates.add("vm/html/index.vm");
        templates.add("vm/html/add.vm");
        templates.add("vm/sql/sql.vm");
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

}
