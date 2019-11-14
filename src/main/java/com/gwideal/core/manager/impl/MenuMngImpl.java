package com.gwideal.core.manager.impl;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.core.entity.Menu;
import com.gwideal.core.manager.MenuMng;
import com.gwideal.util.common.UtilEmpty;
import com.gwideal.util.io.PropertiesReader;
import com.gwideal.util.json.JSONHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service ("menuMng")
@Transactional
public class MenuMngImpl extends BaseMngImpl<Menu> implements MenuMng {


    /**
     * 获得一级菜单
     *
     * @return 一级栏目列表
     */
    @Override
    public List<Menu> getCate () {
        return find("from Menu where 1 = 1 and parent is null and name is not null order by seq");
    }

    /**
     * 通过menuCode获得子菜单
     *
     * @param id 栏目编码
     * @return 子栏目列表
     */
    @Override
    public List<Menu> getCacheSubMenu (String id) {
        return find("from Menu where 1 = 1 and parent.id = ?0 order by seq", id);
    }

    /**
     * 初始化菜单权限
     * 初始化规则：权限Key设置为：模块code+父菜单code+菜单code+'menu'
     *
     * @return 成功字符串
     */
    @Override
    public String initMenuAcl () {
        for ( Menu m : findOrderBy("from Menu where flag = 1", "module.seq,seq") ) {
            m = get(m.getId());
            m.setAclKey(m.getModule().getCode() + "."
                    + (m.getParent() == null ? m.getCode() + "." : (m.getParent().getCode() + "." + m.getCode() + "."))
                    + "menu");
            saveOrUpdate(m);
        }
        return PropertiesReader.getPropertiesValue("sys.title.ok");
    }

    @Override
    public void logicalDel (String id) {
        Menu menu = load(id);
        menu.setFlag(false);
        saveOrUpdate(menu);
    }

    @Override
    public String getJSONMenu (String id) {
        Menu m = get(id);
        List<Menu> list;
        if ( m.getSeq().contains("99") )
            list = getCacheSubMenu(id);
        else {
            list = getCacheSubMenu(m.getParent().getId());
        }
        return JSONHelper.formatObject(list);
    }

    @Override
    public String getMenuByModuleId (String moduleId, String beanId) {
        Map<String, Object> result = new BaseJsonResult();
        //AND PARENT_ID IS NULL
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT pid AS id,name FROM t_core_menu WHERE MODULE_ID = ?  AND flag = 1 ORDER BY SEQ", moduleId);
        result.put("data", list);
        if ( StringUtils.isNotEmpty(beanId) ) {
            List<Map<String, Object>> parents =jdbcTemplate.queryForList("SELECT PARENT_ID AS parent FROM t_core_menu WHERE pid = ? and PARENT_ID IS NOT NULL ", beanId);
            result.put("parentId", UtilEmpty.isArrayEmpty(parents)?null:parents.get(0).get("parent").toString());
        }
        return JSON.toJSONString(result);
    }

    @Override
    public List<Menu> getCacheEnableMenuByModuleCode(String moduleCode) {
        return find("from Menu where flag = 1 and parent is null and module.code = ?0 order by seq", moduleCode);
    }
}
