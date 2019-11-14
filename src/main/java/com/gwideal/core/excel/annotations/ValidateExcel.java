package com.gwideal.core.excel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidateExcel {

  /**
   * 默认为第一页
   *
   * @return 页数
   */
  int sheet () default 0;


  /**
   * 默认为不付值
   *
   * @return 行所在位子
   */
  int row () default -1;

  /**
   * @return 列所在位子
   */
  int cell ();

  /**
   * @return 是否必填
   */
  boolean required ();

  String columnName ();
}
