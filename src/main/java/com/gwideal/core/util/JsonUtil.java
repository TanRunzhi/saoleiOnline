package com.gwideal.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/4.
 */
public class JsonUtil {


  /**
   * list转json(过滤属性)
   *
   * @param filters 过滤属性集合
   * @param sources  转换对象
   * @param isExclude 是否不包含
   * @return
   */
  public static String ObjToJsonString(List<String> filters, Object sources, boolean isExclude) {
    if ( sources == null  ) {
      return JSON.toJSONString(sources);
    }
    PropertyFilter filter = new PropertyFilter() {
      public boolean apply (Object source, String name, Object value) {
        if(isExclude){
          return !filters.contains(name);
        }else{
          return filters.contains(name);
        }
      }
    };
    SerializeWriter sw = new SerializeWriter();
    JSONSerializer serializer = new JSONSerializer(sw);
    serializer.getPropertyFilters().add(filter);
    serializer.write(sources);
    return sw.toString();
  }


  public static String ObjectToJSON (Object source, Map<Class<?>, List<String>> map, SerializerFeature feature) {
    ComplexPropertyPreFilter ss = new ComplexPropertyPreFilter(map,false);
    return  JSON.toJSONString(source,ss,SerializerFeature.WriteNullStringAsEmpty);
  }



  public static void main (String[] args) {

  }
}
