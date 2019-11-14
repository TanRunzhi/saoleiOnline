package com.gwideal.core.manager;

import com.gwideal.base.manager.BaseMng;
import com.gwideal.core.entity.Lookups;

import java.util.List;

/**
 * Created by li_hongyu on 14-7-11.
 */

public interface LookupsMng extends BaseMng<Lookups> {

  List<Lookups> getCacheLookupsByCode (String cCode);

  List<Lookups> getCacheLookupsByCodeOrder (String cCode);

  List<Lookups> getLookupsByCode(String cCode);

  List<Lookups> getLookupsByCodeAndDepartCode(String cCode,String departCode);

  List<Lookups> getDefaulLookupsByCodeAndDepartCode(String cCode,String departCode);

  /**
   * 获得字典项，不包括except中出现的code
   *
   * @param cCode  字典类别编码
   * @param except 不包括该code集合
   * @return 字典列表
   */
  List<Lookups> getLookupsByCode (String cCode, String except);

  List<Lookups> getCategory ();

  Lookups getCategoryBycCode (String cCode);

  String getScan (String category, String lookupsCode);

  String getJSONLookups (String cate);

  String getCateByModuleId (String moduleId, String beanId);
  
  /**
   * 根据字典类code、字典项code，查找字典项
   * @param category
   * @param lookupsCode
   * @return
   */
  Lookups getLookupsByCCodeLCode (String category, String lookupsCode);

  String  getCacheLookupsNameByCCodeLCode(String category, String lookupsCode);

  String  getCacheLookupsCodeByCCodeLCode(String category, String lookupsName);
  
  /**
   * 根据字典类code、字典项名称，查找字典项
   * @param category
   * @param lookupsName
   * @return
   */
  Lookups getLookupsByCCodeLName (String category, String lookupsName);

  Lookups getLookupsByCCodeLNameDepartCode(String category, String lookupsName,String departCode);

  String getLCodeByLName(String category, String lookupsName);

  String getCacheLookupsCodeByCCodeLName(String category, String lookupsName);
}
