package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.Result;
import org.reggie.dto.SetmealDto;
import org.reggie.entity.Category;
import org.reggie.entity.Setmeal;
import org.reggie.service.CategoryService;
import org.reggie.service.SetmealDishService;
import org.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 伪中二
 * @Date : 2023/3/2
 * @Description :管理套餐，由多个菜品拼接成
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

  @Autowired
  private SetmealService setmealService;

  @Autowired
  private SetmealDishService setmealDishService;
  @Autowired
  private CategoryService categoryService;

  /**
   * 用于保存新增菜品套餐
   *
   * @param setmealDto
   * @return
   */
  @CacheEvict(value = "setmealCache", allEntries = true)//删除所有setmealCache的缓存
  @PostMapping
  public Result<String> save(@RequestBody SetmealDto setmealDto) {

    setmealService.saveWithDish(setmealDto);

    return Result.success("新增成功");
  }

  //展示套餐
  @GetMapping("/page")
  public Result<Page<SetmealDto>> page(int page, int pageSize, String name) {
    Page<Setmeal> pageInfo = new Page<>(page, pageSize);
    Page<SetmealDto> dtoPage = new Page<>();

    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    //添加查询条件，根据name进行like模糊查询
    queryWrapper.like(name != null, Setmeal::getName, name);
    //添加排序条件，根据更新时间降序排列
    queryWrapper.orderByDesc(Setmeal::getUpdateTime);

    setmealService.page(pageInfo, queryWrapper);
    //对象拷贝
    BeanUtils.copyProperties(pageInfo, dtoPage, "records");
    List<Setmeal> records = pageInfo.getRecords();

    List<SetmealDto> list = records.stream().map((setmea) -> {
      SetmealDto setmealDto = new SetmealDto();
      //对象拷贝
      BeanUtils.copyProperties(setmea, setmealDto);
      //分类id
      Long categoryId = setmea.getCategoryId();
      //根据分类id查询分类对象
      Category category = categoryService.getById(categoryId);
      if (category != null) {
        //分类名称
        String categoryName = category.getName();
        setmealDto.setCategoryName(categoryName);
      }
      return setmealDto;
    }).toList();

    dtoPage.setRecords(list);
    return Result.success(dtoPage);
  }

  //删除套餐
  @CacheEvict(value = "setmealCache", allEntries = true)//删除所有setmealCache的缓存
  @DeleteMapping
  public Result<String> delete(@RequestParam List<Long> ids) {

    setmealService.deleteWithDish(ids);
    return Result.success("删除成功");
  }


  //修改套餐的停售和起售
  @PostMapping("/status/{IsStatus}")
  public Result<String> status(@PathVariable int IsStatus, @RequestParam List<Long> ids) {
//    log.info("{} - {} ", IsStatus, ids);
    setmealService.updateStatus(IsStatus, ids);
    return Result.success("操作成功");
  }

  //修改套餐
  @PutMapping
  public Result<String> put(@RequestBody SetmealDto setmealDto) {
    setmealService.updateWithSetmealDish(setmealDto);

    return Result.success("修改成功");
  }

  //得到要修改的套餐信息
  @GetMapping("/{id}")
  public Result<SetmealDto> get(@PathVariable Long id) {

    SetmealDto dishDto = setmealService.getByIdWithSetmeal(id);

    return Result.success(dishDto);
  }

  /**
   * 根据条件查询套餐数据
   *
   * @param setmeal
   * @return
   */
  @GetMapping("/list")
  @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
//设置自处开启cache，cache的key为参数的categoryId_status
  public Result<List<Setmeal>> list(Setmeal setmeal) {
    LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId,
            setmeal.getCategoryId())
        .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus())
        .orderByDesc(Setmeal::getUpdateTime);
    List<Setmeal> list = setmealService.list(queryWrapper);
    return Result.success(list);
  }

}
