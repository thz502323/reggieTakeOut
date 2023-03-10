package org.reggie.common;

/**
 * @author : 伪中二
 * @Date : 2023/2/28
 * @Description :获得threadLocal中的信息，里面放session中的id，在登录验证过滤器中执行
 */

//用于
public class BaseUserContext {

  private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

  public static void setCurrentId(Long id) {
    threadLocal.set(id);
  }

  public static Long getCurrentId() {
    return threadLocal.get();
  }

}
