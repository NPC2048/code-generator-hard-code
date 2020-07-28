package com.bcm.h2h.bcmh2hcodegenerator.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bcm.h2h.bcmh2hcodegenerator.common.constant.Constants;
import com.bcm.h2h.bcmh2hcodegenerator.common.enmus.FormType;
import com.bcm.h2h.bcmh2hcodegenerator.common.enmus.JavaType;
import com.bcm.h2h.bcmh2hcodegenerator.common.enmus.MsSqlType;
import com.bcm.h2h.bcmh2hcodegenerator.common.enmus.QueryType;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.MyVelocityUtils;
import com.bcm.h2h.bcmh2hcodegenerator.common.utils.VelocityInitializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yuelong.liang
 */
@Slf4j
@Controller
public class MainController {

    @RequestMapping("/")
    public String root(ModelMap modelMap) {
        // 添加作者
        String username = System.getenv().get("USERNAME");
        modelMap.put("username" , username);

        // 添加枚举
        modelMap.put("javaTypes" , JavaType.values());
        modelMap.put("sqlTypes" , MsSqlType.values());
        modelMap.put("queryTypes" , QueryType.values());
        modelMap.put("formTypes" , FormType.values());

        return "index" ;
    }

    @RequestMapping("/index")
    public String index(ModelMap modelMap) {
        return root(modelMap);
    }

    @RequestMapping("/download")
    @ResponseBody
    public void download(@RequestBody String json, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteStream);
        log.info("request body: " + json);
        JSONObject config = JSON.parseObject(json);
        // 初始化 velocity
        List<String> templates = MyVelocityUtils.getTemplateList(config);
        VelocityInitializer.initVelocity();
        VelocityContext context = MyVelocityUtils.prepareContext(config);

        // 渲染变量到模板
        for (String template : templates) {
            StringWriter writer = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, writer);
            // 添加到 zip
            String str = MyVelocityUtils.getFileName(template, config);
            System.out.println(str);
            zipOutputStream.putNextEntry(new ZipEntry(MyVelocityUtils.getFileName(template, config)));
            StreamUtils.copy(writer.toString(), StandardCharsets.UTF_8, zipOutputStream);
//            IOUtils.closeQuietly(writer);
            zipOutputStream.flush();
//            zipOutputStream.close();
        }

        byte[] data = byteStream.toByteArray();
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"ruoyi.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        StreamUtils.copy(data, response.getOutputStream());
    }

}
