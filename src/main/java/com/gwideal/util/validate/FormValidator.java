package com.gwideal.util.validate;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FormValidator {

  /**
   * @param args
   * 校验类
   */
  // 日志打印
  private final static Logger logger = LogManager.getLogger();

  // 普通枚举
  public enum ValidateType {
    // 姓名，身份证号，手机号， 电话号码 ，邮箱，生日，区县，街道，居委,区县街道，街道居委
    Name, IDcard, Mobile, Phone, Email, Birthday, DS, SJ
  }

  protected final static Map<String, Object> result = new HashMap<>();


  public static boolean validate(String value, ValidateType type) {

    String regex = null;
    Pattern pattern = null;
    Matcher matcher = null;
    boolean result = false;
    if (StringUtils.isEmpty(value)) {
      return result;
    }
    switch (type) {
      case Name:// 名字验证
        regex = "^[\u4e00-\u9fa5]+$||[A-Za-z]+$";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(value);
        if (matcher.matches()) {
          logger.debug("进入名字验证");
          result = true;
        }
        return result;
      case IDcard:
        regex = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|[x,X])$";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(value);
        if (matcher.matches()) {
          logger.debug("进入身份证号码验证且格式化");
          result = true;
        }
        return result;
      case Mobile:
        regex = "^1[3|4|5|7|8]\\d{9}$";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(value);
        if (matcher.matches()) {
          logger.debug("进入手机号码验证");
          result = true;
        }
        return result;
      case Phone:
        if (value.length() > 9) {
          regex = "^[0][1-9]{2,3}-[0-9]{5,10}$";
          pattern = Pattern.compile(regex);
          matcher = pattern.matcher(value);
          if (matcher.matches()) {
            logger.debug("进入电话号码验证");
            result = true;
          }
        } else {
          regex = "^[1-9]{1}[0-9]{5,8}$";
          pattern = Pattern.compile(regex);
          matcher = pattern.matcher(value);
          if (matcher.matches()) {
            logger.debug("进入电话号码验证");
            result = true;
          }
        }
        return result;
      case Email:
        regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(value);
        if (matcher.matches()) {
          logger.debug("进入Email验证");
          result = true;
        }
        return result;
      case Birthday:
        regex = "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(value);
        // 截取年份进行判断 year
        String year = value.substring(0, 4);
        // 判断传进来的年份是否大于1900
        if (matcher.matches() && Integer.parseInt(year) > 1900) {
          logger.debug("进入生日验证且格式化");
          result = true;
        }
        return result;
      default:
        break;
    }
    return result;
  }

  // DS:区县，街道。SJ:街道，居委
  public Map<String, Object> validateDSSJ(String v1, String v2,
                                          ValidateType type) {
    Map<String, Object> result = new HashMap<>();
    switch (type) {
      case DS:// 区县、街道验证
        List<Map<String, Object>> DSdistrictlist =
            jdbcTemplate.queryForList("SELECT region_name,region_code FROM t_biz_official_region WHERE region_name LIKE ? AND t_biz_official_region.region_type = ?",
                "%" + v1 + "%", "district");
        List<Map<String, Object>> DSstreetlist =
            jdbcTemplate.queryForList("SELECT region_name,region_code FROM t_biz_official_region WHERE region_name LIKE ? AND t_biz_official_region.region_type = ?",
                "%" + v2 + "%", "street");
        if (DSdistrictlist.size() == 0 || DSstreetlist.size() == 0) {
          result.put("districtValue", v1);
          result.put("streetValue", v2);
          logger.debug("DS无效");
          result.put("result", false);
          return result;
        } else {
          String DristrictSub = (String) DSdistrictlist.get(0).get(
              "region_code");
          String StreetSub = (String) DSstreetlist.get(0).get(
              "region_code");
          String Dristrictsubstr = DristrictSub.substring(4, 6);// 区县
          String Streetsubstr = StreetSub.substring(0, 2);// 街道
          if (Dristrictsubstr.equals(Streetsubstr)) {
            logger.debug("进入DS验证");
            result.put("districtValue",
                DSdistrictlist.get(0).get("region_name"));
            result.put("streetValue",
                DSstreetlist.get(0).get("region_name"));
            result.put("result", true);
            return result;
          } else {
            result.put("districtValue",
                DSdistrictlist.get(0).get("region_name"));
            result.put("streetValue",
                DSstreetlist.get(0).get("region_name"));
            result.put("result", false);
            return result;
          }
        }

      case SJ:// 街道、居委验证
        List<Map<String, Object>> SJstreetlist =
            jdbcTemplate.queryForList("SELECT region_name,region_code FROM t_biz_official_region WHERE region_name LIKE ? AND t_biz_official_region.region_type = ?",
                "%" + v1 + "%", "street");
        List<Map<String, Object>> SJjuweilist =
            jdbcTemplate.queryForList("SELECT region_name,region_code FROM t_biz_official_region WHERE region_name LIKE ? AND t_biz_official_region.region_type = ?",
                "%" + v2 + "%", "jwh");
        if (SJjuweilist.size() == 0 || SJstreetlist.size() == 0) {
          result.put("streetValue", v1);
          result.put("jwhValue", v2);
          logger.debug("SJ无效！");
          result.put("result", false);
          return result;
        } else {
          String SJStreetSub = (String) SJstreetlist.get(0).get(
              "region_code");
          String SJjuweiSub = (String) SJjuweilist.get(0).get(
              "region_code");
          String SJjuweisubstr = SJjuweiSub.substring(0, 5);// 居委
          if (SJStreetSub.equals(SJjuweisubstr)) {
            logger.debug("进入SJ验证");
            result.put("streetValue",
                SJstreetlist.get(0).get("region_name"));
            result.put("jwhValue", SJjuweilist.get(0)
                .get("region_name"));
            result.put("result", true);
            return result;
          } else {
            result.put("streetValue",
                SJstreetlist.get(0).get("region_name"));
            result.put("jwhValue", SJjuweilist.get(0)
                .get("region_name"));
            result.put("result", false);
            return result;
          }
        }
      default:
        break;
    }
    result.put("result", false);
    return result;
  }

  // v1区县，v2街道，v3居委，同时验证
  public Map<String, Object> validateDSJ(String v1, String v2,
                                         String v3) {
    if (v1.length() < 2 || v2.length() < 2 || v3.length() < 3) {
      logger.debug("数据不完整！\n");
      result.put("result", false);
      return result;
    }
    List<Map<String, Object>> DSJdistrictlist =
        jdbcTemplate.queryForList("SELECT region_name,region_code FROM t_biz_official_region WHERE region_name LIKE ? AND t_biz_official_region.region_type = ?",
            "%" + v1 + "%", "district");
    List<Map<String, Object>> DSJstreetlist =
        jdbcTemplate.queryForList("SELECT region_name,region_code FROM t_biz_official_region WHERE region_name LIKE ? AND t_biz_official_region.region_type = ?",
            "%" + v2 + "%", "street");
    List<Map<String, Object>> DSJjuweilist =
        jdbcTemplate.queryForList("SELECT region_name,region_code FROM t_biz_official_region WHERE region_name LIKE ? AND t_biz_official_region.region_type = ?",
            "%" + v3 + "%", "jwh");
    if (DSJdistrictlist.size() == 0 || DSJstreetlist.size() == 0
        || DSJjuweilist.size() == 0) {
      result.put("districtValue", v1);
      result.put("streetValue", v2);
      result.put("jwhValue", v3);
      logger.debug("DSJ无效");
      result.put("result", false);
      return result;
    } else if (DSJdistrictlist.size() != 0 && DSJstreetlist.size() != 0
        && DSJjuweilist.size() != 0) {
      String DristrictSub = (String) DSJdistrictlist.get(0).get(
          "region_code");
      String StreetSub = (String) DSJstreetlist.get(0).get

          ("region_code");// 居委街道对比
      String JwhSub = (String) DSJjuweilist.get(0).get("region_code");
      String Dristrictsubstr = DristrictSub.substring(4, 6);// 区县
      String Streetsubstr = StreetSub.substring(0, 2);// 区县，街道对比
      String Juweisubstr = JwhSub.substring(0, 5);// 居委
      if (Dristrictsubstr.equals(Streetsubstr)
          && StreetSub.equals(Juweisubstr)) {
        logger.debug("进入DSJ验证");
        result.put("districtValue",
            DSJdistrictlist.get(0).get("region_name"));
        result.put("streetValue",
            DSJstreetlist.get(0).get("region_name"));
        result.put("jwhValue", DSJjuweilist.get(0).get

            ("region_name"));
        result.put("result", true);
        return result;
      } else {
        result.put("districtValue",
            DSJdistrictlist.get(0).get("region_name"));
        result.put("streetValue",
            DSJstreetlist.get(0).get("region_name"));
        result.put("jwhValue", DSJjuweilist.get(0).get

            ("region_name"));
        result.put("result", false);
        return result;
      }
    }
    result.put("result", false);
    return result;
  }

  @Autowired
  private JdbcTemplate jdbcTemplate;
}
