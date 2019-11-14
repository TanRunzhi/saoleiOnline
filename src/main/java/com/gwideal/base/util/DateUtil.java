package com.gwideal.base.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

  /**
   * 字符串转日期
   *
   * @param str
   * @param formart
   * @return
   */
  public static Date stringToDate(String str, String formart) {
    try {
      if (str == null) {
        str = "yyyy-MM-dd";
      }
      return new SimpleDateFormat(formart).parse(str);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 日期转字符串（英文）
   *
   * @param date
   * @param format
   * @return
   */
  public static String dateToStrInEnglish(Date date, String format) {
    try {
      if (date == null) {
        return "";
      }
      return new SimpleDateFormat(format, Locale.ENGLISH).format(date);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * 日期转字符串（英文）
   *
   * @param date
   * @param format
   * @return
   */
  public static String dateToStr(Date date, String format) {
    try {
      if (date == null) {
        return "";
      }
      return new SimpleDateFormat(format).format(date);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }


  public static Date getYearBegin(Integer year) {
    return stringToDate(year + "-01-01", "yyyy-MM-dd");
  }

  public static Date getYearEnd(Integer year) {
    return stringToDate(year + "-12-31", "yyyy-MM-dd");
  }

  /**
   * 计算两个日期之间相差的天数
   *
   * @param sDate 较小的时间
   * @param bDate 较大的时间
   * @return 相差天数
   */
  public static int getDaysBetween(Date sDate, Date bDate) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      sDate = sdf.parse(sdf.format(sDate));
      bDate = sdf.parse(sdf.format(bDate));
      Calendar cal = Calendar.getInstance();
      cal.setTime(sDate);
      long time1 = cal.getTimeInMillis();
      cal.setTime(bDate);
      long time2 = cal.getTimeInMillis();
      long betweenDays = (time2 - time1) / (1000 * 3600 * 24);
      return Integer.parseInt(String.valueOf(betweenDays));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * 日期比较
   *
   * @param timeInterval 类别
   * @param date1        大日期
   * @param date2        小日期
   * @return
   */
  public static long dateDiff(String timeInterval, Date date1, Date date2) {
    if (timeInterval.equals("y")) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date1);
      int time = calendar.get(Calendar.YEAR);
      calendar.setTime(date2);
      return time - calendar.get(Calendar.YEAR);
    }

    if (timeInterval.equals("q")) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date1);
      int time = calendar.get(Calendar.YEAR) * 4;
      calendar.setTime(date2);
      time -= calendar.get(Calendar.YEAR) * 4;
      calendar.setTime(date1);
      time += calendar.get(Calendar.MONTH) / 4;
      calendar.setTime(date2);
      return time - calendar.get(Calendar.MONTH) / 4;
    }

    if (timeInterval.equals("m")) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date1);
      int time = calendar.get(Calendar.YEAR) * 12;
      calendar.setTime(date2);
      time -= calendar.get(Calendar.YEAR) * 12;
      calendar.setTime(date1);
      time += calendar.get(Calendar.MONTH);
      calendar.setTime(date2);
      return time - calendar.get(Calendar.MONTH);
    }

    if (timeInterval.equals("w")) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date1);
      int time = calendar.get(Calendar.YEAR) * 52;
      calendar.setTime(date2);
      time -= calendar.get(Calendar.YEAR) * 52;
      calendar.setTime(date1);
      time += calendar.get(Calendar.WEEK_OF_YEAR);
      calendar.setTime(date2);
      return time - calendar.get(Calendar.WEEK_OF_YEAR);
    }

    if (timeInterval.equals("d")) {
      long time = date1.getTime() / 1000 / 60 / 60 / 24;
      return time - date2.getTime() / 1000 / 60 / 60 / 24;
    }

    if (timeInterval.equals("h")) {
      long time = date1.getTime() / 1000 / 60 / 60;
      return time - date2.getTime() / 1000 / 60 / 60;
    }

    if (timeInterval.equals("mi")) {
      long time = date1.getTime() / 1000 / 60;
      return time - date2.getTime() / 1000 / 60;
    }

    if (timeInterval.equals("s")) {
      long time = date1.getTime() / 1000;
      return time - date2.getTime() / 1000;
    }

    return date1.getTime() - date2.getTime();
  }

  public static double getMonthBetween(Date sDate, Date eDate) {
    Calendar sCal = Calendar.getInstance();
    sCal.setTime(sDate);
    Calendar eCal = Calendar.getInstance();
    eCal.setTime(eDate);
    return (eCal.get(Calendar.YEAR) - sCal.get(Calendar.YEAR)) * 12 + (eCal.get(Calendar.MONTH) - sCal.get(Calendar.MONTH)) + (eCal.get(Calendar.DATE) > sCal.get(Calendar.DATE) ? 0.5 : 0);
  }

  /**
   * 获得指定日期n天后的日期
   *
   * @param date 指定日期
   * @param days 天数（正：返回指定日期之后，负：返回指定日期之前）
   * @return
   */
  public static Date getDateAfterDays(Date date, int days) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.DAY_OF_YEAR, days);
    return c.getTime();
  }

  public static Date getDateAfterHours(Date date, int hours) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.HOUR, hours);
    return c.getTime();
  }

  /**
   * 获得指定日期n月后的日期
   *
   * @param date   指定日期
   * @param months 天数（正：返回指定日期之后，负：返回指定日期之前）
   * @return
   */
  public static Date getDateAfterMonths(Date date, int months) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.MONTH, months);
    return c.getTime();
  }

  /**
   * 获得指定日期当周的最后一天
   *
   * @return yyyy-MM-dd
   */
  public static Date getLastDayOfWeek(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 7);
    return c.getTime();
  }

  /**
   * 获得指定日期当月的最后一天
   *
   * @param date
   * @return
   */
  public static Date getLastDayOfMonth(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
    return c.getTime();
  }

  /**
   * 获得指定日期当月的某一天
   *
   * @param date
   * @return
   */
  public static Date getTheDayOfMonth(Date date, int day) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    //c.add(Calendar.MONTH, -1);
    c.set(Calendar.DAY_OF_MONTH, day);
    return c.getTime();
  }

  public static String getNow(String format) {
    if (StringUtils.isEmpty(format)) {
      format = "yyyy-MM-dd";
    }
    return dateToStr(new Date(), format);
  }



  /**
   * 日期比较 - 年月日
   *
   * @param d1
   * @param d2
   * @return 1 d1>d2、-1 d1<d2、0 d1=d2
   */
  public static int compareDateWithYMD(Date d1, Date d2) {
    try {
      String format = "yyyy-MM-dd";
      //取年月日
      d1 = DateUtil.stringToDate(DateUtil.dateToStr(d1, format), format);
      //取年月日
      d2 = DateUtil.stringToDate(DateUtil.dateToStr(d2, format), format);
      if (d1.getTime() > d2.getTime()) {
        return 1;
      } else if (d1.getTime() < d2.getTime()) {
        return -1;
      } else {
        return 0;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return 0;
  }

  public static String appendToFiveLen(Object s) {
    String str = s.toString();
    if (str.length() < 5) {
      int len = 5 - str.length();
      for (int i = 0; i < len; i++) {
        str = "0" + str;
      }
    }
    return str;
  }

  public static void main(String[] args) {
    Date today = new Date();
    //System.out.println(DateUtil.dateToStr(getDateAfterDays(today, 3), "yyyy-MM-dd"));
    //System.out.println(DateUtil.dateToStr(getLastDayOfWeek(today), "yyyy-MM-dd"));
    //System.out.println(DateUtil.dateToStr(getLastDayOfMonth(today), "yyyy-MM-dd"));
    System.out.println(DateUtil.dateToStr(new Date(), "yyyy年MM月dd日 hh:mm"));
    System.out.println(DateUtil.compareDateWithYMD(stringToDate("2016-4-6", "yyyy-MM-dd"), stringToDate("2016-4-6", "yyyy-MM-dd")));
    System.out.println(DateUtil.compareDateWithYMD(stringToDate("2016-4-6", "yyyy-MM-dd"), today));
    System.out.println("时分" + dateToStr(new Date(), "HHmm"));
    System.out.println("增加12个月" + DateUtil.getDateAfterMonths(DateUtil.stringToDate("2018-01", "yyyy-MM"), 12));
//		System.out.print(DateUtil.dateToStrInEnglish(today,"MMM"));
    //   System.out.println(appendToFiveLen("101"));

    System.out.println(dateToStr(getDateAfterMonths(new Date(), -12), "yyyy-MM-dd"));
  }

}
