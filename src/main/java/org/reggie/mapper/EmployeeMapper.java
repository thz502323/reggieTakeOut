package org.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.reggie.entity.Employee;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {//继承baseMapper后直接拥有一些常见增删改查方法

}
