package com.hy.websocketdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * websocket 服务端口
 * author  zhoushengwen
 * date  2022/12/9
 **/
@ServerEndpoint(value = "/websocket/service/")
@Component
public class WebSocketService {

    // 所有连接的对象（在线的用户连接）
    private static final CopyOnWriteArraySet<WebSocketService> webSocketServers = new CopyOnWriteArraySet<>();

    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);


    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private Session getSession() {
        return session;
    }

    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketServers.add(this);
        log.info("有新连接加入！当前在线人数为 : " + getOnlineCount());
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onclose() {
        webSocketServers.remove(this);
        log.info("某个连接关闭,当前在线人数为 : " + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info(" 来自客户端或者服务端推送的消息 --->: " + message);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.info("发生错误!");
        error.printStackTrace();
    }

    /**
     * 消息推送，广播模式，推给所有在线用户
     *
     * @param message
     */
    public void sendMessage(String message) throws IOException {
        for (WebSocketService webSocketServer : webSocketServers) {
            Session s = webSocketServer.getSession();
            s.getBasicRemote().sendText(message);
        }
    }


    /**
     * 获取连接数
     * author  zhoushengwen
     * date  2022/12/9
     **/
    public static synchronized int getOnlineCount() {
        return webSocketServers.size();
    }


}
