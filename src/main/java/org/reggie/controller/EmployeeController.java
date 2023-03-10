package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.Result;
import org.reggie.entity.Employee;
import org.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 伪中二
 * @Date : 2023/2/26
 * @Description :
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

  //  @Autowired
//  private EmployeeService employeeService;
  /*使用Autowired字段变量注入问题
  1. 对象的外部可见性
  2. 可能导致循环依赖
  3. 无法设置注入的对象为final，也无法注入静态变量，原因是变量必须在类实例化进行初始化
  ，建议使用：构造器注入适用于强制对象注入 或者 Setter注入适合可选对象注入
   */

  private EmployeeService employeeService;

  @Autowired
  public void setEmployeeService(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  /**
   * 员工后台登录
   *
   * @param request  前端请求
   * @param employee 前端的员工共信息对象
   * @return 登录页面
   */
  @PostMapping("/login")
  Result<Employee> login(HttpServletRequest request,
      @RequestBody Employee employee) {//对于对象类型用@RequestBody绑定,前端提交是用请求体

    //1.得到加密的密码
    String password = employee.getPassword();
    password = DigestUtils.md5DigestAsHex(password.getBytes());
    //2.查询用户名是否存在
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Employee::getUsername, employee.getUsername());
    Employee employeeTest = employeeService.getOne(
        queryWrapper);//查一条即可，因为数据库表中username禁止添加了唯一索引禁止重复
    if (employeeTest == null) {
      return Result.error("用户不存在");
    }
    //3.查询密码是否正确
    if (!employeeTest.getPassword().equals(password)) {
      return Result.error("密码错误");
    }

    //4.判断用户是否被封禁
    if (employeeTest.getStatus() == 0) {
      return Result.error("用户被封禁");
    }

    //5.放入session中,返回给前端
    request.getSession().setAttribute("employee", employeeTest.getId());
    return Result.success(employeeTest);
  }

  /**
   * 用户登出
   *
   * @param request
   * @return
   */
  @PostMapping("/logout")
  Result<String> logout(HttpServletRequest request) {
    request.getSession().removeAttribute("employee");
    return Result.success("退出成功");
  }

  /**
   * 增加员工
   *
   * @param employee
   * @return
   */
  @PostMapping
  Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {

    employee.setPassword("e10adc3949ba59abbe56e057f20f883e");//设置员工初始密码md5（123456）
    //获取当前登录用户id
    boolean result = employeeService.save(employee);
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
   * @return 查询的员工列表
   */
  @GetMapping("/page")
  Result<Page<Employee>> page(int page, int pageSize, String name) {
    log.info("{} - {} - {}", page, pageSize, name);
    Page<Employee> pageInfo = new Page<>(page, pageSize);//分页构造器

    //SQL处理
    LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(name != null, Employee::getUsername, name)
        .or()
        .like(name != null, Employee::getName, name)
        .orderByDesc(Employee::getUpdateTime);//查询员工 按更新时间排序
    employeeService.page(pageInfo, wrapper);
    //返回数据

    return Result.success(pageInfo);
  }

  /**
   * 用于编辑员工信息
   *
   * @return
   */
  @PutMapping
  Result<String> upData(HttpServletRequest request, @RequestBody Employee employee) {

//    Long employeeId = employee.getId(); 这个得到的是被修改人的id ，而不是修改人的id
    if (employeeService.updateById(employee)) {
      return Result.success("更新成功");
    }
    return Result.error("更新失败");
  }

  @GetMapping("/{id}")
  Result<Employee> getById(@PathVariable String id) {
    Employee employee = employeeService.getById(id);
    if (employee != null) {
      return Result.success(employee);
    }
    return Result.error("查询失败");
  }
}