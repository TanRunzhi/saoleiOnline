package com.gwideal.core.action;

import com.gwideal.base.action.BaseAct;
import com.gwideal.core.entity.Lookups;
import com.gwideal.core.manager.LookupsMng;
import com.gwideal.core.manager.ModuleMng;
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
 * Created by li_hongyu on 14-7-10.
 */
@Controller
@RequestMapping ("/sys/lookups")
public class LookupsAct extends BaseAct {

  private static final Logger logger = LogManager.getLogger();

  @RequestMapping ("/list")
  public ModelAndView list(Lookups queryBean) {
    logger.debug("lookups list fired");
    if (StringUtils.isEmpty(queryBean.getOrderBy())) {
      queryBean.setOrderBy("module.id desc");//默认按字典类别，字典项编码排序
    }
    return new ModelAndView("sys/lookups/list")
        .addObject("queryBean", queryBean)
        .addObject("list", lookupsMng.mergeHQL(queryBean, " and 1 = 1 "));
  }

  @RequestMapping ("input")
  public ModelAndView input(Lookups queryBean) {
    return new ModelAndView("sys/lookups/input")
        .addObject("bean", lookupsMng.initBean(queryBean))
        .addObject("cateList", lookupsMng.getCategory())
        .addObject("moduleList", moduleMng.getCacheList());
  }

  @RequestMapping ("save")
  public ModelAndView save(Lookups bean) {
    if ( StringUtils.isEmpty(bean.getParent().getId()))
      bean.setParent(null);
    if ( StringUtils.isEmpty(bean.getModule().getId()))
      bean.setModule(null);
    lookupsMng.saveOrUpdate(bean);
    return list(bean);
  }

  @RequestMapping ("ajDel")
  @ResponseBody
  public String ajDel(String id) {
    if (lookupsMng.load(id).getChildren().size() > 0) {
      return PropertiesReader.getPropertiesValue("msg.delete.hasChild");
    } else {
      lookupsMng.del(id);
      return PropertiesReader.getPropertiesValue("msg.delete.success");
    }
  }

  @RequestMapping ("ajGetLookups")
  @ResponseBody
  public String ajGetLookups(String cate) {
    logger.debug("ajGetLookups fired for " + cate);
    return lookupsMng.getJSONLookups(cate);
  }


  @RequestMapping ("ajGetCateName")
  @ResponseBody
  public String ajGetCateName(String cate) {
    logger.debug("ajGetCateName fired for " + cate);
    return lookupsMng.findBy("cCode", cate, "").get(0).getcName();
  }

  @RequestMapping ("getLookupsByModule")
  @ResponseBody
  public String getLookupsByModule(String moduleId, String beanId) {
    return lookupsMng.getCateByModuleId(moduleId, beanId);
  }

  @Resource
  private LookupsMng lookupsMng;

  @Resource
  private ModuleMng moduleMng;
}
