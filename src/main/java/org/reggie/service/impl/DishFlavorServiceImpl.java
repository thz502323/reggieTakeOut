package org.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.entity.DishFlavor;
import org.reggie.mapper.DishFlavorMapper;
import org.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * 管理菜品口味表
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements
    DishFlavorService {
}
