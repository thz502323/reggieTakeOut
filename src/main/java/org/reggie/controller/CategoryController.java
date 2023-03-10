package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.reggie.common.Result;
import org.reggie.entity.Category;
import org.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 伪中二
 * @Date : 2023/2/28
 * @Description :用来菜品分类管理
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
  @Autowired
  private CategoryService categoryService;

  /**
   * 增加菜品分类
   *
   * @param category 菜品分类
   * @return
   */
  @PostMapping
  Result<String> save(HttpServletRequest request, @RequestBody Category category) {
    //获取当前登录用户id
    boolean result = categoryService.save(category);
    if (result) {
      return Result.success("添加成功");
    }
    return Result.error("添加失败");

  }

  /**
   * 分页查询
   *
   * @param page     第几页
   * @param pageSize 一页的大小
   * @param name     要查询的员工姓名，默认null
   * @return 查询的菜品列表
   */
  @GetMapping("/page")
  Result<Page<Category>> page(int page, int pageSize, String name) {
    Page<Category> pageInfo = new Page<>(page, pageSize);//分页构造器
    //SQL处理
    LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
    wrapper.orderByDesc(Category::getSort);
    categoryService.page(pageInfo, wrapper);
    //返回数据
    return Result.success(pageInfo);
  }

  /**
   * 删除分类，注意会关联到菜品，所以要提示
   * @param ids
   * @return
   */
  @DeleteMapping
  Result<String> delete(Long ids){

    categoryService.remove(ids);
    return Result.success("删除成功");
  }

  @PutMapping
  Result<String> updata(@RequestBody Category category){
    categoryService.updateById(category);
    return Result.success("修改成功");
  }

  /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }
}
