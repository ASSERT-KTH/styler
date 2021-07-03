package com.hlj.springboot.one.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeControlelr {


    @GetMapping("hello")
    public String success(){
        return  "maven son springboot success";
    }

}