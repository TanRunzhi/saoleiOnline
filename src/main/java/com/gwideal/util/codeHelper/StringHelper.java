package com.gwideal.util.codeHelper;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by li_hongyu on 14-7-21.
 */
public class StringHelper {

  /**
   * 将第一个字符替换成小写
   *
   * @param str 字符串
   * @return 字符串
   */
  public static String lowCastFirstChar(String str) {
    StringBuilder sb = new StringBuilder(str);
    sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
    return sb.toString();
  }

  /**
   * 年度列表
   *
   * @param length 列表长度
   * @return 年度列表
   */
  public static List<Integer> getYearList(Integer length) {
    Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
    List<Integer> l = new ArrayList<Integer>();
    for (int i = currentYear - length / 2; i <= currentYear + length / 2; i++)
      l.add(i);
    return l;
  }

  /**
   * 将第一个字符替换成大写
   *
   * @param str 字符串
   * @return 字符串
   */
  public static String upperCastFirstChar(String str) {
    StringBuilder sb = new StringBuilder(str);
    sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
    return sb.toString();
  }

  public static String formatDateSql(String fieldName, String publishtime1, String publishtime2) {
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotEmpty(publishtime1))
      sb.append(" and ").append(fieldName).append(">=").append("to_date('").append(publishtime1).append("','yyyy-MM-DD') ");
    if (StringUtils.isNotEmpty(publishtime2))
      sb.append(" and ").append(fieldName).append("<=").append("to_date('").append(publishtime2).append("','yyyy-MM-DD') ");
    return sb.toString();
  }

  public static void main(String[] args) {
    System.out.println(formatDateSql("Usedtime", "", "2015-01-01"));
    System.out.println(formatStringToSqlInQuery("a"));
    System.out.println(formatStringToSqlInQuery("a,b,"));
  }

  public static String formatStringSql(String fieldName, String value) {
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotEmpty(value))
      sb.append(" and ").append(fieldName).append("='").append(value).append("' ");
    return sb.toString();
  }

  /**
   * 字符串集转成in查询形式：('','')
   * @param strs
   * @return
   */
  public static String formatStringToSqlInQuery(String strs) {
    if (StringUtils.isNotEmpty(strs)) {
      String result = "(";
      String[] arr = strs.split(",");
      for (String s : arr) {
        if (StringUtils.isNotEmpty(s)) {
          result += "'" + s + "',";
        }
      }
      return result.substring(0, result.length() - 1) + ")";
    }
    return "";
  }

  /**
   * 拼接馆编/室编档号
   *
   * @param arrs         字符串集合
   * @param split        连接符
   * @param includeEmpty 是否允许包含空字符串
   * @return
   */
  public static String convertCode(String[] arrs, String split, boolean includeEmpty) {
    String str = "";
    for (String s : arrs) {
      if (!includeEmpty && StringUtils.isEmpty(s)) {
        continue;
      }
      str += s + split;
    }
    return StringUtils.isNotEmpty(str) ? str.substring(0, str.length() - split.length()) : str;
  }
}
