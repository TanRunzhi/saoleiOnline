package com.gwideal.core.excel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DynamicExcel {


  String comment();  //注释名称

  String value();    //对应列名

  boolean exclude() default false;

  int index() default -1;       //索引名

  boolean fetch() default false; //导出时是否关联

  String parent() default "";  //关联导出时的列名称

  String scope() default "基本信息";

  int rows() default -1;         //单对象导出时第几行

  int cell() default -1;         //单对象导出时第几列

  int cityRow() default -1;       //评价导出时市级行号

  int cityCell() default -1;      //评价导出时市级列号

  boolean single() default false; //是否单对象导出

  String type() default "";//类型
}
