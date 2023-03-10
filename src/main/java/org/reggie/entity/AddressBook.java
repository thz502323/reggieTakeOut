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
 * 地址簿
 */
@Data
public class AddressBook implements Serializable {

  private static final Long serialVersionUID = 1L;

  private Long id;


  //用户id
  private Long userId;


  //收货人
  private String consignee;


  //手机号
  private String phone;


  //性别 0 女 1 男
  private String sex;


  //省级区划编号
  private String provinceCode;


  //省级名称
  private String provinceName;


  //市级区划编号
  private String cityCode;


  //市级名称
  private String cityName;


  //区级区划编号
  private String districtCode;


  //区级名称
  private String districtName;


  //详细地址
  private String detail;


  //标签
  private String label;

  //是否默认 0 否 1是
  private Integer isDefault;

  //创建时间
  @TableField(fill = FieldFill.INSERT)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")//对应后台传值到前台的json样式
//  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")对应前台向后台传值json的样式
  private LocalDateTime createTime;


  //更新时间
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  @TableField(fill = FieldFill.INSERT_UPDATE)
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
