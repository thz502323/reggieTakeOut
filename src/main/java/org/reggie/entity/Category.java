package org.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 分类
 */
@Data
public class Category implements Serializable {

  private static final Long serialVersionUID = 1L;

  private Long id;


  //类型 1 菜品分类 2 套餐分类
  private Integer type;


  //分类名称
  private String name;


  //顺序
  private Integer sort;


  //创建时间
  @TableField(fill = FieldFill.INSERT)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;


  //更新时间
  @TableField(fill = FieldFill.INSERT_UPDATE)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime updateTime;


  //创建人
  @TableField(fill = FieldFill.INSERT)
  private Long createUser;


  //修改人
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Long updateUser;


  //是否删除
  private Integer isDeleted;

}
