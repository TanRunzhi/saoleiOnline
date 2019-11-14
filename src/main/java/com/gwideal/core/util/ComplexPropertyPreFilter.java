package com.gwideal.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：
 * @Comment ： fastjson 针对类型的属性选择过滤器(可以跨层级) <br>
 */
public class ComplexPropertyPreFilter implements PropertyPreFilter {

  private Map<Class<?>, List<String>> includes = new HashMap<Class<?>, List<String>>();
  private Map<Class<?>, List<String>> excludes = new HashMap<Class<?>, List<String>>();

  static {
    JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
  }

  public ComplexPropertyPreFilter() {

  }

  public ComplexPropertyPreFilter(Map<Class<?>, List<String>> includes,boolean include) {
    super();
    if(include) {
      this.includes = includes;
    }else{
      this.excludes = includes;
    }
  }

  @Override
  public boolean apply(JSONSerializer serializer, Object source, String name) {
    serializer.setDateFormat("yyyy-MM-dd");
    //对象为空。直接放行
    if (source == null) {
      return true;
    }

    // 获取当前需要序列化的对象的类对象
    Class<?> clazz = source.getClass();

    // 无需序列的对象、寻找需要过滤的对象，可以提高查找层级
    // 找到不需要的序列化的类型
    for (Map.Entry<Class<?>, List<String>> item : this.excludes.entrySet()) {
      // isAssignableFrom()，用来判断类型间是否有继承关系
      if (item.getKey().isAssignableFrom(clazz)) {
        // 该类型下 此 name 值无需序列化
        if (item.getValue().contains(name)) {
          return false;
        }
      }
    }

    // 需要序列的对象集合为空 表示 全部需要序列化
    if (this.includes.isEmpty()) {
      return true;
    }

    // 需要序列的对象
    // 找到不需要的序列化的类型
    for (Map.Entry<Class<?>, List<String>> item : this.includes.entrySet()) {
      // isAssignableFrom()，用来判断类型间是否有继承关系
      if (item.getKey().isAssignableFrom(clazz)) {
        // 该类型下 此 name 值无需序列化
        if (item.getValue().contains( name)) {
          return true;
        }
      }
    }
    return false;
  }

  public Map<Class<?>, List<String>> getIncludes() {
    return includes;
  }

  public void setIncludes(Map<Class<?>, List<String>> includes) {
    this.includes = includes;
  }

  public Map<Class<?>, List<String>> getExcludes() {
    return excludes;
  }

  public void setExcludes(Map<Class<?>, List<String>> excludes) {
    this.excludes = excludes;
  }
}
