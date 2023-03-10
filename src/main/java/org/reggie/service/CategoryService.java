package org.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggie.entity.Category;


public interface CategoryService extends IService<Category> {

  void remove(Long id);

}
