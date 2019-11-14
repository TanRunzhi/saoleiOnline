package com.gwideal.util.common;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/18.
 */
public class UtilEmpty {

  /**
   * 验证集合非空
   *
   * @param array
   * @return
   */
  public static boolean isArrayEmpty (Collection array) {
    if ( array == null || array.size() == 0 ) {
      return true;
    }
    return false;
  }

  /**
   * 验证Map非空
   *
   * @param map
   * @return
   */
  public static boolean isArrayEmpty (Map map) {
    if ( map == null || map.size() == 0 ) {
      return true;
    }
    return false;
  }

  /**
   * 验证数组非空
   *
   * @param objs
   * @return
   */
  public static boolean isArrayEmpty (Object[] objs) {
    if ( objs == null || objs.length == 0 ) {
      return true;
    }
    return false;
  }

  /**
   * 验证字符串非空
   *
   * @param str
   * @return
   */
  public static boolean isStringEmpty (String str) {
    if ( str == null || "".equals(str) ) {
      return true;
    }
    return false;
  }

  public static String emptyValue (Object str) {
    return str == null ? "" : str.toString();
  }
}
