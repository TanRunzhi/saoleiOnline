package com.gwideal.core.action;

import com.gwideal.base.action.BaseAct;
import com.gwideal.core.entity.ActionLog;
import com.gwideal.core.entity.OPTYPE;
import com.gwideal.core.manager.ActionLogMng;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.EnumSet;

@Controller
@RequestMapping ("/sys/log")
public class ActionLogAct extends BaseAct {

	private final static Logger logger = LogManager.getLogger();

	@RequestMapping ("list")
	public ModelAndView list(ActionLog queryBean) {
		logger.debug("log list fired");
		if ( StringUtils.isEmpty(queryBean.getOrderBy())) {
			queryBean.setOrderBy("operatorDate desc");
		}
		return new ModelAndView("sys/log/list").addObject("queryBean", queryBean)
				.addObject("list", actionLogMng.mergeHQL(queryBean, ""))
				.addObject("typelist",EnumSet.allOf(OPTYPE.class));
	}
}
