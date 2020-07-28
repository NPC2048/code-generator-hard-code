package com.bcm.h2h.bcmh2hcodegenerator.run;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bcm.h2h.bcmh2hcodegenerator.common.constant.Constants;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.MyVelocityUtils;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.VelocityInitializer;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author yuelong.liang
 */
@Slf4j
public class CodeGenerator {

    /**
     * 全局配置文件路径
     */
    public static String globalConfig = "gen-config/vm.yml" ;

    /**
     * 具体生成列信息配置文件路径
     */
    public static String genConfig = "gen-config/gen.yml" ;

    public static String vmJsonPath = "gen-config/vm.json" ;

    public static String genJsonPath = "gen-config/gen.json" ;
    /**
     * 变量
     */
    public static JSONObject config;

    public static void main(String[] args) throws IOException {
        // 初始化配置
        init();
        // 獲取模板列表
        List<Map.Entry<String, String>> templates = MyVelocityUtils.getTemplateList(config);
        // 初始化 velocity
        VelocityInitializer.initVelocity();
        // 将变量放入 content
        System.out.println(JSONObject.toJSONString(config));
        VelocityContext context = MyVelocityUtils.prepareContext(config);
        // 渲染結果 list
        List<String> result = new ArrayList<>(templates.size());
        for (String template : templates) {
            //渲染模板
            StringWriter writer = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, writer);
            result.add(writer.toString());
            String fileName = MyVelocityUtils.getFileName(template, config);
            // 输出到指定目录
            Path outPath = Paths.get(config.getString("genPath") + "/" + fileName);
            // 判断路径是存在, 不存在则创建
            Path parent = outPath.getParent();
            if (Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            log.info("write to : " + outPath);
            FileCopyUtils.copy(writer.toString(), new PrintWriter(outPath.toFile()));
            log.info("write success");
        }
        log.info("生成完成");
    }

    /**
     * 初始化操作
     */
    public static void init() throws IOException {
        // 读取全局配置, 默认全局配置路径 classpath:/gen-config
        JSONObject global = readVmJson(vmJsonPath);
        // 将类名首字母小写，设置到 global properties 中
        global.put("lowClassName" , StringUtils.uncapitalize(global.getString("className")));
        System.out.println(global);
        // 读取 gen 的配置, 并做预处理
        JSONObject gen = readVmJson(genJsonPath);
        System.out.println(gen);
        // 与 global 合并
        global.putAll(gen);
        config = global;
        System.out.println(config);
    }

    public static JSONObject readVmJson(String vmJsonPath) throws IOException {
        return JSON.parseObject(StreamUtils.copyToString(new ClassPathResource(vmJsonPath).getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 读取 yaml 文件
     *
     * @param path 文件路径
     * @return Properties
     */
    public static JSONObject readYaml(String path) {
        YamlPropertiesFactoryBean bean = new YamlPropertiesFactoryBean();
        bean.setResources(new ClassPathResource(path));
        bean.afterPropertiesSet();
        Properties prop = bean.getObject();
        String json = JSON.toJSONString(YamlUtils.toHump(prop));
        return JSON.parseObject(json);
    }

}
