package com.gwideal.core.manager;


import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.RoleGroup;

public interface RoleGroupMng extends BaseMng<RoleGroup> {
	String loadGroupTree (String deptid);
	
}
