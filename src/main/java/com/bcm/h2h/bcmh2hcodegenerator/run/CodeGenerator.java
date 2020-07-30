package com.bcm.h2h.bcmh2hcodegenerator.run;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.DBUtils;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.MyVelocityUtils;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.VelocityInitializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

/**
 * @author yuelong.liang
 */
@Slf4j
public class CodeGenerator {

    public static String vmJsonPath = "gen-config/vm.json";

    public static String genJsonPath = "gen-config/gen.json";

    public static String customJsonPath = "gen-config/custom.json";

    /**
     * 变量
     */
    public static JSONObject config;

    public static void main(String[] args) throws IOException, SQLException {
        // 初始化配置
        init();
        // 獲取模板列表
        List<JSONObject> vmList = MyVelocityUtils.getTemplateList(config);

        // 初始化 velocity
        VelocityInitializer.initVelocity();
        // 将变量放入 content
        log.info("config:" + config);
        VelocityContext context = MyVelocityUtils.prepareContext(config);
        boolean isGenTable = config.get("genTable") != null && config.getBooleanValue("genTable");
        // 渲染結果 list
        for (JSONObject vm : vmList) {
            // 渲染并输出模板到文件
            StringWriter writer = MyVelocityUtils.output(vm, config, context);
            if (isGenTable && vm.getString("path").contains("table.sql")) {
                // 执行 sql
                DBUtils.createTable(writer.toString());
            }
        }
        // 生成枚举类
        MyVelocityUtils.genEnums(config);
        log.info("生成完成");
    }

    /**
     * 初始化操作
     */
    public static void init() throws IOException {
        // 读取全局配置, 默认全局配置路径 classpath:/gen-config
        JSONObject global = readVmJson(vmJsonPath);
        // 将类名首字母小写，设置到 global properties 中
        global.put("lowClassName", StringUtils.uncapitalize(global.getString("className")));
        System.out.println(global);
        // 读取 gen 的配置, 并做预处理
        JSONObject gen = readVmJson(genJsonPath);
        System.out.println(gen);
        // 读取自定义变量
        JSONObject custom = readVmJson(customJsonPath);
        // 与 global 合并
        global.putAll(gen);
        global.putAll(custom);
        config = global;
        System.out.println(config);
    }

    public static JSONObject readVmJson(String vmJsonPath) throws IOException {
        return JSON.parseObject(StreamUtils.copyToString(new ClassPathResource(vmJsonPath).getInputStream(), StandardCharsets.UTF_8));
    }

}
