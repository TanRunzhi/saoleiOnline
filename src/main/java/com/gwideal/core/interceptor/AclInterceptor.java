package com.gwideal.core.interceptor;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.gwideal.core.entity.Menu;
import com.gwideal.core.manager.MenuMng;
import com.gwideal.util.io.PropertiesReader;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * Created by li_hongyu on 14-7-29.
 */
public class AclInterceptor implements HandlerInterceptor {

    private static final Logger logger = LogManager.getLogger();

    @Resource
    private MenuMng menuMng;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o) throws Exception {
        logger.debug("AclInterceptor fired ...");
        boolean isControl = Boolean.parseBoolean(PropertiesReader.getPropertiesValue("sys.acl.control"));
        String currentUrl = httpServletRequest.getRequestURI();
        logger.debug(currentUrl);
        if(!isControl){
            return true;
        }
        Set<String> aclSet = (Set<String>) httpServletRequest.getSession().getAttribute("aclSet");
        List<Menu> menus = menuMng.getCacheEnableMenuByModuleCode(PropertiesReader.getPropertiesValue("app.code"));
        if (aclSet != null && aclSet.size() > 0) {
            String aclKey = "";
            for (Menu m : menus) {
                if (null != m.getUrl() && currentUrl.contains(m.getUrl())) {
                    aclKey = m.getAclKey();
                    break;
                }
            }

            if (StringUtils.isEmpty(aclKey) && aclSet.contains(aclKey)) {
                 /*httpServletRequest.setAttribute("message", "没有足够的权限");
               httpServletRequest.getRequestDispatcher("/syserror.jsp").forward(httpServletRequest, response);*/
                response.sendRedirect("/page/no_acls.jsp");
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o, Exception e) throws Exception {

    }
}
