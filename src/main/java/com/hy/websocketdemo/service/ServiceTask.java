package com.hy.websocketdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@EnableScheduling
public class ServiceTask {
    @Autowired
    private WebSocketService webSocketServer;

    /**
     * 服务端  向客户端  推送 消息
     */
    @Scheduled(initialDelay = 10000, fixedDelay = 1000 * 10)
    public void send() throws IOException {
        int index = (int) (Math.random() * 5 + 1);
        List<String> language = Arrays.asList("aaaaa", "bbb", "cccc", "dddd", "eeee", "ffff");
        webSocketServer.sendMessage("Hello, " + language.get(index) + " !!!");
    }

}
