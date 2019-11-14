package com.gwideal.core.action;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.action.BaseAct;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.core.config.Constants;
import com.gwideal.core.entity.Module;
import com.gwideal.core.entity.SysUser;
import com.gwideal.core.manager.MenuMng;
import com.gwideal.core.manager.ModuleMng;
import com.gwideal.core.manager.SysConfigMng;
import com.gwideal.core.manager.SysUserMng;
import com.gwideal.security.RSASecurityUtil;
import com.gwideal.util.codeHelper.CustomerCoder;
import com.gwideal.util.io.PropertiesReader;
import net.sf.ehcache.CacheManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Created by li_hongyu on 14-7-22.
 */
@Controller
@RequestMapping("/")
public class IndexAct extends BaseAct {
  private final static Logger logger = LogManager.getLogger();

  @RequestMapping("welcome")
  public ModelAndView welcome(HttpSession s, HttpServletRequest request) {
    request.removeAttribute("error");
    SysUser currentUser = getCurrentUser(s);
    if (currentUser == null) {
      Map<String, Object> keyMap = RSASecurityUtil.generateKeyPair();
      s.setAttribute("keyMap", keyMap);
      return new ModelAndView("login")
          .addObject("publicKey", RSASecurityUtil.getPublicKey(keyMap));
    } else {
      if (sysConfigMng.getCacheValue(Constants.SYS_WHITE_LIST).contains(currentUser.getAccount())) {
        return sys(s);
      }else {
        s.setAttribute("crtMenu", null);
        s.setAttribute("menuTree", menuMng.getCacheEnableMenuByModuleCode(PropertiesReader.getPropertiesValue("app.code")));
        if (getUserAcls(s).contains(Constants.CN_CORP_ACLKEY)){
          // 公司登陆直接进入案卷著录
          return new ModelAndView("redirect:/common/guide/OWI4NzRhYzIzNDQwNDA0MDI4YzQ4MTRkZTA0ZWRiMDE0ZGUwNTFlMmFkMDAwMjQ2Yjg0NWUxYWM2MjQw.htm");
        }
        return new ModelAndView("archives/index");
      }
    }
  }


  @RequestMapping("login")
  @ResponseBody
  public String login(String account, String pwd, String keyInput, String phone, HttpSession s, HttpServletRequest request) {
    if (!validCaptcha(keyInput, phone, s)) {
      return JSON.toJSONString(new BaseJsonResult(false, PropertiesReader.getPropertiesValue("sys.account.invalidCode")));
    }
    account = RSASecurityUtil.decrypt(getKeyMap(s), account);
    SysUser su = sysUserMng.authLogin(account, RSASecurityUtil.decrypt(getKeyMap(s), pwd));
    if (su == null) {
      logger.info("{} user: [{}] authLogin failed from ip:[{}]",
          sysConfigMng.getCacheValue(Constants.SYS_WHITE_LIST).contains(account) ? "admin" : "common",
          account, request.getRemoteAddr());
      return JSON.toJSONString(new BaseJsonResult(false, sysUserMng.userLoginFailed(account)));
    } else {
      logger.info("user:[{}] login by u&p from ip:[{}]", account, request.getRemoteAddr());
      List<String> userAcls = sysUserMng.getAclKeyByUserId(su.getId());
      s.setAttribute("currentUser", su);
      s.setAttribute("userAcls", userAcls);
      s.setAttribute("aclSet", sysUserMng.getAclUrlByUserId(su.getId()));
      // 是否是集团本部下的用户
      s.setAttribute("isAdminDepart", userAcls.contains(Constants.CN_DEPART_ACLKEY));
      // 是否是集团管理员
      s.setAttribute("isAdmin", userAcls.contains(Constants.CN_DEPARTADMIN_ACLKEY));
      actionLogMng.saveLog(null, "", su.getAccount(), "login", "1", "", su.getId(), null, null);
      if (sysConfigMng.getCacheValue(Constants.SYS_WHITE_LIST).contains(su.getAccount())) {
        //系统管理白名单用户，直接登录至系统管理
        return JSON.toJSONString(new BaseJsonResult(true, "sys"));
      } else {
        return JSON.toJSONString(new BaseJsonResult(true, "ok"));
      }
    }
  }

  @RequestMapping("logout")
  @ResponseBody
  public String logout(HttpSession s) {
    actionLogMng.saveLog(null, "", getCurrentUser(s).getAccount(), "loginOut", "1", "", getCurrentUser(s).getId(), null, null);
    s.setAttribute("currentUser", null);
    s.setAttribute("userAcls", null);
    s.setAttribute("aclSet",null);
    s.setAttribute("menuTree", null);
    s.setAttribute("aclSet", null);
    s.invalidate();
    return "ok";
  }

  @RequestMapping("notAdmin")
  public ModelAndView notAdmin() {
    return new ModelAndView("notAdmin");
  }

  @RequestMapping("sys")
  public ModelAndView sys(HttpSession s) {
    s.setAttribute("crtModule", moduleMng.findBy("code", "sys", "").get(0));
    s.setAttribute("crtMenu", menuMng.findBy("code", "sys", "").get(0));
    s.setAttribute("sysTree", menuMng.findBy("parent.code", "sys", "seq"));
    Runtime runtime = Runtime.getRuntime();
    return new ModelAndView("/sys/index")
        .addObject("os_arch", System.getProperty("os.arch"))
        .addObject("os_name", System.getProperty("os.name"))
        .addObject("os_version", System.getProperty("os.version"))
        .addObject("user_name", System.getProperty("user.name"))
        .addObject("user_dir", System.getProperty("user.dir"))
        .addObject("java_io_tmpdir", System.getProperty("java.io.tmpdir"))
        .addObject("java_runtime_name", System.getProperty("java.runtime.name"))
        .addObject("java_runtime_version", System.getProperty("java.runtime.version"))
        .addObject("java_vm_name", System.getProperty("java.vm.name"))
        .addObject("maxMemory", runtime.maxMemory())
        .addObject("usedMemory", runtime.maxMemory() - runtime.freeMemory())
        .addObject("freeMemory", runtime.freeMemory())
        .addObject("activeThreadCount", Thread.activeCount());
  }

  @RequestMapping("gate")
  public ModelAndView gate() {
    List<Module> modules = moduleMng.getCacheList();
    return new ModelAndView("gate")
        .addObject("modules", modules);
  }

  /**
   * 清除所有ehcache缓存
   *
   * @return 已清除文字信息
   */
  @RequestMapping("clrCache")
  @ResponseBody
  public String clearCache() {
    cacheManager.clearAll();
    return PropertiesReader.getPropertiesValue("msg.cache.clearDone");
  }

  @Resource
  private SysUserMng sysUserMng;

  @Resource
  private SysConfigMng sysConfigMng;


  @Resource
  private MenuMng menuMng;

  @Resource
  private ModuleMng moduleMng;

  @Resource
  private CacheManager cacheManager;


}
