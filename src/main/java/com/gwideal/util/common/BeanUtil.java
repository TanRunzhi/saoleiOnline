package com.gwideal.util.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @author zzy
 * 拷贝类属性
 */
public class BeanUtil {

  private final static Logger logger = LogManager.getLogger();


  public static void copyNewObj(Object source, Object target, List<String> excludes) {
    for (Field f : source.getClass().getDeclaredFields()) {
      try {
        if (!excludes.contains(f.getName())) {
          f.setAccessible(true);
          f.set(target, f.get(source));
        }
      } catch (IllegalAccessException e) {
        logger.debug(f.getName() + " copy error");
      }
    }
  }

}
