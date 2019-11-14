package com.gwideal.core.action;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.action.BaseAct;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.core.entity.Menu;
import com.gwideal.core.entity.Module;
import com.gwideal.core.entity.OPTYPE;
import com.gwideal.core.entity.SysUser;
import com.gwideal.core.manager.MenuMng;
import com.gwideal.core.manager.SysUserMng;
import com.gwideal.security.RSASecurityUtil;
import com.gwideal.util.codeHelper.CustomerCoder;
import com.gwideal.util.io.PropertiesReader;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by farno on 2016/5/18.
 */
@Controller
@RequestMapping("common")
public class CommonAct extends BaseAct {

  @RequestMapping("guide/{id}")
  public ModelAndView guide(@PathVariable("id") String menuId, HttpSession s) {
    Menu menu = menuMng.load(CustomerCoder.getFromBASE64(menuId));
    if(StringUtils.isEmpty(menu.getUrl()) && !menu.getChildren().isEmpty()){
      menu = menu.getChildren().iterator().next();
    }
    s.setAttribute("crtMenu", menu);
    s.setAttribute("crtMenuKey", menu.getAclKey());
    if (StringUtils.isNotEmpty(menu.getUrl())) {
      return new ModelAndView("redirect:" + menu.getUrl());
    } else {
      return null;
    }
  }

  @RequestMapping("getLoginKey")
  @ResponseBody
  public String getLoginKey(HttpSession s) {
    Map<String, Object> map = new BaseJsonResult();
    Map<String, Object> keyMap = RSASecurityUtil.generateKeyPair();
    String key = RSASecurityUtil.getPublicKey(keyMap);
    map.put("pubKey", key);
    s.setAttribute("keyMap", keyMap);
    return JSON.toJSONString(map);
  }


  @RequestMapping("pwd")
  public ModelAndView pwd(HttpSession s) {
    return new ModelAndView("/user/pwd");
  }

  @RequestMapping("user/reset")
  @ResponseBody
  public String setPwd(String pwd, HttpSession s) {
    return sysUserMng.ajSetPwd(getCurrentUser(s), pwd);
  }

  @RequestMapping("ajCancel")
  @ResponseBody
  public String ajCancel(String entity, String className, String id,String act, HttpSession s) {
    SysUser currentUser = getCurrentUser(s);
    sysUserMng.ajaxUpdate( id, "currentState", "02");
    return JSON.toJSONString(new BaseJsonResult(true, PropertiesReader.getPropertiesValue("sys.title.ok")));
  }


  @Resource
  private MenuMng menuMng;
  @Resource
  private SysUserMng sysUserMng;
}
