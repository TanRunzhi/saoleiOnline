package com.gwideal.core.action;

import com.gwideal.base.action.BaseAct;
import com.gwideal.core.entity.Module;
import com.gwideal.core.manager.MenuMng;
import com.gwideal.core.manager.ModuleMng;
import com.gwideal.core.manager.SysAclMng;
import com.gwideal.util.io.PropertiesReader;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping ("/sys/module")
public class ModuleAct extends BaseAct {

  private final static Logger logger = LogManager.getLogger();

  @RequestMapping ("list")
  public ModelAndView list(Module queryBean) {
    logger.debug("module list fired...");
    if ( StringUtils.isEmpty(queryBean.getOrderBy()))
      queryBean.setOrderBy("seq");//默认按排序号排列
    return new ModelAndView("sys/module/list")
        .addObject("queryBean", queryBean)
        .addObject("list", moduleMng.mergeHQL(queryBean, ""));
  }

  @RequestMapping ("input")
  public ModelAndView input(Module queryBean) {
    return new ModelAndView("sys/module/input")
        .addObject("bean", moduleMng.initBean(queryBean));
  }

  @RequestMapping ("save")
  public ModelAndView save(Module bean) {
    if ( StringUtils.isEmpty(bean.getId()))
      bean.setRegDate(new Date());
    moduleMng.saveOrUpdate(bean);
    return list(bean);
  }

  @RequestMapping ("ajDel")
  @ResponseBody
  public String ajDel(String id) {
    moduleMng.del(id);
    return PropertiesReader.getPropertiesValue("msg.delete.success");
  }

  @RequestMapping ("checkDuplicated")
  @ResponseBody
  public String checkDuplicated(String id,String key, String value) {
    if (moduleMng.checkDuplicated(id, key, value,null)) {
      return "duplicated";
    } else
      return "ok";
  }


  @RequestMapping ("active")
  @ResponseBody
  public String active(String id, boolean activated) {
    moduleMng.activate(id, activated);
    return activated ? PropertiesReader.getPropertiesValue("sys.title.moduleActive")
        : PropertiesReader.getPropertiesValue("sys.title.moduleDeactivate");
  }

  /**
   * 根据模块code,跳转到当前模块的第一个菜单，如果人员是Uim登陆，则加载人员在当前模块的权限值
   *
   * @param code
   * @param s
   * @return
   */
  @RequestMapping ("{code}")
  public ModelAndView jump(@PathVariable String code, HttpSession s) {
    List<Module> mList = moduleMng.findBy("code", code, "");
    if (mList.size() > 0) {
      Module m = mList.get(0);
      s.setAttribute("crtModule", m);
      switch (m.getCode()) {
        case "sys":
          return new ModelAndView("redirect:/sys.htm");
        case "cms":
          return new ModelAndView("redirect:/cms.htm");
        default:
          return new ModelAndView("redirect:/index.htm");
      }
    } else {
      return new ModelAndView("404");
    }
  }

  @Resource
  private ModuleMng moduleMng;

  @Resource
  private MenuMng menuMng;

  @Resource
  private SysAclMng sysAclMng;
}
