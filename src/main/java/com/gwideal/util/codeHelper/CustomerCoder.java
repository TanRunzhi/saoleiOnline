package com.gwideal.util.codeHelper;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;

/**
 * Created by li_hongyu on 14-6-10.
 */
@Service
public class CustomerCoder {

  private static final String saltStr = "*()_HJLFD()FSjofdsaf80d9a13";
  public static final String saltStr1 = "9b874ac2344040";
  private static final String saltStr2 = "46b845e1ac6240";
  private static final Charset encoding = Charset.forName("UTF-8");

  /**
   * 补位0
   *
   * @param num 一共几位
   * @param str 原字符串
   * @return 新字符串
   */
  public static String convertInt2String(int num, String str) {
    char[] ary1 = str.toCharArray();
    StringBuilder str0 = new StringBuilder();
    for (int i = 0; i < num; i++) {
      str0.append("0");
    }
    char[] ary2 = str0.toString().toCharArray();
    System.arraycopy(ary1, 0, ary2, ary2.length - ary1.length, ary1.length);
    return new String(ary2);
  }

  /**
   * MD5编码
   *
   * @param str 要加密的字符串
   * @return 加密后的字符串
   */
  public String encoderMD5(String str) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
      String s = str + saltStr;
      md.update(s.getBytes(encoding));
      return new BigInteger(1, md.digest()).toString(16);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return str;
  }

  /**
   * 序列号生成
   * 公式： 前缀+n位序列编号
   *
   * @param suffix  前缀
   * @param current 当前数值
   * @param pattern 位数
   * @return 自动生成的编号
   */
  public static String autoSeqGenerator(String suffix, String current, Integer pattern) {
    suffix = suffix + Calendar.getInstance().get(Calendar.YEAR);
    if (StringUtils.isEmpty(current) || !current.contains(suffix))
      return suffix + convertInt2String(pattern, 1 + "");
    else {
      int seq = "".equals(current.substring(current.indexOf(suffix) + suffix.length() + 1))
          ? 0 : Integer.parseInt(current.substring(current.indexOf(suffix) + +suffix.length() + 1));
      return suffix + convertInt2String(pattern, (seq + 1) + "");
    }
  }

  /**
   * 流水字符串
   *
   * @param suffix  前缀
   * @param current 当前值
   * @param pattern 流水位数
   * @return 流水字符串
   */
  public static String autoNumberTemplate(String suffix, String current, Integer pattern) {
    String beforePlaceHolder = suffix.substring(0, suffix.indexOf("%"));
    String afterPlaceHolder = suffix.substring(suffix.indexOf("%") + 1);
    if (StringUtils.isEmpty(current))
      return beforePlaceHolder + convertInt2String(pattern, 1 + "") + afterPlaceHolder;
    else {
      Integer seq = Integer.parseInt(current.replace(beforePlaceHolder, "").replace(afterPlaceHolder, "").replace("0", ""));
      return beforePlaceHolder + convertInt2String(pattern, (seq + 1) + "") + afterPlaceHolder;
    }
  }

  public static String getBASE64(String s) {
    if (s == null) return null;
    else{
      s=saltStr1+s+saltStr2;
    }
    return Base64.getEncoder().encodeToString(s.getBytes(encoding));
  }

  // 将 BASE64 编码的字符串 s 进行解码
  public static String getFromBASE64(String s) {
    if (s == null) return null;
    try {
      byte[] b = Base64.getDecoder().decode(s);
      String ss = new String(b, encoding);
      ss = ss.substring(14);
      ss = ss.substring(0,ss.length()-14);
      return ss;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 加密函数
   * @param content 待加密的内容
   * @param strKey 函数
   * @return
   * @throws Exception
   */
  public static byte[] enCrypt(String content,String strKey) throws Exception{
    KeyGenerator keygen;
    SecretKey desKey;
    Cipher c;
    byte[] cByte;
    String str = content;

    keygen = KeyGenerator.getInstance("AES");
    keygen.init(128, new SecureRandom(strKey.getBytes()));

    desKey = keygen.generateKey();
    c = Cipher.getInstance("AES");

    c.init(Cipher.ENCRYPT_MODE, desKey);

    cByte = c.doFinal(str.getBytes("UTF-8"));

    return cByte;
  }

  /** 解密函数
   * @param src   加密过的二进制字符数组
   * @param strKey  密钥
   * @return
   * @throws Exception
   */
  public static String deCrypt (byte[] src,String strKey) throws Exception{
    KeyGenerator keygen;
    SecretKey desKey;
    Cipher c;
    byte[] cByte;

    keygen = KeyGenerator.getInstance("AES");
    keygen.init(128, new SecureRandom(strKey.getBytes()));

    desKey = keygen.generateKey();
    c = Cipher.getInstance("AES");

    c.init(Cipher.DECRYPT_MODE, desKey);


    cByte = c.doFinal(src);

    return new String(cByte,"UTF-8");
  }


  /**2进制转化成16进制
   * @param buf
   * @return
   */
  public static String parseByte2HexStr(byte buf[]) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < buf.length; i++) {
      String hex = Integer.toHexString(buf[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      sb.append(hex.toUpperCase());
    }
    return sb.toString();
  }


  /**将16进制转换为二进制
   * @param hexStr
   * @return
   */
  public static byte[] parseHexStr2Byte(String hexStr) {
    if (hexStr.length() < 1)
      return null;
    byte[] result = new byte[hexStr.length()/2];
    for (int i = 0;i< hexStr.length()/2; i++) {
      int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
      int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
      result[i] = (byte) (high * 16 + low);
    }
    return result;
  }

  public  String enCryptString(String s){
    try {
      byte[] bytes = enCrypt(s, saltStr1);
      s = parseByte2HexStr(bytes);
    }catch (Exception e){
      e.printStackTrace();
    }
    return s;
  }

  public  String deCryptString(String s){
    try {
      byte[] bytes = parseHexStr2Byte(s);
      s = deCrypt(bytes,saltStr1);
    }catch (Exception e){
      e.printStackTrace();
    }
    return s;
  }

  public static void main(String[] args) {
//    System.out.println(getBASE64("151513152"));
//    System.out.println(getFromBASE64("OWI4NzRhYzIzNDQwNDA0MDgwODBhODVjMTUxZGJjMDE1YzE1MWVhZDE2MDAwMV8xNDk2Mjk5MDY5NTU1NDZiODQ1ZTFhYzYyNDA="));
//    System.out.println(new String(Base64.getDecoder().decode("OWI4NzRhYzIzNDQwNDA0MDgwODBhODVjMTUxZGJjMDE1YzE1MWVhZDE2MDAwMV8xNDk2Mjk5MDY5NTU1NDZiODQ1ZTFhYzYyNDA=")));
    System.out.print(new CustomerCoder().encoderMD5("1"));
    //System.out.print(autoNumberTemplate("abc%", "abc0049", 4));
    /*String s = "123456";
    System.out.println("原始：" + s);
    System.out.println("编码：" + getBASE64(s));
    System.out.println("解码：" + getFromBASE64(getBASE64(s)));*/
  }
}
