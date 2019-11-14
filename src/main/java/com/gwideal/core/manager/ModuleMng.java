package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.Module;

import java.util.List;

public interface ModuleMng extends BaseMng<Module> {
  List<Module> getCacheList ();

  void activate (String id, boolean active);
  
  List<String> getAclsByModule(Module m);

  Module getBizModule();
}
