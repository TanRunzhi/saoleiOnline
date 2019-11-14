package com.gwideal.core.action;

import com.gwideal.base.action.BaseAct;
import com.gwideal.core.entity.SysDepart;
import com.gwideal.core.manager.SysDepartMng;
import com.gwideal.util.io.PropertiesReader;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/sys/depart")
public class SysDepartAct extends BaseAct {

  private final static Logger logger = LogManager.getLogger();

  @RequestMapping("list")
  public ModelAndView list(SysDepart queryBean) {
    logger.debug("depart list fired ...");
    if (StringUtils.isEmpty(queryBean.getOrderBy()))
      queryBean.setOrderBy("seq");//默认按上级部门排序号，排序号排列
    return new ModelAndView("sys/depart/list")
        .addObject("list", sysDepartMng.mergeHQL(queryBean, " and flag = 1"))
        .addObject("queryBean", queryBean);
  }

  @RequestMapping("input")
  public ModelAndView input(SysDepart queryBean) {
    return new ModelAndView("sys/depart/input")
        .addObject("depts", sysDepartMng.mergeHQL(queryBean, " and flag = 1 "))
        .addObject("bean", sysDepartMng.initBean(queryBean));
  }

  @RequestMapping("save")
  public ModelAndView save(SysDepart bean) {
    if (StringUtils.isEmpty(bean.getParent().getId()))
      bean.setParent(null);
    sysDepartMng.saveOrUpdate(bean);
    return list(bean);
  }

  @RequestMapping("ajDel")
  @ResponseBody
  public String ajDel(String id, HttpSession s) {
    sysDepartMng.logicDel(id, null);
    return PropertiesReader.getPropertiesValue("msg.delete.success");
  }

  @RequestMapping("getCacheAjaxRoot")
  @ResponseBody
  public String getCacheAjaxRoot(String scope, String q, HttpSession s) {
    return sysDepartMng.getCacheAjaxRoot(scope, q, getCurrentUser(s));
  }

  @RequestMapping("getCacheAjaxChild")
  @ResponseBody
  public String getCacheAjaxChild(String id) {
    return sysDepartMng.getCacheAjaxChild(id);
  }

  @RequestMapping("ajaxSearch")
  @ResponseBody
  public String ajaxSearch(String keyword) {
    return sysDepartMng.getJSONDept(keyword);
  }

  @RequestMapping("ajGetUsersByDept")
  @ResponseBody
  public String ajGetUsersByDept(String id) {
    return sysDepartMng.getJSONUser(id);
  }

  @Resource
  private SysDepartMng sysDepartMng;
}
