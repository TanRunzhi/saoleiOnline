package com.gwideal.util.io;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Created by li_hongyu on 14-5-28.
 */
public class PropertiesReader {

  private final static String PROPERTIES_FILE_PATH = "config/resource.properties";

  /**
   * 此方法只支持读取src目录property文件
   *
   * @param key 要读取的配置键
   * @return String 配置值
   */
  public static String getPropertiesValue(String key) {
    Properties props = new Properties();
    try {
      props.load(PropertiesReader.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_PATH));
      return props.getProperty(key);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 拼接内容，参数为内容KEY+参数数组
   *
   * @param key  配置文件中的key
   * @param args 占位符替换内容的数组
   * @return String
   */
  public static String getValueWithPH(String key, String... args) {
    return MessageFormat.format(getPropertiesValue(key), (Object[]) args);
  }

  /**
   * 获取属性文件值
   *
   * @param path
   * @return
   */
  public static Properties getProperties(String path) {
    Properties props = new Properties();
    try {
      props.load(PropertiesReader.class.getClassLoader().getResourceAsStream(path));
      return props;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
