package org.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.entity.Employee;
import org.reggie.mapper.EmployeeMapper;
import org.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author : 伪中二
 * @Date : 2023/2/26
 * @Description :员工实现service类
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements
    EmployeeService {
  //由于继承了mybatis plus中的ServiceImpl，会有一些默认的实现方法，就和Mapper中一样

}
