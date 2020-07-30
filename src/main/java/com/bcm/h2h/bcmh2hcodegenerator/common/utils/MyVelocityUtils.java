package com.bcm.h2h.bcmh2hcodegenerator.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcm.h2h.bcmh2hcodegenerator.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author yuelong.liang
 */
@Slf4j
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

    /**
     * 枚举类生成
     *
     * @param config vm.json
     */
    public static void genEnums(JSONObject config) throws IOException {
        // 获取要生成的枚举变量
        JSONArray genEnums = config.getJSONArray("genEnums");
        // 获取枚举模板配置
        JSONObject enumVm = config.getJSONObject("enumVm");
        for (int i = 0; i < genEnums.size(); i++) {
            // 获取当期数组位置的枚举变量, 设置到 context
            JSONObject enumConfig = genEnums.getJSONObject(i);
            VelocityContext enumContext = new VelocityContext(enumConfig);
            // 设置作者
            enumContext.put("author", config.get("author"));
            // 设置 datetime
            enumContext.put("datetime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            // 设置生成路径
            enumConfig.put("genPath", config.get("genPath"));
            // javaPath
            enumConfig.put("javaPath", config.get("javaPath"));
            output(enumVm, enumConfig, enumContext);
        }
    }

    /**
     * 将 SpringWriter 的内容输出到指定路径
     *
     * @param vm     vm
     * @param config config
     * @return content
     * @throws IOException e
     */
    public static StringWriter output(JSONObject vm, JSONObject config, VelocityContext context) throws IOException {
        StringWriter writer = new StringWriter();
        // 读取模板
        Template template = Velocity.getTemplate(vm.getString("path"), Constants.UTF8);
        // 渲染变量到模板内容
        template.merge(context, writer);
        String fileName = MyVelocityUtils.getFileName(vm, config);
        // 输出到指定目录
        Path outPath = Paths.get(fileName);
        // 判断路径是存在, 不存在则创建
        Path parent = outPath.getParent();
        if (Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
        log.info("生成 : " + vm.getString("path") + " 到 :" + outPath);
        FileCopyUtils.copy(writer.toString(), new PrintWriter(outPath.toFile()));
        log.info("write success");
        return writer;
    }

}
