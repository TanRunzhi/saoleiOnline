package com.gwideal.core.util;

import com.gwideal.util.io.PropertiesReader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: fan_jinliang
 * @Date: 2018/7/17 16:34
 */

public class WordHander {
  private static Configuration configuration = null;

  private final static String tempFilePath = "";

  static {
    configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    //设置默认的编码
    configuration.setDefaultEncoding("utf-8");
  }

  public static void downLoadDoc(Map<String, Object> dataMap, String fileName, String templateName, HttpServletResponse response) {
    //dataMap 要填入模本的数据文件
    //设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
    //这里我们的模板是放在template包下面

    try {
      response.setContentType("application/octet-stream;charset=utf-8");
      response.setHeader("Content-Disposition", "attachment;filename=" +
          new String(fileName.getBytes(PropertiesReader.getPropertiesValue("sys.file.getCode")), PropertiesReader.getPropertiesValue("sys.file.code")));
      // 客户端不缓存
      response.addHeader("Pargam", "no-cache");
      response.addHeader("Cache-Control", "no-cache");
      OutputStream out = response.getOutputStream();
      configuration.setClassForTemplateLoading(WordHander.class, "/template");
      Template template = configuration.getTemplate("/" + templateName);
      Writer w = new OutputStreamWriter(out, "utf-8");
      template.process(dataMap, w);
      w.flush();
      w.close();
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static File createDoc(Map<String, Object> dataMap, String fileName, String tempName) {
    //dataMap 要填入模本的数据文件
    //设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
    //这里我们的模板是放在template包下面
    configuration.setClassForTemplateLoading(WordHander.class, "/template");
    Template template = null;

    String uuid = UUID.randomUUID().toString();
    String wordFileName = tempFilePath + uuid + ".doc";
    File f = new File(wordFileName);
    try {
      template = configuration.getTemplate("/" + tempName);
      Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
      template.process(dataMap, w);
      w.flush();
      w.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    File newFile = new File(tempFilePath + fileName);
    f.renameTo(newFile);
    return newFile;
  }


}
