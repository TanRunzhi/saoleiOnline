package com.gwideal.util.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.List;

/**
 * Created by li_hongyu on 2015/07/23.
 */
public class FileHelper {

  private static final Logger log = LogManager.getLogger();

  /**
   * 新建目录
   *
   * @param folderPath String 如 c:/fqf
   * @return boolean
   */
  public static void newFolder (String folderPath) {
    try {
      File myFilePath = new File(folderPath);
      if ( !myFilePath.exists() ) {
        boolean r = myFilePath.mkdir();
        if ( r ) {
          log.info("folder to create success...");
        }
      }
    } catch ( Exception e ) {
      System.out.println("新建目录操作出错");
      e.printStackTrace();
    }
  }

  /**
   * 删除文件
   *
   * @param filePathAndName String 文件路径及名称 如c:/fqf.txt
   * @return boolean
   */
  public static void delFile (String filePathAndName) {
    try {
      String filePath = filePathAndName;
      filePath = filePath.toString();
      File myDelFile = new File(filePath);
      boolean r = myDelFile.delete();
      if ( r ) {
        log.info("folder to delete success...");
      }
    } catch ( Exception e ) {
      System.out.println("删除文件操作出错");
      e.printStackTrace();

    }

  }

  /**
   * 删除文件夹
   *
   * @param folderPath String 文件夹路径及名称 如c:/fqf
   * @return boolean
   */
  public static void delFolder (String folderPath) {
    try {
      delAllFile(folderPath); //删除完里面所有内容
      String filePath = folderPath;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      boolean r = myFilePath.delete(); //删除空文件夹
      if ( r ) {
        log.info("folder to delete success");
      }
    } catch ( Exception e ) {
      System.out.println("删除文件夹操作出错");
      e.printStackTrace();

    }

  }

  /**
   * 删除文件夹里面的所有文件
   *
   * @param path String 文件夹路径 如 c:/fqf
   */
  public static void delAllFile (String path) {
    File file = new File(path);
    if ( !file.exists() ) {
      return;
    }
    if ( !file.isDirectory() ) {
      return;
    }
    String[] tempList = file.list();
    File temp = null;
    if ( tempList != null && tempList.length > 0 )
      for ( int i = 0; i < tempList.length; i++ ) {
        if ( path.endsWith(File.separator) ) {
          temp = new File(path + tempList[i]);
        } else {
          temp = new File(path + File.separator + tempList[i]);
        }
        if ( temp.isFile() ) {
          boolean r = temp.delete();
        }
        if ( temp.isDirectory() ) {
          delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
          delFolder(path + "/" + tempList[i]);//再删除空文件夹
        }
      }
  }

  /**
   * 复制单个文件
   *
   * @param oldPath String 原文件路径 如：c:/fqf.txt
   * @param newPath String 复制后路径 如：f:/fqf.txt
   * @return boolean
   */
  public static void copyFile (String oldPath, String newPath) {
    try {
      int bytesum = 0;
      int byteread = 0;
      File oldfile = new File(oldPath);
      if ( oldfile.exists() ) { //文件存在时
        InputStream inStream = new FileInputStream(oldPath); //读入原文件
        FileOutputStream fs = new FileOutputStream(newPath);
        byte[] buffer = new byte[1444];
        int length;
        while ( (byteread = inStream.read(buffer)) != -1 ) {
          bytesum += byteread; //字节数 文件大小
          System.out.println(bytesum);
          fs.write(buffer, 0, byteread);
        }
        inStream.close();
        fs.close();
      }
    } catch ( Exception e ) {
      System.out.println("复制单个文件操作出错");
      e.printStackTrace();

    }

  }

  /**
   * 复制整个文件夹内容
   *
   * @param oldPath String 原文件路径 如：c:/fqf
   * @param newPath String 复制后路径 如：f:/fqf/ff
   * @return boolean
   */
  public static void copyFolder (String oldPath, String newPath) {
    boolean r = new File(newPath).mkdirs(); //如果文件夹不存在 则建立新文件夹
    if ( r ) {
      log.info("folder to create success");
    }
    File a = new File(oldPath);
    String[] file = a.list();
    File temp = null;
    if ( file != null && file.length > 0 )
      for ( String aFile : file ) {
        if ( oldPath.endsWith(File.separator) ) {
          temp = new File(oldPath + aFile);
        } else {
          temp = new File(oldPath + File.separator + aFile);
        }
        if ( temp.isFile() ) {
          try {
            FileInputStream input = new FileInputStream(temp);
            FileOutputStream output = new FileOutputStream(newPath + "/" +
                (temp.getName()).toString());
            byte[] b = new byte[1024 * 5];
            int len;
            while ( (len = input.read(b)) != -1 ) {
              output.write(b, 0, len);
            }
            output.flush();
            output.close();
            input.close();
          } catch ( Exception e ) {
            e.printStackTrace();
          }
        }
        if ( temp.isDirectory() ) {//如果是子文件夹
          copyFolder(oldPath + "/" + aFile, newPath + "/" + aFile);
        }
      }
  }

  /**
   * 移动文件到指定目录
   *
   * @param oldPath String 如：c:/fqf.txt
   * @param newPath String 如：d:/fqf.txt
   */
  public static void moveFile (String oldPath, String newPath) {
    copyFile(oldPath, newPath);
    delFile(oldPath);

  }

  /**
   * 移动文件到指定目录
   *
   * @param oldPath String 如：c:/fqf.txt
   * @param newPath String 如：d:/fqf.txt
   */
  public static void moveFolder (String oldPath, String newPath) {
    copyFolder(oldPath, newPath);
    delFolder(oldPath);

  }

  /**
   * 利用byte数组转换InputStream------->String <功能详细描述>
   *
   * @param in
   * @return
   */
  public static String input2Str (InputStream in, String encode) {
    StringBuilder sb = new StringBuilder(500);
    byte[] b = new byte[1024];
    int len = 0;
    try {
      if ( encode == null || "".equals(encode)) {
        encode = "utf-8";
      }
      while ( (len = in.read(b)) != -1 ) {
        sb.append(new String(b, 0, len, encode));
      }
      in.close();
      return sb.toString();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
    return "";

  }

  /**
   * 功能:压缩多个文件成一个zip文件
   *
   * @param srcfile：源文件列表
   * @param zipfile：压缩后的文件
   */
  public static void zipFiles (List<File> srcfile, File zipfile) {
    byte[] buf = new byte[1024];
    try {
      //ZipOutputStream类：完成文件或文件夹的压缩
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
      for ( File file : srcfile ) {
        if ( file == null ) {
          continue;
        }
        FileInputStream in = new FileInputStream(file);

        out.putNextEntry(new ZipEntry(file.getName()));
        int len;
        while ( (len = in.read(buf)) > 0 ) {
          out.write(buf, 0, len);
        }
        out.setEncoding("utf-8");
        out.closeEntry();
        in.close();
      }
      out.close();
      for ( File delFile : srcfile ) {
        delFile.delete();
      }
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

}
