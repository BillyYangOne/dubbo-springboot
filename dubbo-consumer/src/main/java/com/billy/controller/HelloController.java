package com.billy.controller;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.HelloService;

@RestController
public class HelloController {

    @Reference
    private HelloService helloService;

    @RequestMapping("hello")
    public String hello(String name) {

        return helloService.sayHello(name);
    }
}
