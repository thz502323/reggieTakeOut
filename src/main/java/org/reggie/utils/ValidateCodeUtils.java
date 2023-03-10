package org.reggie.utils;

import java.util.Random;
import org.reggie.exception.BusinessException;

/**
 * @author : 伪中二
 * @Date : 2023/3/3
 * @Description :
 */
public class ValidateCodeUtils {

  /**
   * 随机生成验证码
   * @param length 长度为4位或者6位 数字验证码
   * @return
   */
  public static Integer generateValidateCode(int length) {
    int code;
    Random random = new Random();
    if (length == 4) {
      code = random.nextInt(9000)+1000;//生成随机数，最大为9999
    } else if (length == 6) {
      code = random.nextInt(900000)+100000;//生成随机数，最大为999999
    } else {
      throw new BusinessException("只能生成4位或6位数字验证码");
    }
    return code;
  }

  /**
   * 随机生成指定长度字符串验证码
   * @param length 长度
   * @return
   */
  public static String generateValidateCodeString(int length) {
    Random rdm = new Random();
    String hash = Integer.toHexString(rdm.nextInt());
    return hash.substring(0, length);
  }
}