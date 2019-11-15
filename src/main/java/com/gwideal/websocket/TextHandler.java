package com.gwideal.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.biz.entity.Cell;
import com.gwideal.biz.entity.Person;
import com.gwideal.biz.entity.Table;
import com.gwideal.biz.manager.TableMng;
import com.gwideal.core.config.Constants;
import com.gwideal.util.common.UtilEmpty;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextHandler extends  TextWebSocketHandler {

  private static Map<Integer, WebSocketSession> sessionMap = new HashMap<>();

  private static final int MAX_SIZE = 2;

  private static int id = 1;

  private static Person p1 = new Person() ;

  private static Person p2 = new Person();

  private static Table table = new Table() ;

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
      if(StringUtils.isEmpty(p1.getId())){
        p1.setId(id);
        p1.setSessionId(session.getId());
        data.put("player",1);
      }else if(StringUtils.isEmpty(p2.getId())){
        p2.setId(id);
        p2.setSessionId(session.getId());
        data.put("player",2);
      }
      //
      id ++;
      sendMessage(session,new BaseJsonResult(true,Constants.USER_INFO).setData(data).toJSONString());
    }
    System.out.println("open ... " + session.getId());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    for(Map.Entry<Integer, WebSocketSession> entry : sessionMap.entrySet()){
      if( session.getId().equals(entry.getValue().getId()) ){
        sessionMap.remove(entry.getKey());
      }
      if(session.getId().equals(p1.getSessionId())){
        p1 = new Person();
      }
      if(session.getId().equals(p2.getSessionId())){
        p2 = new Person();
      }
    }
    if(UtilEmpty.isArrayEmpty(sessionMap)){
      table = new Table();
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
    System.out.println( session.getId() + " 发送消息：" + getMsg);
    //
    JSONObject map = JSON.parseObject(getMsg);
    String returnMsg = JSON.toJSONString(dealWithMsg(map));
    //
    if(Constants.MSG_SEND_ALL.equals(map.get("sendTo")) || Constants.MSG_SEND_MYSELF.equals(map.get("sendTo")) ){
      System.out.println( session.getId() + " 接受消息：" + returnMsg);
      sendMessage(session,returnMsg);
    }
    if(Constants.MSG_SEND_ALL.equals(map.get("sendTo")) || Constants.MSG_SEND_ANOTHER.equals(map.get("sendTo")) ){
      WebSocketSession anotherSession = null;
      for(Map.Entry<Integer, WebSocketSession> entry : sessionMap.entrySet()){
        if( !session.getId().equals(entry.getValue().getId()) ){
          anotherSession = entry.getValue();
        }
      }
      if(anotherSession != null){
        System.out.println( anotherSession.getId() + " 接受消息：" + returnMsg);
        sendMessage(anotherSession,returnMsg);
      }
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

  private Map dealWithMsg(JSONObject map){
    JSONObject data = (JSONObject)map.get("data");
    if(Constants.TABLE_INIT.equals(map.get("msg"))){
      if(table.getCells() == null){
        TableMng.resetTable(table,(Integer)data.get("row"),(Integer)data.get("col"),(Integer)data.get("boom"));
      }
      return new BaseJsonResult(true,Constants.TABLE_PRINT).setData(table.getCells());
    }else if(Constants.TD_CLICK.equals(map.get("msg"))){
      List<Cell> cell = TableMng.changeTable(table,(Integer)data.get("row"),(Integer)data.get("col"),(Integer)data.get("player"),(Integer)data.get("button"));
      return new BaseJsonResult(true,Constants.TD_CHANGE).setData(cell);
    }
    return null;
  }

}
