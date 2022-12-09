package com.hy.websocketdemo.service;
import java.util.Date;


import com.alibaba.fastjson.JSON;
import com.hy.websocketdemo.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint(value = "/webSocketOneToOne/{sid}")
@Slf4j
public class WebSocketOneToOne {
    // 在线连接数
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    // 存放所有在线的客户端
    private static Map<String, Session> clients = new HashMap<>();

    @OnOpen
    public void onOpen(Session session,@PathParam(value = "sid") String sid){
        //  在线连接数 +1
        onlineCount.incrementAndGet();
        //  在线 客户端
        clients.put(sid,session);
        log.info(sid+" 加入了WebSocket！当前在线人数为 : "+onlineCount.get());

//        // 广播上线消息
        Message message = new Message();
        message.setTo("0");
        message.setText(sid);
        message.setDate(new Date());
        this.broadcast(JSON.toJSONString(message));
    }

    @OnClose
    public void onClose(@PathParam(value = "sid") String sid){
        // 在线连接数  -1
        onlineCount.decrementAndGet();
        clients.remove(sid);
        log.info(sid+" WebSocket连接关闭，当前在线人数为：{}",onlineCount.get());

//        // 广播下线消息
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setTo("-2");
        msg.setText(sid);
        broadcast(JSON.toJSONString(msg,true));
    }

    @OnError
    public void onError(Session session,Throwable error){
        error.printStackTrace();
        log.info("发生错误！");
    }

    @OnMessage
    public void onSend(String message){
        Message msg = JSON.parseObject(message, Message.class);
        log.info("---> to: "+msg.getTo());
        if (msg.getTo().equals("-1")){
            this.broadcast(JSON.toJSONString(msg));
        }else {
            sendInfo(msg.getTo(), JSON.toJSONString(msg));
        }

    }

    //给指定用户发送信息
    public void sendInfo(String sid, String message){
        Session session = clients.get(sid);
        log.info("消息内容： "+message);
        try {
            sendMessage(message,session);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

   // 群发消息
    public void broadcast(String message){
        log.info("群发消息内容： "+message);
        for (Session session : clients.values()) {
            try {
                sendMessage(message,session);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }


    /**
     * 发送消息
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message,Session session) throws IOException {
        if (session != null){
            synchronized (session){
                session.getBasicRemote().sendText(message);
            }
        }
    }


}
