package com.bcm.h2h.bcmh2hcodegenerator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bcm.h2h.bcmh2hcodegenerator.common.constant.Constants;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.MyVelocityUtils;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.VelocityInitializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuelong.liang
 */
@Slf4j
public class CodeGenerator {

    public static void main(String[] args) throws IOException {
        // 加载生成配置
        JSONObject vmJson = readVmJson("./gen-config/vm.json");
        // 獲取模板列表
        List<String> templates = MyVelocityUtils.getTemplateList();
        // 初始化 velocity
        VelocityInitializer.initVelocity();
        // 将变量放入 content
        VelocityContext context = MyVelocityUtils.prepareContext(vmJson);

        // 渲染結果 list
        List<String> result = new ArrayList<>(templates.size());
        for (String template : templates) {
            //渲染模板
            StringWriter writer = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, writer);
            result.add(writer.toString());
            log.info(writer.toString());
        }
        log.info("生成完成");
    }

    public static JSONObject readVmJson(String vmJsonPath) throws IOException {
        return JSON.parseObject(StreamUtils.copyToString(new ClassPathResource(vmJsonPath).getInputStream(), StandardCharsets.UTF_8));
    }

}
