package com.gwideal.core.action;

import com.gwideal.base.action.BaseAct;
import com.gwideal.websocket.TextHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by li_hongyu on 14-7-22.
 */
@Controller
@RequestMapping("/")
public class IndexAct extends BaseAct {
  private final static Logger logger = LogManager.getLogger();

  @RequestMapping("welcome")
  public ModelAndView welcome(String id,HttpSession s, HttpServletRequest request) {

    return new ModelAndView("index");
  }


  @RequestMapping("sendMsg")
  @ResponseBody
  public boolean sendMsg(String account, String pwd, String keyInput, String phone, HttpSession s, HttpServletRequest request) {
    return true;
//    return webScoketHandler.sendMessageTo("1", "你好socket 1");
  }

  @RequestMapping("getMsg")
  @ResponseBody
  public boolean getMsg(String account, String pwd, String keyInput, String phone, HttpSession s, HttpServletRequest request) {
    return true;
//    return webScoketHandler.sendMessageToUser("2", "你好socket 2");
  }

  @Resource
  private TextHandler webScoketHandler;
}
