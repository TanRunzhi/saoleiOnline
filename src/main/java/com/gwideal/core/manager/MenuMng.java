package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.Menu;

import java.util.List;

public interface MenuMng extends BaseMng<Menu> {

  List<Menu> getCate ();

  List<Menu> getCacheSubMenu (String code);

  String initMenuAcl ();

  void logicalDel (String id);

  String getJSONMenu (String id);

  String getMenuByModuleId (String moduleId, String beanId);

  List<Menu> getCacheEnableMenuByModuleCode(String moduleCode);
}
