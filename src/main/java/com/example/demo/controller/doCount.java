package com.example.demo.controller;

import com.example.demo.service.FileKeyWordCount;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class doCount {

    @Resource
    FileKeyWordCount fileKeyWordCount;

    //@RequestMapping("/docount")
    public String doCount(){
        fileKeyWordCount.doCount("D:\\txt下载");
        return "统计成功！";
    }
}
