package com.gwideal.websocket;

import com.gwideal.core.entity.SysUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Author: fan_jinliang
 * @Date: 2019/2/21 10:05
 */
@Component
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
  private static final Logger logger = LogManager.getLogger();
  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//    logger.debug("Before Handshake");
    ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
    HttpSession session = serverHttpRequest.getServletRequest().getSession(false);
    if(null != session){
      SysUser sysUser = (SysUser) session.getAttribute("currentUser");
      if(null != sysUser){
        attributes.put("webSocketUser",sysUser);
      }
    }
    return super.beforeHandshake(request, response, wsHandler, attributes);
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
//    logger.debug("After Handshake");
    super.afterHandshake(request, response, wsHandler, ex);
  }
}
