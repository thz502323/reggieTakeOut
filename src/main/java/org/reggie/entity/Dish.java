package org.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 菜品实现类
 */
@Data
public class Dish implements Serializable {

  private static final Long serialVersionUID = 1L;

  private Long id;


  //菜品名称
  private String name;


  //菜品分类id
  private Long categoryId;


  //菜品价格
  private BigDecimal price;


  //商品码
  private String code;


  //图片
  private String image;


  //描述信息
  private String description;


  //0 停售 1 起售
  private Integer status;


  //顺序
  private Integer sort;

  //为了避免 Could not write JSON: Invalid type definition for type `java.time.LocalDateTime`:redis序列化出错
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;


  @TableField(fill = FieldFill.INSERT)
  private Long createUser;


  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Long updateUser;


  //是否删除
  private Integer isDeleted;

}
