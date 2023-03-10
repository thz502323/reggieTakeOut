package org.reggie.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import org.reggie.dto.SetmealDto;
import org.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {


  //修改套餐
  void updateWithSetmealDish(SetmealDto setmealDto);

  /*
    新增套餐，并保存菜品与套餐的关联关系
     */
  void saveWithDish(SetmealDto setmealDto);

  /**
   * 删除套餐与菜品的关系
   */
  void deleteWithDish(List<Long> ids);

  /**
   * 根据id查询套餐
   *
   * @param id
   */
  SetmealDto getByIdWithSetmeal(Long id);

  void updateStatus(int IsStatus, List<Long> ids);
}
