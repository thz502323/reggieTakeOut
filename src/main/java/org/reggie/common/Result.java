package org.reggie.common;

import java.io.Serializable;
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 伪中二
 * @Date : 2023/2/26
 * @Description :通用返回结果类，服务器返回结果给前端都会用到这个
 */

@Data
public class Result<T> implements Serializable {//泛型类，代表只接受一种泛型，T-数据，注意T必须是引用对象

  private Integer code; //编码：1成功，0和其它数字为失败

  private String msg; //错误信息

  private T data; //数据

  private Map<String, Object> map = new HashMap<String, Object>(); //动态数据

  public static <T> Result<T> success(T object) {
    Result<T> result = new Result<T>();
    result.data = object;
    result.code = 1;
    return result;
  }

  public static <T> Result<T> error(String msg) {//注意静态方法无法访问类上定义的泛型-静态方法比类实例更快，所以要在static后面加入你要引用的泛型即第一个<T>。
    Result<T> result = new Result<>();
    result.msg = msg;
    result.code = 0;
    return result;
  }

  public Result<T> add(String key, Object value) {//返回Result类，加入一个泛型约束<T>
    this.map.put(key, value);
    return this;
  }

}
