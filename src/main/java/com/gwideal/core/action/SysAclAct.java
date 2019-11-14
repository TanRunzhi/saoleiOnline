package com.gwideal.core.action;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.action.BaseAct;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.core.entity.RoleGroup;
import com.gwideal.core.entity.SysAcl;
import com.gwideal.core.entity.SysRole;
import com.gwideal.core.manager.LookupsMng;
import com.gwideal.core.manager.RoleGroupMng;
import com.gwideal.core.manager.SysAclMng;
import com.gwideal.core.manager.SysRoleMng;
import com.gwideal.util.io.PropertiesReader;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/acl")
public class SysAclAct extends BaseAct {

  private final static Logger logger = LogManager.getLogger();

  @RequestMapping("index")
  public ModelAndView index() {
    return new ModelAndView("sys/acl/index")
        .addObject("lookups_aclTypes", lookupsMng.getCacheLookupsByCode("ACLTYPE"));
  }

  @RequestMapping("loadAclTree")
  @ResponseBody
  public String loadAclTree(String roleId) {
    return sysAclMng.getAclTree(roleId);
  }

  @RequestMapping("loadRoleTree")
  @ResponseBody
  public String loadRoleTree() {
    return sysRoleMng.getCacheRoleTree();
  }

  @RequestMapping("addRoleObject")
  @ResponseBody
  public String addRoleObject(String type, String code, String name, String seq, String groupId) {
    if (StringUtils.isNotEmpty(type))
      if ("group".equals(type))
        if (roleGroupMng.checkDuplicated(null, "groupCode", code,null))
          return JSON.toJSONString(new BaseJsonResult(false, PropertiesReader.getPropertiesValue("msg.general.keyDuplicated")));
        else
          roleGroupMng.saveOrUpdate(new RoleGroup(code, name, seq));
      else if ("role".equals(type))
        if (sysRoleMng.checkDuplicated(null, "code", code,null))
          return JSON.toJSONString(new BaseJsonResult(false, PropertiesReader.getPropertiesValue("msg.general.keyDuplicated")));
        else
          sysRoleMng.saveOrUpdate(new SysRole(code, name, seq, roleGroupMng.load(groupId)));
    return JSON.toJSONString(new BaseJsonResult());
  }

  @RequestMapping("getAclChildren")
  @ResponseBody
  public String getAclChildren(String roleId, String id) {
    return sysAclMng.getAclChildren(roleId, id);
  }

  @RequestMapping("getRoleChildren")
  @ResponseBody
  public String getRoleChildren(String id, String userid) {
    return sysRoleMng.getCacheRoleChildren(id);
  }

  @RequestMapping("addAcl")
  @ResponseBody
  public String addAcl(String key, String name, String aclType) {
    if (sysAclMng.checkDuplicated(null, "aclKey", key,null))
      return JSON.toJSONString(new BaseJsonResult(false, PropertiesReader.getPropertiesValue("msg.general.keyDuplicated")));
    sysAclMng.saveOrUpdate(new SysAcl(key, name, aclType));
    return JSON.toJSONString(new BaseJsonResult());
  }

  @RequestMapping("saveRoleAcl")
  @ResponseBody
  public String saveRoleAcl(String roleId, String aclKey, String op) {
    logger.debug("auto save role acl fired...");
    sysRoleMng.saveRoleAcl(roleId, aclKey, op);
    return JSON.toJSONString(new BaseJsonResult());
  }

  @RequestMapping("saveRoleUser")
  @ResponseBody
  public String saveRoleUser(String roleId, String userId, String op) {
    logger.debug("auto save role user fired...");
    sysRoleMng.saveRoleUser(roleId, userId, op);
    return JSON.toJSONString(new BaseJsonResult());
  }

  @RequestMapping("list")
  public ModelAndView list(SysAcl queryBean) {
    return new ModelAndView("sys/acl/list")
        .addObject("queryBean", queryBean)
        .addObject("lookup_aclType", lookupsMng.getCacheLookupsByCode("ACLTYPE"))
        .addObject("list", sysAclMng.mergeHQL(queryBean, " "));
  }

  @RequestMapping("input")
  public ModelAndView input(SysAcl queryBean) {
    return new ModelAndView("sys/acl/input")
        .addObject("lookup_aclType", lookupsMng.getCacheLookupsByCode("ACLTYPE"))
        .addObject("bean", sysAclMng.initBean(queryBean));
  }

  @RequestMapping("save")
  public ModelAndView save(SysAcl queryBean) {
    SysAcl bean = sysAclMng.get(queryBean.getId());
    bean.setAclKey(queryBean.getAclKey());
    bean.setName(queryBean.getName());
    bean.setRemark(queryBean.getRemark());
    sysAclMng.saveOrUpdate(bean);
    return list(queryBean);
  }

  @Resource
  private SysAclMng sysAclMng;

  @Resource
  private RoleGroupMng roleGroupMng;

  @Resource
  private SysRoleMng sysRoleMng;

  @Resource
  private LookupsMng lookupsMng;
}
