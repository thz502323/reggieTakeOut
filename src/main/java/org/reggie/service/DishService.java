package org.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import org.reggie.dto.DishDto;
import org.reggie.entity.Dish;

public interface DishService extends IService<Dish> {


  //新增菜品，同时插入菜品口味表
  void saveWithFlavor(DishDto dishDto);

  //根据id更新菜品信息和口味
  void updateWithFlavor(DishDto dishDto);

  //根据id查询菜品信息和口味
  DishDto getByIdWithFlavor(Long id);


  //根据id更新套餐和菜品停售与起售
  void updateWithSetmealDish(int isStatus, List<Long> ids);
}
