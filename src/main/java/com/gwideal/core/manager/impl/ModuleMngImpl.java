package com.gwideal.core.manager.impl;

import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.core.config.Constants;
import com.gwideal.core.entity.Menu;
import com.gwideal.core.entity.Module;
import com.gwideal.core.manager.MenuMng;
import com.gwideal.core.manager.ModuleMng;
import com.gwideal.util.common.UtilEmpty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service ("moduleMng")
@Transactional
public class ModuleMngImpl extends BaseMngImpl<Module> implements ModuleMng {

  @Override
  public List<Module> getCacheList() {
    return find("from Module where activated = 1 order by seq");
  }

  @Override
  public void activate(String id, boolean activated) {
    Module module = load(id);
    module.setActivated(activated);
    saveOrUpdate(module);
  }

  @Override
  public List<String> getAclsByModule(Module m) {
    try {
      List<Menu> aclAll = menuMng.findBy("module.code", "babydb", "seq");
      if (aclAll != null) {
        List<String> acls = new ArrayList<>();
        for (Menu acl : aclAll)
          acls.add(acl.getAclKey());
        return acls;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Module getBizModule() {
    return getModuleByCode(Constants.BIZ_MODULE_USER_CODE);
  }

  private Module getModuleByCode(String code) {
    List<Module> list = find("from Module where code = ?0 order by seq",code);
    return UtilEmpty.isArrayEmpty(list) ? null : list.get(0);
  }

  @Resource
  private MenuMng menuMng;
}
