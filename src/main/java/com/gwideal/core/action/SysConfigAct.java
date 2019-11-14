package com.gwideal.core.action;

import com.gwideal.base.action.BaseAct;
import com.gwideal.core.entity.SysConfig;
import com.gwideal.core.manager.SysConfigMng;
import com.gwideal.util.io.PropertiesReader;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by li_hongyu on 2015/06/01.
 */
@Controller
@RequestMapping ("/sys/config")
public class SysConfigAct extends BaseAct {

  private final static Logger logger = LogManager.getLogger();

  @RequestMapping ("list")
  public ModelAndView list(SysConfig queryBean) {
    logger.debug("sysConfig list fired");
    if ( StringUtils.isEmpty(queryBean.getOrderBy()))
      queryBean.setOrderBy("cfgKey");//默认按配置键排列
    return new ModelAndView("/sys/config/list")
        .addObject("queryBean", queryBean)
        .addObject("list", sysConfigMng.mergeHQL(queryBean, ""));
  }

  @RequestMapping ("input")
  public ModelAndView input(SysConfig queryBean) {
    return new ModelAndView("sys/config/input")
        .addObject("bean", sysConfigMng.initBean(queryBean));
  }

  @RequestMapping ("save")
  public ModelAndView save(SysConfig bean) {
    sysConfigMng.setValue(bean.getCfgKey(), bean.getCfgValue(), bean.getRemark());
    return list(bean);
  }

  @RequestMapping ("ajDel")
  @ResponseBody
  public String ajDel(String id) {
    return PropertiesReader.getPropertiesValue("msg.delete.success");
  }

  @Resource
  private SysConfigMng sysConfigMng;
}
