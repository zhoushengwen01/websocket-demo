package com.hy.websocketdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RouterController {


    //自己给自己发消息
    @GetMapping({"/oneself"})
    public String you() {
        return "toOneself";
    }

    //自己给所有客户端发送消息
    @GetMapping({"/index", "/", "/all"})
    public String sendAllClient() {
        return "toAll";
    }

    //自己给别的客户端发送消息
    @GetMapping({"/one"})
    public String sendOtherClient() {
        return "toOne";
    }


    /**
     * 客户端页面
     * author  zhoushengwen
     * date  2022/12/9
     **/
    @GetMapping({"/client"})
    public String client() {
        return "client";
    }
}
