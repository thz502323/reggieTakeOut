package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.entity.Category;
import org.reggie.entity.Dish;
import org.reggie.entity.Setmeal;
import org.reggie.exception.BusinessException;
import org.reggie.mapper.CategoryMapper;
import org.reggie.service.CategoryService;
import org.reggie.service.DishService;
import org.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : 伪中二
 * @Date : 2023/2/26
 * @Description :员工实现service类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements
    CategoryService {

  @Autowired
  private DishService dishService;
  @Autowired
  private SetmealService setmealService;

  @Override
  public void remove(Long ids) {
    LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
    //添加查询条件，根据分类id进行查询
    dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
    int dishCountId = dishService.count(dishLambdaQueryWrapper);

    //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
    if (dishCountId > 0) {
      //已经关联菜品，抛出一个业务异常
      throw new BusinessException("当前分类下关联了菜品，不能删除");
    }

    //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
    LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
    //添加查询条件，根据分类id进行查询
    setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);

    int setmealCountId = setmealService.count();
    if (setmealCountId > 0) {
      //已经关联套餐，抛出一个业务异常
      throw new BusinessException("当前分类下关联了套餐，不能删除");
    }

    //正常删除分类
    super.removeById(ids);
  }
  //由于继承了mybatis plus中的ServiceImpl，会有一些默认的实现方法，就和Mapper中一样


}
