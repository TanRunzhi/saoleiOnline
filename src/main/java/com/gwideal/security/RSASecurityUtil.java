package com.gwideal.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by farno on 2017/3/30.
 */
public class RSASecurityUtil {

  private final static Logger logger = LogManager.getLogger();

  /**
   * 指定加密算法为RSA
   */
  private static final String ALGORITHM = "RSA";
  /**
   * 密钥长度，用来初始化
   */
  private static final int KEYSIZE = 1024;
  /**
   * 指定公钥存放文件
   */
  public static final String PUBLIC_KEY_FILE = "PublicKey";
  /**
   * 指定私钥存放文件
   */
  private static final String PRIVATE_KEY_FILE = "PrivateKey";

  /**
   * 生成密钥对
   *
   * @throws Exception
   */
  public static Map<String, Object> generateKeyPair() {

//        /** RSA算法要求有一个可信任的随机数源 */
//        SecureRandom secureRandom = new SecureRandom();

    try {
      /** 为RSA算法创建一个KeyPairGenerator对象 */
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

      /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
//        keyPairGenerator.initialize(KEYSIZE, secureRandom);
      keyPairGenerator.initialize(KEYSIZE);

      /** 生成密匙对 */
      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      /** 得到公钥 */
      RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

      /** 得到私钥 */
      RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

      Map<String, Object> keyMap = new HashMap<>();
      keyMap.put(PUBLIC_KEY_FILE, publicKey);
      keyMap.put(PRIVATE_KEY_FILE, privateKey);
      return keyMap;
    } catch (Exception e) {
      logger.error(e.getMessage());
      return null;
    }
  }

  /**
   * 加密方法
   *
   * @param source 源数据
   * @return
   * @throws Exception
   */
  public static String encrypt(Map<String, Object> keyMap, String source) {
    try {
      RSAPublicKey publicKey = (RSAPublicKey) keyMap.get(PUBLIC_KEY_FILE);
      /** 得到Cipher对象来实现对源数据的RSA加密 */
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] b = source.getBytes();
      /** 执行加密操作 */
      byte[] b1 = cipher.doFinal(b);
      return Base64.getEncoder().encodeToString(b1);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return null;
    }
  }

  public static String getPublicKey(Map<String, Object> keyMap) {
    Key key = (Key) keyMap.get(PUBLIC_KEY_FILE);
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  /**
   * 解密算法
   *
   * @param cryptograph 密文
   * @return
   * @throws Exception
   */
  public static String decrypt(Map<String, Object> keyMap, String cryptograph) {
    logger.debug("decrypt is fired...");
    try {
      RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get(PRIVATE_KEY_FILE);
      /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] b1 = Base64.getDecoder().decode(cryptograph);

      /** 执行解密操作 */
      byte[] b = cipher.doFinal(b1);
      return new String(b);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    String source = "上海合合信息科技发展有限公司";// 要加密的字符串
    System.out.println("准备用公钥加密的字符串为：" + source);
    Map<String, Object> keyMap = generateKeyPair();
    String cryptograph = encrypt(keyMap, source);// 生成的密文
    System.out.print("用公钥加密后的结果为:" + cryptograph);
    System.out.println();

    String target = decrypt(keyMap, cryptograph);// 解密密文
    System.out.println("用私钥解密后的字符串为：" + target);
    System.out.println();
  }
}
