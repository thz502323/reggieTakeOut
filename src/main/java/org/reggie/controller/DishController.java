package org.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.Result;
import org.reggie.dto.DishDto;
import org.reggie.entity.Category;
import org.reggie.entity.Dish;
import org.reggie.entity.DishFlavor;
import org.reggie.service.CategoryService;
import org.reggie.service.DishFlavorService;
import org.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
 * @Date : 2023/3/1
 * @Description :菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

  @Autowired
  private DishService dishService;

  @Autowired
  private RedisTemplate redisTemplate;

  @Autowired
  private DishFlavorService dishFlavorService;

  @Autowired
  private CategoryService categoryService;

  /**
   * 新增菜品
   *
   * @param dishDto
   * @return
   */
  @PostMapping
  public Result<String> save(@RequestBody DishDto dishDto) {
    log.info(dishDto.toString());
    dishService.saveWithFlavor(dishDto);
    //优化部分保存新的数据时，需要redis清除之前的缓存，保存数据库和redis一致性
    String key = "dish_" + dishDto.getCategoryId()+"_1";
    redisTemplate.delete(key);

    return Result.success("新增菜品成功");
  }

  /**
   * 菜品信息分页查询
   *
   * @param page
   * @param pageSize
   * @param name
   * @return
   */
  @GetMapping("/page")
  public Result<Page<DishDto>> page(int page, int pageSize, String name) {

    //构造分页构造器对象
    Page<Dish> pageInfo = new Page<>(page, pageSize);
    Page<DishDto> dishDtoPage = new Page<>();

    //条件构造器
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    //添加过滤条件和添加排序条件
    queryWrapper.like(name != null, Dish::getName, name)
        .orderByDesc(Dish::getUpdateTime);

    //执行分页查询
    dishService.page(pageInfo, queryWrapper);

    //对象拷贝
    BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

    List<Dish> records = pageInfo.getRecords();

    List<DishDto> list = records.stream().map((item) -> {
      DishDto dishDto = new DishDto();

      BeanUtils.copyProperties(item, dishDto);

      //分类id
      Long categoryId = item.getCategoryId();
      //根据id查询分类对象
      Category category = categoryService.getById(categoryId);

      if (category != null) {
        String categoryName = category.getName();
        dishDto.setCategoryName(categoryName);
      }
      return dishDto;
    }).collect(Collectors.toList());

    dishDtoPage.setRecords(list);

    return Result.success(dishDtoPage);
  }

  /**
   * 根据id查询菜品信息和对应的口味信息
   *
   * @param id
   * @return
   */
  @GetMapping("/{id}")
  public Result<DishDto> get(@PathVariable Long id) {

    DishDto dishDto = dishService.getByIdWithFlavor(id);

    return Result.success(dishDto);
  }

  /**
   * 修改菜品
   *
   * @param dishDto
   * @return
   */
  @PutMapping
  public Result<String> update(@RequestBody DishDto dishDto) {
    dishService.updateWithFlavor(dishDto);
    //优化部分，当菜品出现更新操作时需要清理对应的缓存
    String key = "dish_" + dishDto.getCategoryId()+"_1";
    redisTemplate.delete(key);

    return Result.success("修改菜品成功");
  }


  @GetMapping("/list")
  public Result<List<DishDto>> list(Dish dish) {

    List<DishDto> dishDtoList = null;
    //redis 优化
    String dishkey = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
    dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(dishkey);
    if (dishDtoList != null) {//能从redis中取数据就不要到mysql中拿
      return Result.success(dishDtoList);
    }

    //构造查询条件
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
    //添加条件，查询状态为1（起售状态）的菜品
    queryWrapper.eq(Dish::getStatus, 1);

    //添加排序条件
    queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

    List<Dish> list = dishService.list(queryWrapper);

    dishDtoList = list.stream().map((item) -> {
      DishDto dishDto = new DishDto();

      BeanUtils.copyProperties(item, dishDto);

      Long categoryId = item.getCategoryId();//分类id
      //根据id查询分类对象
      Category category = categoryService.getById(categoryId);

      if (category != null) {
        String categoryName = category.getName();
        dishDto.setCategoryName(categoryName);
      }

      //当前菜品的id
      Long dishId = item.getId();
      LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
      lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
      //SQL:select * from dish_flavor where dish_id = ?
      List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
      dishDto.setFlavors(dishFlavorList);
      return dishDto;
    }).collect(Collectors.toList());

    //由于redis查不到数据，所以将数据库数据传入redis中
    redisTemplate.opsForValue().set(dishkey,dishDtoList,1, TimeUnit.HOURS);
    return Result.success(dishDtoList);

  }

  //修改菜品的停售和起售，由于关联了套餐，所以要修改套餐
  @PostMapping("/status/{IsStatus}")
  public Result<String> status(@PathVariable int IsStatus, @RequestParam List<Long> ids) {

    dishService.updateWithSetmealDish(IsStatus, ids);

    return Result.success("操作成功");
  }


}
