package com.bcm.h2h.bcmh2hcodegenerator.controller;

import com.alibaba.fastjson.JSON;
import com.bcm.h2h.bcmh2hcodegenerator.common.enmus.JavaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yuelong.liang
 */
@Controller
public class MainController {

    @RequestMapping("/")
    public String root(ModelMap modelMap) {
        // 添加作者
        String username = System.getenv().get("USERNAME");
        modelMap.put("username", username);

        // 添加枚举
        modelMap.put("javaTypes", JavaType.values());

        return "index";
    }

    @RequestMapping("/index")
    public String index(ModelMap modelMap) {
        return root(modelMap);
    }


}
