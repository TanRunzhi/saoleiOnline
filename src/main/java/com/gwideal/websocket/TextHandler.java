package com.gwideal.websocket;

import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.biz.entity.Person;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextHandler extends  TextWebSocketHandler {

  private static Map<Integer, WebSocketSession> sessionMap = new HashMap<>();

  private static final int MAX_SIZE = 2;

  private static int id = 1;

  /**
   * 连接成功的时候，触发页面上onopen方法
   */
  @Override
  public synchronized void afterConnectionEstablished(WebSocketSession session) throws Exception {
    if(sessionMap.size() < MAX_SIZE){
      sessionMap.put( id , session);
      Map<String,Object> data = new HashMap<>();
      data.put("id",id);
      //
      id ++;
      sendMessage(session,new BaseJsonResult().setData(data).toJSONString());
    }
    System.out.println("open ... " + session.getId());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    for(Map.Entry<Integer, WebSocketSession> entry : sessionMap.entrySet()){
      if( session.getId().equals(entry.getValue().getId()) ){
        sessionMap.remove(entry.getKey());
      }
    }
    System.out.println("close ... " + session.getId());
  }

  /**
   * 接受消息的时候
   * */
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    super.handleTextMessage(session, message);
    String getMsg = message.getPayload();
    System.out.println( session.getId() + " 收到消息：" + message.getPayload());
    WebSocketSession anotherSession = null;
    for(Map.Entry<Integer, WebSocketSession> entry : sessionMap.entrySet()){
      if( !session.getId().equals(entry.getValue().getId()) ){
        anotherSession = entry.getValue();
      }
    }
    if(anotherSession != null){
      sendMessage(anotherSession,getMsg);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    if (session.isOpen()) {
      session.close();
    }
    Person person = getLoginer(session);
    if (person != null) {
//      removeWebSocketSession(session);
    }
  }

  /**
   * 获取当前登录人。这里直接获取用户登录拦截器所存入的用户session
   * @param session session
   * @return 登录人
   */
  private Person getLoginer(WebSocketSession session) {
    for(Map.Entry<Integer, WebSocketSession> entry : sessionMap.entrySet()){
      if(entry.getValue().getId().equals(session.getId())){
        return new Person().setId(entry.getKey()).setSessionId(session.getId());
      }
    }
    return null;
  }

  /**
   * 发送消息
   * @param session session
   * @param message 消息内容
   */
  private boolean sendMessage(WebSocketSession session, String message) throws IOException {
    if (session.isOpen()) {
      session.sendMessage(new TextMessage(message));
      return true;
    } else {
      return false;
    }
  }


}
