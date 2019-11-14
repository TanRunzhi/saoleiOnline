package com.gwideal.core.action;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.action.BaseAct;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.core.entity.SysUser;
import com.gwideal.core.util.DBHandler;
import com.gwideal.util.io.PropertiesReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by li_hongyu on 2014/9/19.
 */
@Controller
@RequestMapping("/sys/init")
public class InitAct extends BaseAct {

  private final static Logger logger = LogManager.getLogger();

  @RequestMapping("ajDataClear")
  @ResponseBody
  public String ajDataClear(String tableNames) {
    return PropertiesReader.getValueWithPH("msg.sys.dataClearDone", dbHandler.cleanData(tableNames) + "");
  }

  @RequestMapping("list")
  public ModelAndView list() {
    return new ModelAndView("/sys/init/list");
  }


  @Resource
  private DBHandler dbHandler;
}
