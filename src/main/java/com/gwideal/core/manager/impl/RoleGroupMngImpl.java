package com.gwideal.core.manager.impl;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.core.entity.RoleGroup;
import com.gwideal.core.manager.RoleGroupMng;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service ("roleGroupMng")
@Transactional
public class RoleGroupMngImpl extends BaseMngImpl<RoleGroup> implements
        RoleGroupMng {

    private final static Logger logger = LogManager.getLogger();

    @Override
    public String loadGroupTree (String deptid) {
        String sql = "select pid AS id,name as [text],''  iconCls,'true' isGroup,"
                + "(select (case when count(*)>0 then 'closed' else '' end )  "
                + "from T_CORE_ROLE h where h.GROUP_ID=t.pid) [state]  "
                + "from T_CORE_ROLE_GROUP t ";
        if ( !StringUtils.isEmpty(deptid) ) {
            sql += " where t.GROUP_CODE!='city'";
        }
        sql += " order by seq ";
        return JSON.toJSONString(jdbcTemplate.queryForList(sql));
    }
}
