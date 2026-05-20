package com.antigravity.diabetes.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/doctor/{doctorId}")
@Component
@Slf4j
public class DoctorWebSocketServer {

    private static final ConcurrentHashMap<Long, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("doctorId") Long doctorId) {
        sessionMap.put(doctorId, session);
        log.info("医生[{}]已连接WebSocket, 当前在线医生数: {}", doctorId, sessionMap.size());
    }

    @OnClose
    public void onClose(@PathParam("doctorId") Long doctorId) {
        sessionMap.remove(doctorId);
        log.info("医生[{}]已断开WebSocket, 当前在线医生数: {}", doctorId, sessionMap.size());
    }

    @OnError
    public void onError(@PathParam("doctorId") Long doctorId, Throwable error) {
        Session session = sessionMap.get(doctorId);
        if (error != null) {
            log.warn("医生[{}]WebSocket异常 ({}): {}", doctorId,
                error.getClass().getSimpleName(),
                error.getMessage() != null ? error.getMessage() : "连接意外关闭/超时");
        }
        if (session == null || !session.isOpen()) {
            sessionMap.remove(doctorId);
        }
    }

    /**
     * 推送预警消息给指定医生
     */
    public static void sendAlertMessage(Long doctorId, String message) {
        log.info("【sendAlertMessage】sessionMap大小: {}, 目标医生ID: {}", sessionMap.size(), doctorId);
        
        Session session = sessionMap.get(doctorId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("成功推送强提醒至医生[{}]: {}", doctorId, message);
            } catch (Exception e) {
                log.error("推送消息给医生[{}]失败: {}", doctorId, e.getMessage(), e);
            }
        } else {
            log.warn("医生[{}]不在线或会话已关闭(session={}, isOpen={}), 未推送预警消息",
                    doctorId,
                    session != null ? "exists" : "null",
                    session != null ? session.isOpen() : "N/A");
        }
    }
}
