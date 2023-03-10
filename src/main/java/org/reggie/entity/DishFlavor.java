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
 * 菜品口味
 */
@Data
public class DishFlavor implements Serializable {

  private static final Long serialVersionUID = 1L;

  private Long id;


  //菜品id
  private Long dishId;


  //口味名称
  private String name;


  //口味数据list
  private String value;


  @TableField(fill = FieldFill.INSERT)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime createTime;


  @TableField(fill = FieldFill.INSERT_UPDATE)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime updateTime;


  @TableField(fill = FieldFill.INSERT)
  private Long createUser;


  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Long updateUser;


  //是否删除
  private Integer isDeleted;

}
