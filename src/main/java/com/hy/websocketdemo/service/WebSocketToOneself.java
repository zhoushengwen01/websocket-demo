package com.hy.websocketdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@ServerEndpoint(value = "/webSocketToOneself")
@Component
public class WebSocketToOneself {

    // 用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onLineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的  WebSocketServer 对象。
    // 若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocketToOneself> webSocketServers = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketServers.add(this);
        addOnlineCount();
        log.info("有新连接加入！当前在线人数为 : "+getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onclose(){
        webSocketServers.remove(this);
        subOnlineCount();
        log.info("某个连接关闭,当前在线人数为 : "+getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message   客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message){
        log.info(" 来自客户端或者服务端推送的消息 --->: "+message);
        // 自己给自己推送消息
        try {
            this.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session,Throwable error){
        log.info("发生错误!");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     * @param message
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onLineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketToOneself.onLineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketToOneself.onLineCount--;
    }
}
