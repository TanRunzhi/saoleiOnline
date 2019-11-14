package com.gwideal.core.word;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 使用poi装载word
 * Created by  tan_runzhi  on 2018/12/14.
 */
public class DocUtils {

  /**
   * 生成文档  (doc)
   * @param modelPath  读取的模版文件路径( 模版中需要用占位符填写需要填写的字段 )
   * @param createPath  生成文件的路径
   * @param map         Map<占位符名称，实际应该写的名称>
   * @return  File 最终生成的文件对象
   * */
  public static File createDoc(String modelPath, String createPath, Map<String, String> map){
    File file = null;
    InputStream is = null;
    FileOutputStream fos = null;
    try {
      //获取docx解析对象
      is = new FileInputStream(modelPath);
      HWPFDocument hdt = new HWPFDocument(is);
      Range range = hdt.getRange();
      //替换读取到的word模板内容的指定字段
      for (Map.Entry<String, String> entry:map.entrySet()) {
        range.replaceText(entry.getKey(),entry.getValue());
      }
      //生成新的word文档
      file = new File(createPath);
      fos = new FileOutputStream(file);
      hdt.write(fos);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        }catch (IOException e){
          e.printStackTrace();
        }
      }
      if (fos != null) {
        try {
          fos.close();
        }catch (IOException e){
          e.printStackTrace();
        }
      }
      return file;
    }
  }


  /**
   * 生成文档  (docx)
   * @param modelPath  读取的模版文件路径( 模版中需要用占位符填写需要填写的字段 )
   * @param createPath  生成文件的路径
   * @param map         Map<占位符名称，实际应该写的名称>
   * @return  File 最终生成的文件对象
   * */
  public static File createDocx(String modelPath, String createPath, Map<String, String> map){
    File file = null;
    InputStream is = null;
    FileOutputStream fos = null;
    try {
      //获取docx解析对象
      is = new FileInputStream(modelPath);
      XWPFDocument document = new XWPFDocument(is);
      Iterator<XWPFParagraph> itPara = document.getParagraphsIterator();
      //替换读取到的word模板内容的指定字段
      while (itPara.hasNext()) {
        XWPFParagraph paragraph = itPara.next();
        List<XWPFRun> runs = paragraph.getRuns();
        String fixString = "";
        for (XWPFRun run : runs) {
          String oneparaString = run.getText(run.getTextPosition());
          if (StringUtils.isBlank(oneparaString)) {
            continue;
          }
          if(oneparaString.indexOf("${") > -1 && oneparaString.indexOf("}") > -1){
            for (Map.Entry<String, String> entry : map.entrySet()) {
              oneparaString = oneparaString.replace(entry.getKey(), entry.getValue());
            }
            run.setText(oneparaString, 0);
          }else if(oneparaString.indexOf("${") > -1 && oneparaString.indexOf("}") == -1){
            fixString += oneparaString;
            run.setText("", 0);
          }else if(oneparaString.indexOf("${") == -1 && oneparaString.indexOf("}") == -1 && StringUtils.isNotEmpty(fixString)){
            fixString += oneparaString;
            run.setText("", 0);
          }else if(oneparaString.indexOf("${") == -1 && oneparaString.indexOf("}") > -1){
            fixString += oneparaString;
            for (Map.Entry<String, String> entry : map.entrySet()) {
              fixString = fixString.replace(entry.getKey(), entry.getValue());
            }
            run.setText(fixString, 0);
            fixString = "";
          }
        }
      }
      //生成新的word文档
      file = new File(createPath);
      fos = new FileOutputStream(file);
      document.write(fos);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        }catch (IOException e){
          e.printStackTrace();
        }
      }
      if (fos != null) {
        try {
          fos.close();
        }catch (IOException e){
          e.printStackTrace();
        }
      }
      return file;
    }
  }

  public static boolean addWaterMark(String wordPath, String waterMarkPath){
   /* WordInsertWaterMark wordObj = WordInsertWaterMark.getInstance();
    return wordObj.addWaterMark(wordPath, waterMarkPath);*/
   return false;
  }

  public static void main(String[] args) {

  }
}