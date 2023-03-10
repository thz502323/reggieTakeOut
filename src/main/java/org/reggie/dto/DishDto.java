package org.reggie.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import org.reggie.entity.Dish;
import org.reggie.entity.DishFlavor;

//DTO，全称为Data Transfer Object，即数据传输对象，一般用于展示层与服务层之间的数据传输。
@Data
public class DishDto extends Dish {//dto类 数据传输对象，用于封装页面提交的数据，因为页面数据不一定和数据库entity中数据字段刚好对应

    //菜品口味
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
