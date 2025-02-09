package com.ajie.enterprisewechat.controller;

import com.ajie.enterprisewechat.bean.Demo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiangxingjie
 * @date 2025/2/9 23:29
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/getTest")
    public void getTest(String name){
        Demo demo = new Demo();
        demo.setName(name);
        demo.save();
    }
}
