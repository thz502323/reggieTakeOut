package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.reggie.dto.DishDto;
import org.reggie.entity.Dish;
import org.reggie.entity.DishFlavor;
import org.reggie.entity.Setmeal;
import org.reggie.entity.SetmealDish;
import org.reggie.mapper.DishMapper;
import org.reggie.service.DishFlavorService;
import org.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.reggie.service.SetmealDishService;
import org.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

  @Autowired
  private DishFlavorService dishFlavorService;

  @Autowired
  private SetmealDishService setmealDishService;

  @Autowired
  private SetmealService setmealService;

  @Override
  @Transactional
  public void updateWithSetmealDish(int IsStatus, List<Long> ids) {
    //先修改菜单停售起售状态
    LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.in(Dish::getId, ids).set(Dish::getStatus, IsStatus);
    this.update(updateWrapper);

    //得到和这个菜品相关的套餐id
    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.in(SetmealDish::getDishId, ids);
    List<Long> listId = setmealDishService.list(queryWrapper).stream()
        .map(SetmealDish::getSetmealId).toList();

    //接着修改与菜单关联的套餐停售和起售
    if (IsStatus == 0) {
      LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
      wrapper.in(Setmeal::getId, listId)
          .set(Setmeal::getStatus, IsStatus);//当停售菜品时需要停售包含此菜品的套餐，起售则不影响套餐
      setmealService.update(wrapper);
    }
  }

  /**
   * 新增菜品，同时保存对应的口味数据
   *
   * @param dishDto
   */
  @Transactional//事务注解
  public void saveWithFlavor(DishDto dishDto) {
    //保存菜品的基本信息到菜品表dish
    this.save(dishDto);

    Long dishId = dishDto.getId();//菜品id

    //菜品口味集合，此时需要在dishFlavor插入dishId
    List<DishFlavor> flavors = dishDto.getFlavors()
        .stream()
        .peek((item) -> item.setDishId(dishId))
        .collect(Collectors.toList());

    //保存菜品口味数据到菜品口味表dish_flavor
    dishFlavorService.saveBatch(flavors);

  }

  /**
   * 根据id查询菜品信息和对应的口味信息
   *
   * @param id
   * @return
   */
  public DishDto getByIdWithFlavor(Long id) {
    //查询菜品基本信息，从dish表查询
    Dish dish = this.getById(id);

    DishDto dishDto = new DishDto();
    BeanUtils.copyProperties(dish, dishDto);

    //查询当前菜品对应的口味信息，从dish_flavor表查询
    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId, dish.getId());
    List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
    dishDto.setFlavors(flavors);

    return dishDto;
  }

  @Override
  @Transactional
  public void updateWithFlavor(DishDto dishDto) {
    //更新dish表基本信息
    this.updateById(dishDto);

    //清理当前菜品对应口味数据---dish_flavor表的delete操作
    LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

    dishFlavorService.remove(queryWrapper);

    //添加当前提交过来的口味数据---dish_flavor表的insert操作
    List<DishFlavor> flavors = dishDto.getFlavors();

    flavors = flavors.stream().peek((item) -> item.setDishId(dishDto.getId()))
        .collect(Collectors.toList());

    dishFlavorService.saveBatch(flavors);
  }

}
