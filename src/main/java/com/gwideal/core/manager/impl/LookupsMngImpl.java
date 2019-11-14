package com.gwideal.core.manager.impl;

import com.alibaba.fastjson.JSON;
import com.gwideal.base.entity.BaseJsonResult;
import com.gwideal.base.manager.impl.BaseMngImpl;
import com.gwideal.core.entity.Lookups;
import com.gwideal.core.manager.LookupsMng;
import com.gwideal.core.util.JsonUtil;
import com.gwideal.util.common.UtilEmpty;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by li_hongyu on 14-7-11.
 */
@Service("lookupsMng")
@Transactional
public class LookupsMngImpl extends BaseMngImpl<Lookups> implements LookupsMng {

  @Override
  public List<Lookups> getCacheLookupsByCode(String cCode) {
    return find("from Lookups where parent.cCode = ?0 order by seq,lCode", cCode);
  }

  @Override
  public List<Lookups> getCacheLookupsByCodeOrder(String cCode) {
    return find("from Lookups where parent.cCode = ?0 order by seq", cCode);
  }

  @Override
  public List<Lookups> getLookupsByCode(String cCode) {
    return getLookupsByCode(cCode,null);
  }

  @Override
  public List<Lookups> getLookupsByCodeAndDepartCode(String cCode,String departCode) {
    return find("from Lookups where 1 = 1 and parent.cCode = ?0 and departCode = ?1 order by lCode", cCode,departCode);
  }

  @Override
  public List<Lookups> getDefaulLookupsByCodeAndDepartCode(String cCode,String departCode) {
    List<Lookups> departLookups = find("from Lookups where 1 = 1 and parent.cCode = ?0 and departCode = ?1 order by lCode", cCode,departCode);
    if(UtilEmpty.isArrayEmpty(departLookups)){
      return find("from Lookups where 1 = 1 and parent.cCode = ?0 and departCode is null order by lCode", cCode);
    }else {
      return departLookups;
    }
  }

  @Override
  public List<Lookups> getLookupsByCode(String cCode, String except) {
    if (StringUtils.isNotEmpty(except)) {
      return find("from Lookups where 1 = 1 and parent.cCode = ?0 and lCode not in (" + except + ") order by seq", cCode);
    } else {
      return find("from Lookups where 1 = 1 and parent.cCode = ?0 order by lCode", cCode);
    }
  }

  public List<Lookups> getCategory() {
    return find("from Lookups where 1 = 1 and parent is null and cName is not null");
  }

  @Override
  public Lookups getCategoryBycCode (String cCode){
    List<Lookups> list = find("from Lookups where 1 = 1 and parent is null and cName is not null and cCode = ?0" ,cCode);
    return UtilEmpty.isArrayEmpty(list) ? null : list.get(0);
  }

  @Override
  public String getScan(String category, String lookupsCode) {
    return find("from Lookups where 1 = 1 and parent.cCode = ?0 and lCode = ?1", category, lookupsCode).get(0).getlName();
  }

  @Override
  public String getJSONLookups(String cate) {
    //return JSONHelper.formatObject(getCacheLookupsByCode(cate));
    List<Lookups> list = getCacheLookupsByCode(cate);
    return JsonUtil.ObjToJsonString(Arrays.asList("parent", "module", "children"), list,true);
  }

  @Override
  public String getCateByModuleId(String moduleId, String beanId) {
    Map<String, Object> result = new BaseJsonResult();
    List<Map<String, Object>> list = jdbcTemplate.queryForList("select pid as id,CATEGORY_NAME as name from t_core_lookups where MODULE_ID = ? and PARENT_ID is null order by CATEGORY_CODE", moduleId);
    result.put("data", list);
    if (StringUtils.isNotEmpty(beanId))
      result.put("parentId", jdbcTemplate.queryForList("select PARENT_ID as parent from t_core_lookups where pid = ?", beanId).get(0).get("parent").toString());
    return JSON.toJSONString(result);
  }

  @Override
  public Lookups getLookupsByCCodeLCode(String category, String lookupsCode) {
    List<Lookups> list = find("from Lookups where parent.cCode = ?0 and lCode = ?1", category, lookupsCode);
    return list.isEmpty() ? null : list.get(0);
  }

  @Override
  public String getCacheLookupsNameByCCodeLCode(String category, String lookupsCode) {
    Lookups lookups = getLookupsByCCodeLCode(category, lookupsCode);
    return lookups == null ? "" : lookups.getlName();
  }

  @Override
  public String getCacheLookupsCodeByCCodeLCode(String category, String lookupsName) {
    Lookups lookups = getLookupsByCCodeLName(category, lookupsName);
    return lookups == null ? "" : lookups.getlCode();
  }

  @Override
  public Lookups getLookupsByCCodeLName(String category, String lookupsName) {
    List<Lookups> list = find("from Lookups where parent.cCode = ?0 and lName = ?1", category, lookupsName);
    return list.isEmpty() ? null : list.get(0);
  }

  @Override
  public Lookups getLookupsByCCodeLNameDepartCode(String category, String lookupsName,String departCode) {
    List<Lookups> list = find("from Lookups where parent.cCode = ?0 and lName = ?1 and departCode = ?2", category, lookupsName,departCode);
    return list.isEmpty() ? null : list.get(0);
  }

  /**
   * 用于通过LName模糊匹配出对应的LCode，用于模糊查询
   *   eg :  数据字典中有 : 01 数据1 ，02 字典2 ，03 数据3
   *    lookupsName = "数" ，此时返回 "-1,01,03" (-1用于防止空时放于in中sql报错)
   * */
  @Override
  public String getLCodeByLName(String category, String lookupsName){
    if(StringUtils.isEmpty(lookupsName)){
      return "";
    }
    String newParam = "-1";
    for(Lookups look :getCacheLookupsByCode(category)){
      if(look.getlName().indexOf(lookupsName) > -1){
        newParam += ","+look.getlCode();
      }
    }
    return newParam;
  }

  @Override
  public String getCacheLookupsCodeByCCodeLName(String category, String lookupsName) {
    try {
      return getLookupsByCCodeLName(category,lookupsName).getlCode();
    } catch (Exception e) {
      return "";
    }
  }

}
