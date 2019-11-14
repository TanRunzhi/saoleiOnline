package com.gwideal.util.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangyinghu on 15/6/25.
 */
public class DateUtil {

  private final static Logger logger = LogManager.getLogger();

  public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
  public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static final int YEAR = Calendar.YEAR;
  public static final int MONTH = Calendar.MONTH;
  public static final int DATE = Calendar.DATE;
  public static final int HOUR = Calendar.HOUR;
  public static final int MINUTE = Calendar.MINUTE;
  public static final int SECOND = Calendar.SECOND;
  public static final int WEEKDAY = Calendar.DAY_OF_WEEK;

  /**
   * @return String
   * @see 转换时间
   */
  public static Date strToDate(String time) {
    if (time == null || time.length() < 1)
      return null;
    return parseDateTime(time).getTime();
  }

  /**
   * 日期转字符串（英文）
   *
   * @param date
   * @param formart
   * @return
   */
  public static String dateToStr(Date date, String formart) {
    try {
      if (date == null) {
        return "";
      }
      return new SimpleDateFormat(formart).format(date);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * @return String
   * @see 取得当前时间（格式为：yyy-MM-dd HH:mm:ss）
   */
  public static String GetDateTime() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String sDate = sdf.format(new Date());
    return sDate;
  }

  /**
   * @return String
   */
  public static String GetTimeFormat(String strFormat) {
    SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
    String sDate = sdf.format(new Date());
    return sDate;
  }

  /**
   * 计算指定日期时间之间的时间差
   *
   * @param beginStr 开始日期字符串
   * @param endStr   结束日期字符串
   * @param f        时间差的形式0-秒,1-分种,2-小时,3--天
   *                 日期时间字符串格式:yyyy-MM-dd
   */
  public static int getInterval(String beginStr, String endStr) {
    int hours = 0;
    try {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      Date beginDate = df.parse(beginStr);
      Date endDate = df.parse(endStr);
      long millisecond = endDate.getTime() - beginDate.getTime();
      /**
       * 1秒 = 1000毫秒
       * millisecond/1000 --> 秒
       * millisecond/1000*60 - > 分钟
       * millisecond/(1000*60*60) -- > 小时
       * millisecond/(1000*60*60*24) --> 天
       * */
      int f = 3;
      switch (f) {
        case 0: // second
          return Math.abs((int) (millisecond / 1000));
        case 1: // minute
          return Math.abs((int) (millisecond / (1000 * 60)));
        case 2: // hour
          return Math.abs((int) (millisecond / (1000 * 60 * 60)));
        case 3: // day
          return Math.abs((int) (millisecond / (1000 * 60 * 60 * 24)));
      }
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
    }
    return hours;
  }

  /**
   * 计算指定日期时间之间的时间差
   *
   * @param beginStr 开始日期字符串
   * @param endStr   结束日期字符串
   * @param f        时间差的形式0-秒,1-分种,2-小时,3--天
   *                 日期时间字符串格式:yyyy-MM-dd
   */
  public static int getIntervalDay(String beginStr, String endStr, String format) {
    int hours = 0;
    try {
      SimpleDateFormat df = new SimpleDateFormat(format);
      Date beginDate = df.parse(beginStr);
      Date endDate = df.parse(endStr);
      long millisecond = endDate.getTime() - beginDate.getTime(); //日期相减得到日期差X(单位:毫秒)
      /**
       * 1秒 = 1000毫秒
       * millisecond/1000 --> 秒
       * millisecond/1000*60 - > 分钟
       * millisecond/(1000*60*60) -- > 小时
       * millisecond/(1000*60*60*24) --> 天
       * */
      int f = 3;
      switch (f) {
        case 0: // second
          return Math.abs((int) (millisecond / 1000));
        case 1: // minute
          return Math.abs((int) (millisecond / (1000 * 60)));
        case 2: // hour
          return Math.abs((int) (millisecond / (1000 * 60 * 60)));
        case 3: // day
          return Math.abs((int) (millisecond / (1000 * 60 * 60 * 24)));
      }
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
    }
    return hours;
  }

  //
  public static int getIntervalMonth(String beginStr, String endStr, String format) {
    int months = 0;
    try {
      SimpleDateFormat df = new SimpleDateFormat(format);
      Date beginDate = df.parse(beginStr);
      Date endDate = df.parse(endStr);
      Calendar c1 = Calendar.getInstance();
      c1.setTime(beginDate);
      Calendar c2 = Calendar.getInstance();
      c2.setTime(endDate);

      int beginYear = c1.get(Calendar.YEAR);
      int beginMonth = c1.get(Calendar.MONTH);

      int endYear = c2.get(Calendar.YEAR);
      int endMonth = c2.get(Calendar.MONTH);
      int difMonth = (endYear - beginYear) * 12 + (endMonth - beginMonth);
      months = difMonth;
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
    }
    return months;
  }


  /**
   * @return String
   * @throws ParseException
   */
  public static String SetDateFormat(String myDate, String strFormat) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
    String sDate = sdf.format(sdf.parse(myDate));
    return sDate;
  }

