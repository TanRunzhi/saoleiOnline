package com.gwideal.util.codeHelper;


import org.springframework.util.StringUtils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by li_hongyu on 14-6-17.
 */
public class AppHelper {
  /**
   * 验证密码是否符合规则（数字+字母，6-16位）
   *
   * @param str 密码字符串
   * @return 验证是否通过
   */
  public static boolean checkPassword(String str) {
    return str.matches("^[0-9a-zA-Z]{6,16}");
  }

  public static List<String> clearEmpty(List<String> str) {
    if (str.size() > 1) {
      for (String s : str) {
        if ( StringUtils.isEmpty(s))
          str.remove(s);
      }
      return str;
    } else if (str.size() == 1) {
      if ( StringUtils.isEmpty(str.get(0)))
        return null;
      else
        return str;
    } else
      return str;
  }

  /**
   * 数组转字符串
   *
   * @param str 数组
   * @return 字符串
   */
  public static String arrayToString(String[] str) {
    StringBuilder sb = new StringBuilder();
    if (str != null && str.length > 0)
      for (String s : str) {
        sb.append(s).append(",");
      }
    return sb.toString();
  }

  /**
   * 格式化大数字
   *
   * @param bigNum 大数字
   * @return 正常数字
   */
  public static String formatBigNumberToNormal(Double bigNum) {
    NumberFormat nf = NumberFormat.getInstance();
    nf.setRoundingMode(RoundingMode.HALF_UP);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    if (bigNum != null) {
      return nf.format(bigNum).replace(",", "");
    }
    return "";
  }
}
