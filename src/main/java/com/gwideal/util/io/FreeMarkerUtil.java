package com.gwideal.util.io;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;


public class FreeMarkerUtil {


  /**
   * freeMarker打印时，特殊符号处理
   * @param s
   * @return
   */
  public static String formatPrintStr(String s){
    return StringUtils.isEmpty(s) ? "" : s.replaceAll("<","《").replaceAll(">", "》");
  }

  /**
   * 打印wps
   *
   * @param dataMap      要填充的数据集合
   * @param templatePath 模板相对路径+模板名（相对于webapps/的路径，例：WEB-INF/template/EnvelopCatalog/QuanYinMuLu.ftl）
   * @param fileName     导出的文件名
   * @param request
   * @param response
   */
  public static void print(Map<String, Object> dataMap, String templatePath, String fileName, HttpServletRequest request, HttpServletResponse response) {
    try {
      String templateFolderPath = templatePath.indexOf("/") >= 0 ? (templatePath.substring(0, templatePath.lastIndexOf("/"))) : "";
      String templateFileName = templatePath.indexOf("/") >= 0 ? (templatePath.substring(templatePath.lastIndexOf("/") + 1)) : templatePath;

      String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
      rootPath = new File(rootPath).getParentFile().getParent() + "/" + templateFolderPath;

      Configuration configuration = new Configuration(Configuration.getVersion());
      configuration.setDefaultEncoding("utf-8");
      configuration.setDirectoryForTemplateLoading(new File(rootPath));
      Template template = configuration.getTemplate(templateFileName, "utf-8");

      String agent = request.getHeader("User-Agent");
      boolean isMSIE = (agent != null && (agent.indexOf("MSIE") != -1 || agent.indexOf("Trident") != -1));
      fileName += ".doc";
      if (isMSIE) {
        fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
      } else {
        fileName = new String(fileName.getBytes("utf-8"), "ISO8859-1");
      }
      ServletOutputStream os = null;
      try {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.ms-works");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        os = response.getOutputStream();
        Writer w = new OutputStreamWriter(os, "utf-8");
        template.process(dataMap, w);
        w.close();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (os != null) {
          os.close();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 生成文档
   * @param templatePath  读取的模版文件路径（相对于webapps/的路径，例：WEB-INF/template/EnvelopCatalog/QuanYinMuLu.ftl）
   * @param createPath  生成文件的路径
   * @param dataMap     Map<占位符名称，实际应该写的名称>
   * @return  File 最终生成的文件对象
   * */
  public static File getDoc(Map<String, Object> dataMap, String templatePath, String createPath) {
    try {
      String templateFolderPath = templatePath.indexOf("/") >= 0 ? (templatePath.substring(0, templatePath.lastIndexOf("/"))) : "";
      String templateFileName = templatePath.indexOf("/") >= 0 ? (templatePath.substring(templatePath.lastIndexOf("/") + 1)) : templatePath;

      String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getFile();
      rootPath = new File(rootPath).getParentFile().getParent() + "/" + templateFolderPath;

      Configuration configuration = new Configuration(Configuration.getVersion());
      configuration.setDefaultEncoding("utf-8");
      configuration.setDirectoryForTemplateLoading(new File(rootPath));
      Template template = configuration.getTemplate(templateFileName, "utf-8");
      File file = null;
      Writer w =null;
      FileOutputStream fos=null;
      try {
        file = new File(createPath);
        // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
        fos=new FileOutputStream(file);
        w = new OutputStreamWriter(fos, "utf-8");
        template.process(dataMap, w);
        w.flush();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (w != null) {
          w.close();
        }
        if (fos != null) {
          fos.close();
        }
      }
      return file;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] a) {
    String b = "aaa/bb.xml";
    System.out.println(b.indexOf("/") >= 0 ? (b.substring(0, b.lastIndexOf("/"))) : "");
    System.out.println(b.indexOf("/") >= 0 ? (b.substring(b.lastIndexOf("/") + 1, b.length())) : b);

  }
}