  public static String FormatDateTime(String strDateTime, String strFormat) {
    if (strDateTime != null && !strDateTime.trim().equals("")) {
      String sDateTime = strDateTime;

      try {
        Calendar Cal = parseDateTime(strDateTime);
        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat(strFormat);
        sDateTime = sdf.format(Cal.getTime());
      } catch (Exception e) {

      }
      return sDateTime;
    } else
      return "";
  }

  public static String FormatDate(Date strDateTime, String strFormat) {
    String result = "";
    if (strDateTime != null) {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        result = sdf.format(strDateTime);
      } catch (Exception e) {
        logger.error(e.getMessage(),e);
      }
      return result;
    } else
      return "";
  }

  public static Date parseDate(String strDateTime, String strFormat) {
    Date result = new Date();
    if (strDateTime != null) {
      try {
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        result = sdf.parse(strDateTime);
      } catch (Exception e) {

      }
      return result;
    } else
      return new Date();
  }

  public static Calendar parseDateTime(String baseDate) {
    if (baseDate != null && !baseDate.equals("")) {
      SimpleDateFormat sdf = null;
      if (baseDate.length() > 10)
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      else
        sdf = new SimpleDateFormat("yyyy-MM-dd");
      try {
        baseDate = sdf.format(sdf.parse(baseDate));
      } catch (ParseException e) {
        logger.error(e.getMessage(),e);
      }
      Calendar cal = null;
      cal = new GregorianCalendar();
      int yy = Integer.parseInt(baseDate.substring(0, 4));
      int mm = Integer.parseInt(baseDate.substring(5, 7)) - 1;
      int dd = Integer.parseInt(baseDate.substring(8, 10));
      int hh = 0;
      int mi = 0;
      int ss = 0;
      if (baseDate.length() > 12) {
        hh = Integer.parseInt(baseDate.substring(11, 13));
        mi = Integer.parseInt(baseDate.substring(14, 16));
        ss = Integer.parseInt(baseDate.substring(17, 19));
      }
      cal.set(yy, mm, dd, hh, mi, ss);
      cal.getTime();
      return cal;
    } else
      return null;
  }

  public static Calendar parseDateTimeByFormat(String baseDate, String format) {
    if (baseDate != null && !baseDate.equals("")) {
      SimpleDateFormat sdf = null;
      sdf = new SimpleDateFormat(format);
      Calendar cal = null;
      cal = new GregorianCalendar();
      try {
        cal.setTime(sdf.parse(baseDate));
      } catch (ParseException e) {
        logger.error(e.getMessage(),e);
      }
      return cal;
    } else
      return null;
  }

  /**
   * **************************************
   * 功能：  获取当前日期的星期几
   *
   * @return 返回星期几  1(星期日)、2(星期一)，依次类推
   * <p/>
   * **************************************
   */
  public static int getWeekDay() {
    return getDate(null, WEEKDAY);
  }

  /**
   * **************************************
   * 功能：  获取给定日期的星期
   *
   * @param strDate:给定的日期
   * @return 返回星期几  1(星期日)、2(星期一)，依次类推
   * <p/>
   * **************************************
   */
  public static int getWeekDay(String strDate) {
    return getDate(strDate, WEEKDAY);
  }

  /**
   * **************************************
   * 功能：  获取给当前日期的星期几名称
   *
   * @return 返回星期几
   * <p/>
   * **************************************
   */
  public static String getWeekDayName() {
    return getWeekDayName(null, 0);
  }

  /**
   * **************************************
   * 功能：  获取给定日期的星期几名称
   *
   * @param strDate:给定的日期
   * @return 返回星期几
   * <p/>
   * **************************************
   */
  public static String getWeekDayName(String strDate) {
    return getWeekDayName(strDate, 0);
  }

  public static String getWeekDayName(String strDate, int type) {

    String mName[] = {
        "日", "一", "二", "三", "四", "五", "六"
    };
    int iWeek = 0;
    if (strDate == null || strDate.equals(""))
      iWeek = getWeekDay();
    else
      iWeek = getWeekDay(strDate);
    iWeek = iWeek - 1;
    if (type == 0)
      return "星期" + mName[iWeek];
    else
      return "周" + mName[iWeek];

  }

  /**
   * **************************************
   * 功能：  获取给当前日期的年份
   * **************************************
   */
  public static int getYear() {
    return getDate(null, YEAR);
  }

  /**
   * **************************************
   * 功能：  获取给当前日期的月份
   * **************************************
   */
  public static int getMonth() {
    return getDate(null, MONTH) + 1;
  }

  /**
   * **************************************
   * 功能：  获取给当前日期的天
   * **************************************
   */
  public static int getDay() {
    return getDate(null, DATE);
  }

  /**
   * **************************************
   * 功能：  获取给定日期的年
   *
   * @param strDate:给定的日期
   * @return 返回年份
   * **************************************
   */
  public static int getYear(String strDate) {
    return getDate(strDate, YEAR);
  }

  /**
   * **************************************
   * 功能：  获取给定日期的月
   *
   * @param strDate:给定的日期
   * @return 返回月份
   * **************************************
   */
  public static int getMonth(String strDate) {
    return getDate(strDate, MONTH) + 1;
  }

  /**
   * **************************************
   * 功能：  获取给定日期的天
   *
   * @param strDate:给定的日期
   * @return 返回天
   * <p/>
   * **************************************
   */
  public static int getDay(String strDate) {
    return getDate(strDate, DATE);
  }

  private static int getDate(String strDate, int iType) {
    Calendar cal = null;
    if (strDate == null || strDate.equals("")) {
      cal = Calendar.getInstance();
    } else {
      cal = parseDateTime(strDate);
    }
    if (iType == YEAR || iType == MONTH || iType == DATE || iType == HOUR || iType == MINUTE || iType == SECOND || iType == WEEKDAY)
      return cal.get(iType);
    else
      return -1;
  }

  private static void p(Object str) {
    System.out.println(str);
  }


  /**
   * **************************************
   * 功能：  改变给定时间的年、月、日、时、分、秒 其中一项
   *
   * @param strDate:要改变的时间
   * @param iCount:改变度。                          为整数
   * @param iType:要改变的类型(年月日时分秒)                 参数已经指定，如：年、DateUtil.YEAR
   * @param iType:0(返回天数)、1(返回小时)、2(返回分钟)、3(返回秒)
   * @return String   改变之后的时间
   * <p/>
   * **************************************
   */
  public static String DateAdd(String strDate, int iCount, int iType) {
    if (strDate != null) {
      strDate = strDate.trim();
      Calendar Cal = parseDateTime(strDate);
      int pType = 0;
      if (iType == YEAR || iType == MONTH || iType == DATE || iType == HOUR || iType == MINUTE || iType == SECOND)
        pType = iType;
      Cal.add(pType, iCount);
      SimpleDateFormat sdf = null;
      if (strDate.length() < 11)
        sdf = new SimpleDateFormat("yyyy-MM-dd");
      else
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String sDate = sdf.format(Cal.getTime());
      return sDate;
    } else {
      return null;
    }
  }

  public static int getPmorAm(Date date) {
    try {
      Calendar t_Calendar = Calendar.getInstance();
      t_Calendar.setTime(date);
      return t_Calendar.get(Calendar.AM_PM);
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
    }

    return 3;
  }

  public static String getPmorAmStr(Date date) {
    try {
      Calendar t_Calendar = Calendar.getInstance();
      t_Calendar.setTime(date);
      int val = t_Calendar.get(Calendar.AM_PM);
      if (val == 0) {
        return "AM";
      } else if (val == 1) {
        return "PM";
      }
    } catch (Exception e) {
      logger.error(e.getMessage(),e);
    }

    return "error";
  }

  public static String DateAddByFormat(String strDate, int iCount, int iType, String format) {
    if (strDate != null) {
      strDate = strDate.trim();
      Calendar Cal = parseDateTimeByFormat(strDate, format);
      int pType = 0;
      if (iType == YEAR || iType == MONTH || iType == DATE || iType == HOUR || iType == MINUTE || iType == SECOND)
        pType = iType;
      Cal.add(pType, iCount);
      SimpleDateFormat sdf = null;
      sdf = new SimpleDateFormat(format);
      String sDate = sdf.format(Cal.getTime());
      return sDate;
    } else {
      return null;
    }
  }

  /**
   * **************************************
   * 功能: 获取一段时间内的天数、小时、分钟或秒
   *
   * @param strDateBegin:开始时间
   * @param strDateEnd:结束时间
   * @param iType:0(返回天数)、1(返回小时)、2(返回分钟)、3(返回秒)
   * @return String   天数、小时、分钟 或者 秒
   * <p/>
   * **************************************
   */
  public static int DateDiff(String strDateBegin, String strDateEnd, int iType) {
    Calendar calBegin = parseDateTime(strDateBegin);
    Calendar calEnd = parseDateTime(strDateEnd);
    long lBegin = calBegin.getTimeInMillis();
    long lEnd = calEnd.getTimeInMillis();
    int ss = (int) ((lBegin - lEnd) / 1000L);
    int min = ss / 60;
    int hour = min / 60;
    int day = hour / 24;
    if (iType == 0)
      return day;
    if (iType == 1)
      return hour;
    if (iType == 2)
      return min;
    if (iType == 3)
      return ss;
    else
      return -1;
  }

  /**
   * **************************************
   *
   * @return boolean
   * @throws ParseException **************************************
   * @功能 判断某年是否为闰年
   */
  public static boolean isLeapYear(int yearNum) {
    boolean isLeep = false;
    /**判断是否为闰年，赋值给一标识符flag*/
    if ((yearNum % 4 == 0) && (yearNum % 100 != 0)) {
      isLeep = true;
    } else if (yearNum % 400 == 0) {
      isLeep = true;
    } else {
      isLeep = false;
    }
    return isLeep;
  }


  /**
   * **************************************
   *
   * @return interger
   * @throws ParseException **************************************
   * @功能 计算当前日期某年的第几周
   */
  public static int getWeekNumOfYear() {
    Calendar calendar = Calendar.getInstance();
    int iWeekNum = calendar.get(Calendar.WEEK_OF_YEAR);
    return iWeekNum;
  }

  /**
   * **************************************
   *
   * @return interger
   * @throws ParseException **************************************
   * @功能 计算指定日期某年的第几周
   */
  public static int getWeekNumOfYearDay(String strDate) throws ParseException {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date curDate = format.parse(strDate);
    calendar.setTime(curDate);
    int iWeekNum = calendar.get(Calendar.WEEK_OF_YEAR);
    return iWeekNum;
  }

  /**
   * **************************************
   *
   * @return interger
   * @throws ParseException **************************************
   * @功能 计算某年某周的开始日期
   */
  public static String getYearWeekFirstDay(int yearNum, int weekNum) throws ParseException {

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, yearNum);
    cal.set(Calendar.WEEK_OF_YEAR, weekNum);
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    //分别取得当前日期的年、月、日
    String tempYear = Integer.toString(yearNum);
    String tempMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
    String tempDay = Integer.toString(cal.get(Calendar.DATE));
    String tempDate = tempYear + "-" + tempMonth + "-" + tempDay;
    return SetDateFormat(tempDate, "yyyy-MM-dd");


  }

  /**
   * **************************************
   *
   * @return interger
   * @throws ParseException **************************************
   * @功能 计算某年某周的结束日期
   */
  public static String getYearWeekEndDay(int yearNum, int weekNum) throws ParseException {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, yearNum);
    cal.set(Calendar.WEEK_OF_YEAR, weekNum + 1);
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    //分别取得当前日期的年、月、日
    String tempYear = Integer.toString(yearNum);
    String tempMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);
    String tempDay = Integer.toString(cal.get(Calendar.DATE));
    String tempDate = tempYear + "-" + tempMonth + "-" + tempDay;
    return SetDateFormat(tempDate, "yyyy-MM-dd");
  }


  /**
   * **************************************
   *
   * @return interger
   * @throws ParseException **************************************
   * @功能 计算某年某月的开始日期
   */
  public static String getYearMonthFirstDay(int yearNum, int monthNum) throws ParseException {

    //分别取得当前日期的年、月、日
    String tempYear = Integer.toString(yearNum);
    String tempMonth = Integer.toString(monthNum);
    String tempDay = "1";
    String tempDate = tempYear + "-" + tempMonth + "-" + tempDay;
    return SetDateFormat(tempDate, "yyyy-MM-dd");

  }

  /**
   * **************************************
   *
   * @return interger
   * @throws ParseException **************************************
   * @功能 计算某年某月的结束日期
   */
  public static String getYearMonthEndDay(int yearNum, int monthNum) throws ParseException {


    //分别取得当前日期的年、月、日
    String tempYear = Integer.toString(yearNum);
    String tempMonth = Integer.toString(monthNum);
    String tempDay = "31";
    if (tempMonth.equals("1") || tempMonth.equals("3") || tempMonth.equals("5") || tempMonth.equals("7") || tempMonth.equals("8") || tempMonth.equals("10") || tempMonth.equals("12")) {
      tempDay = "31";
    }
    if (tempMonth.equals("4") || tempMonth.equals("6") || tempMonth.equals("9") || tempMonth.equals("11")) {
      tempDay = "30";
    }
    if (tempMonth.equals("2")) {
      if (isLeapYear(yearNum)) {
        tempDay = "29";
      } else {
        tempDay = "28";
      }
    }
    //System.out.println("tempDay:" + tempDay);
    String tempDate = tempYear + "-" + tempMonth + "-" + tempDay;
    return SetDateFormat(tempDate, "yyyy-MM-dd");

  }

  public static String getMonday(Date date, String format) {
    Calendar c = DateUtils.toCalendar(date);
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 1);
    if (StringUtils.isNotEmpty(format)) {
      return DateFormatUtils.format(c.getTime(), format);
    }
    return DateFormatUtils.format(c.getTime(), DEFAULT_DATE_FORMAT);
  }

  public static String getNextMonday(Date date, String format) {
    Calendar c = DateUtils.toCalendar(date);
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 1);
    c.add(Calendar.DATE, 7);
    if (StringUtils.isNotEmpty(format)) {
      return DateFormatUtils.format(c.getTime(), format);
    }
    return DateFormatUtils.format(c.getTime(), DEFAULT_DATE_FORMAT);
  }

  public static String getPreMonday(Date date, String format) {
    Calendar c = DateUtils.toCalendar(date);
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 1);
    c.add(Calendar.DATE, -7);
    if (StringUtils.isNotEmpty(format)) {
      return DateFormatUtils.format(c.getTime(), format);
    }
    return DateFormatUtils.format(c.getTime(), DEFAULT_DATE_FORMAT);
  }

  public static List<Date> getWeekDays(Date date) {
    List<Date> list = new LinkedList<Date>();
    Calendar c = DateUtils.toCalendar(date);
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 1);
    list.add(c.getTime()); // 周一
    for (int i = 1; i < 7; i++) {
      c.add(Calendar.DATE, 1);
      list.add(c.getTime());
    }
    return list;
  }

  /**
   * 得到本周周一
   *
   * @return yyyy-MM-dd
   */
  public static String getMondayOfThisWeek(String format) {
    Calendar c = Calendar.getInstance();
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 1);
    if (StringUtils.isNotEmpty(format)) {
      return DateFormatUtils.format(c.getTime(), format);
    }
    return DateFormatUtils.format(c.getTime(), DEFAULT_DATE_FORMAT);
  }

  public static String getSunday(Date date, String format) {
    Calendar c = DateUtils.toCalendar(date);
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 7);
    if (StringUtils.isNotEmpty(format)) {
      return DateFormatUtils.format(c.getTime(), format);
    }
    return DateFormatUtils.format(c.getTime(), DEFAULT_DATE_FORMAT);
  }

  public static String getPreSunday(Date date, String format) {
    Calendar c = DateUtils.toCalendar(date);
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 7);
    c.add(Calendar.DATE, -7);
    if (StringUtils.isNotEmpty(format)) {
      return DateFormatUtils.format(c.getTime(), format);
    }
    return DateFormatUtils.format(c.getTime(), DEFAULT_DATE_FORMAT);
  }

  public static String getNextSunday(Date date, String format) {
    Calendar c = DateUtils.toCalendar(date);
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 7);
    c.add(Calendar.DATE, 7);
    if (StringUtils.isNotEmpty(format)) {
      return DateFormatUtils.format(c.getTime(), format);
    }
    return DateFormatUtils.format(c.getTime(), DEFAULT_DATE_FORMAT);
  }

  /**
   * 得到本周周日
   *
   * @return yyyy-MM-dd
   */
  public static String getSundayOfThisWeek(String format) {
    Calendar c = Calendar.getInstance();
    int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
    if (dayofweek == 0)
      dayofweek = 7;
    c.add(Calendar.DATE, -dayofweek + 7);
    if (StringUtils.isNotEmpty(format)) {
      return DateFormatUtils.format(c.getTime(), format);
    }
    return DateFormatUtils.format(c.getTime(), DEFAULT_DATE_FORMAT);
  }

  /**
   * 功能：时间比较
   *
   * @param s1    第一个时间 格式：2008-01-12 12:12:12
   * @param s2    第二个时间
   * @param iType 比较的精确度(例如以分钟为基础进行比较，则此参数为DateUtil.MINUTE)
   * @return 第一个时间大返回1，第二个时间大返回2，相等返回0，错误参数返回-1
   */
  public static int comparedDate(String s1, String s2, int iType) {
    int day = 0;
    if (s1 == null || s2 == null || s1.equals("") || s2.equals(""))
      return -1;
    SimpleDateFormat sf = null;
    if (iType == YEAR)
      sf = new SimpleDateFormat("yyyy");
    else if (iType == MONTH)
      sf = new SimpleDateFormat("yyyy-MM");
    else if (iType == DATE)
      sf = new SimpleDateFormat("yyyy-MM-dd");
    else if (iType == HOUR)
      sf = new SimpleDateFormat("yyyy-MM-dd HH");
    else if (iType == MINUTE)
      sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    else if (iType == SECOND)
      sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    else
      sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date first = new Date();
    Date second = new Date();
    try {
      first = sf.parse(s1);
      second = sf.parse(s2);
      p(first);
      p(second);
      day = (int) ((second.getTime() - first.getTime()) / 1000);
    } catch (ParseException e) {
      logger.error(e.getMessage(),e);
    }
    if (day > 0) {
      return 2;
    } else if (day < 0) {
      return 1;
    } else
      return 0;
  }

  private static final String[] BIGNUMBERS = {"零", "壹", "贰", "叁", "肆", "伍",
      "陆", "柒", "捌", "玖"};

  private static final String[] NUMBERS = {"〇", "一", "二", "三", "四", "五",
      "六", "七", "八", "九"};

  /**
   * 转换数字为大写
   */
  private static String convertBigNum(String str) {
    return BIGNUMBERS[Integer.valueOf(str)];
  }

  /**
   * 转换数字为大写
   */
  private static String convertNum(String str) {
    return NUMBERS[Integer.valueOf(str)];
  }

  /**
   * 分别得到年月日的小写 默认分割符 "-"
   */
  public static String getSplitDateStr(String str, int unit) {
    // unit是单位 0=年 1=月 2日
    String[] DateStr = str.split("-");
    if (unit > DateStr.length)
      unit = 0;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < DateStr[unit].length(); i++) {

      if ((unit == 1 || unit == 2) && Integer.valueOf(DateStr[unit]) > 9) {
        sb.append(convertNum(DateStr[unit].substring(0, 1)))
            .append("十").append(
            convertNum(DateStr[unit].substring(1, 2)));
        break;
      } else {
        sb.append(convertNum(DateStr[unit].substring(i, i + 1)));
      }
    }
    if (unit == 1 || unit == 2) {
      return sb.toString().replaceAll("^一", "").replace("○", "");
    }
    return sb.toString();

  }


  public static synchronized String toChinese(String str) {
    StringBuffer sb = new StringBuffer();
    sb.append(getSplitDateStr(str, 0)).append("年").append(
        getSplitDateStr(str, 1)).append("月").append(
        getSplitDateStr(str, 2)).append("日");
    return sb.toString();
  }

  public static synchronized String toChinese(Date date) {
    return toChinese(FormatDate(date, "yyyy-MM-dd"));
  }

  public static Date getMaxDate(){
    try {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("9999-12-31 23:59:59");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) throws ParseException {
    Date d = new SimpleDateFormat("yyyy-MM-dd").parse("2015-12-31");
    System.out.println(getPreMonday(d, "yyyy-MM-dd"));
    System.out.println(getPreSunday(d, "yyyy-MM-dd"));
    System.out.println(getNextMonday(d, "yyyy-MM-dd"));
    System.out.println(getNextSunday(d, "yyyy-MM-dd"));
  }
}
