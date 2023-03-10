package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.reggie.dto.SetmealDto;
import org.reggie.entity.Dish;
import org.reggie.entity.Setmeal;
import org.reggie.entity.SetmealDish;
import org.reggie.exception.BusinessException;
import org.reggie.mapper.SetmealMapper;
import org.reggie.service.DishService;
import org.reggie.service.SetmealDishService;
import org.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理套餐与菜品的关系
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements
    SetmealService {

  @Autowired
  private SetmealDishService setmealDishService;
  @Autowired
  private DishService dishService;

  /**
   * 修改套餐
   *
   * @param setmealDto
   */
  @Override
  @Transactional
  public void updateWithSetmealDish(SetmealDto setmealDto) {
    //更新setmeal表基本信息
    updateById(setmealDto);

    //清理当前套餐对应菜单数据---SetmealDish表的delete操作
    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());

    setmealDishService.remove(queryWrapper);

    //添加当前提交过来的菜单数据
    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

    setmealDishes = setmealDishes.stream().peek((item) -> item.setSetmealId(setmealDto.getId()))
        .collect(Collectors.toList());
    setmealDishService.saveBatch(setmealDishes);
  }

  //增加套餐
  @Override
  @Transactional
  public void saveWithDish(SetmealDto setmealDto) {
    //保存套餐基本信息 由于setmealDto是继承了setmaeal字段，可以直接保存
    this.save(setmealDto);

    //保存关联关系的信息
    List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream()
        .peek(item -> item.setSetmealId(setmealDto.getId())).toList();
    setmealDishService.saveBatch(setmealDishes);
  }

  //删除套餐与菜品的关系
  @Override
  @Transactional
  public void deleteWithDish(List<Long> ids) {
    LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();

    wrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);//是否停售

    if (this.count(wrapper) > 0) {
      throw new BusinessException("菜品正在售卖，不能删除");
    }

    this.removeByIds(ids);//删除了Setmeal表中的数据,可以根据id直接删除

    //开始删除SetmealDish表内容，没法找到id 只能删除关联的菜品id不能用removeByIds
    LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

    lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);

    setmealDishService.remove(lambdaQueryWrapper);

  }

  //根据id获取信息
  @Override
  public SetmealDto getByIdWithSetmeal(Long id) {
    //查询菜品基本信息，从setmeal表查询
    Setmeal setmeal = this.getById(id);
    SetmealDto setmealDto = new SetmealDto();
    BeanUtils.copyProperties(setmeal, setmealDto);

    //得到相关的菜品信息和套餐名称
    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
    List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
    setmealDto.setSetmealDishes(setmealDishes);
    return setmealDto;
  }

  //用于启用或停用套餐
  @Override
  public void updateStatus(int IsStatus, List<Long> ids) {
    LambdaUpdateWrapper<SetmealDish> queryWrapper = new LambdaUpdateWrapper<>();
    LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
    queryWrapper.in(SetmealDish::getSetmealId, ids);
    List<Long> setmealDishes = setmealDishService.list(queryWrapper).stream().map(
        SetmealDish::getDishId).toList();//得到关联菜品的id

    if (dishService.count(wrapper.in(Dish::getId, setmealDishes).eq(Dish::getStatus, 0))
        > 0) {//只要有一个菜品被停售了
      throw new BusinessException("菜品已停售，不能启用");
    }
    LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.in(Setmeal::getId, ids).set(Setmeal::getStatus, IsStatus);
    this.update(lambdaUpdateWrapper);

  }
}
