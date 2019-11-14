package com.gwideal.core.action;

import com.gwideal.base.action.BaseAct;
import com.gwideal.core.entity.Menu;
import com.gwideal.core.manager.MenuMng;
import com.gwideal.core.manager.ModuleMng;
import com.gwideal.util.common.UtilEmpty;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sys/menu")
public class MenuAct extends BaseAct {

  private final static Logger logger = LogManager.getLogger();

  @RequestMapping("list")
  public ModelAndView list(Menu queryBean) {
    logger.debug("menu list fired...");
    if (StringUtils.isEmpty(queryBean.getOrderBy()))
      queryBean.setOrderBy("module.seq,seq");//默认按排序号排列
    return new ModelAndView("sys/menu/list")
        .addObject("queryBean", queryBean)
        .addObject("list", menuMng.mergeHQL(queryBean, " and flag = 1"));
  }

  @RequestMapping("input")
  public ModelAndView input(Menu queryBean) {
    return new ModelAndView("sys/menu/input")
        .addObject("bean", menuMng.initBean(queryBean))
        .addObject("menuList", menuMng.getCate())
        .addObject("moduleList", moduleMng.getCacheList());
  }

  @RequestMapping("save")
  public ModelAndView save(Menu bean) {
    if (StringUtils.isEmpty(bean.getParent().getId()))
      bean.setParent(null);
    if (StringUtils.isEmpty(bean.getModule().getId()))
      bean.setModule(null);
    menuMng.saveOrUpdate(bean);
    return list(bean);
  }

  @RequestMapping("ajDel")
  @ResponseBody
  public String ajDel(String id) {
    menuMng.logicalDel(id);
    return PropertiesReader.getPropertiesValue("msg.delete.success");
  }

  @RequestMapping("{code}")
  public String menu(HttpSession s, @PathVariable("code") String code) {
    List<Menu> list = menuMng.getCacheSubMenu(code);
    if (UtilEmpty.isArrayEmpty(list)) {
      list = new ArrayList<>();
      list.add(menuMng.get(code));
    }
    s.setAttribute("menuCode", code);
    s.setAttribute("subMenu", list);
    return "redirect:" + list.get(0).getUrl();
  }

  @RequestMapping("subMenu_{code}")
  public String subMenu(HttpSession s, @PathVariable("code") String code) {
    List<Menu> list = menuMng.getCacheSubMenu(code);
    s.setAttribute("firstMenu", menuMng.get(code));
    if (!UtilEmpty.isArrayEmpty(list)) {
      s.setAttribute("subMenu", list);
    }
    s.setAttribute("crtMenu", list.get(0));
    return "redirect:" + list.get(0).getUrl();
  }

  @RequestMapping("ajInitMenuAcl")
  @ResponseBody
  public String ajInitMenuAcl(HttpSession s) {
    return menuMng.initMenuAcl();
  }

  @RequestMapping("checkDuplicated")
  @ResponseBody
  public String checkDuplicated(String key, String value) {
    if (menuMng.checkDuplicated(null, key, value,null)) {
      return "duplicated";
    } else
      return "ok";
  }

  /**
   * 根据模块ID返回该模块下所有一级栏目
   *
   * @param moduleId 模块Id
   * @return 一级栏目json数据
   */
  @RequestMapping("getMenuByModule")
  @ResponseBody
  public String getMenuByModule(String moduleId, String beanId) {
    return menuMng.getMenuByModuleId(moduleId, beanId);
  }

  @Resource
  private MenuMng menuMng;

  @Resource
  private ModuleMng moduleMng;
}
