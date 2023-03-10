package org.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * @author : 伪中二
 * @Date : 2023/2/28
 * @Description :用于填充公共数据字段
 */
@Component
public class DataFillMetaObjectHandler implements MetaObjectHandler {

  //插入自动填充
  @Override
  public void insertFill(MetaObject metaObject) {
    metaObject.setValue("updateTime", LocalDateTime.now());
    metaObject.setValue("createTime", LocalDateTime.now());
    metaObject.setValue("createUser", BaseUserContext.getCurrentId());
    metaObject.setValue("updateUser", BaseUserContext.getCurrentId());
  }

  //更新自动填充
  @Override
  public void updateFill(MetaObject metaObject) {
    metaObject.setValue("updateTime", LocalDateTime.now());
    metaObject.setValue("updateUser", BaseUserContext.getCurrentId());
  }
}
