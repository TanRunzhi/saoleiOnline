package com.gwideal.core.word;

import com.gwideal.util.io.PropertiesReader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.UUID;

/**
 * ftl文件装载word
 * @Author: fan_jinliang
 * @Date: 2018/7/17 16:34
 */

public class WordHander {
  private static Configuration configuration = null;

  static{
    configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    configuration.setDefaultEncoding("utf-8");//设置默认的编码
  }


  public void createDoc(HttpServletResponse response,Map<String, Object> dataMap, String fileName, String tempName) throws Exception{
    //dataMap 要填入模本的数据文件
    //设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
    //这里我们的模板是放在template包下面
    configuration.setClassForTemplateLoading(this.getClass(), "/template");
    Template template=null;
    File f = new File(fileName);
    try {
      template = configuration.getTemplate(tempName);
      Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
      //数据装载入模板
      template.process(dataMap, w);
      w.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      response.setContentType("application/octet-stream;charset=utf-8");
      response.setHeader("Content-Disposition", "attachment;filename=" +
          new String(fileName.getBytes(PropertiesReader.getPropertiesValue("sys.file.getCode")), PropertiesReader.getPropertiesValue("sys.file.code")));
      // 客户端不缓存
      response.addHeader("Pargam", "no-cache");
      response.addHeader("Cache-Control", "no-cache");
      OutputStream out = response.getOutputStream();
      InputStream fin = new FileInputStream(f);
      byte[] buffer = new byte[512];

      if (f != null) {
        int bytesToRead = -1;
        // 通过循环将读入的Word文件的内容输出到浏览器中
        while ((bytesToRead = fin.read(buffer)) != -1) {
          out.write(buffer, 0, bytesToRead);
        }
      }
      fin.close();
      out.flush();
      out.close();
      f.delete();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public File createDoc(String tempFilePath,Map<String,Object> dataMap, String fileName,String tempName) throws Exception{
    //dataMap 要填入模本的数据文件
    //设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
    //这里我们的模板是放在template包下面
    configuration.setClassForTemplateLoading(this.getClass(), "/template");
    Template template=null;

    String uuid = UUID.randomUUID().toString();
    String wordFileName = tempFilePath +"/"+ uuid + ".doc";
    File f = new File(wordFileName);
    try {
      template = configuration.getTemplate(tempName);
      Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
      template.process(dataMap, w);
      w.flush();
      w.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    File newFile = new File(fileName);
    new File(wordFileName).renameTo(newFile);
    return newFile;
  }
}
