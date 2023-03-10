package org.reggie.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.reggie.common.BaseUserContext;
import org.reggie.common.Result;
import org.reggie.dto.OrdersDto;
import org.reggie.entity.OrderDetail;
import org.reggie.entity.Orders;
import org.reggie.service.OrderDetailService;
import org.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

  @Autowired
  private OrderService orderService;
  @Autowired
  private OrderDetailService orderDetailService;

  /**
   * 用户下单
   *
   * @param orders
   * @return
   */
  @PostMapping("/submit")
  public Result<String> submit(@RequestBody Orders orders) {
    log.info("订单数据：{}", orders);
    orderService.submit(orders);
    return Result.success("下单成功");
  }

  /**
   * 查看订单
   */
//  @GetMapping("/userPage")
//  public Result<Page<OrderDetail>> userPage(int page, int pageSize) {
//    Page<OrderDetail> pageInfo = new Page<>(page, pageSize);//分页构造器
//    //获得当前用户id
//    Long userId = BaseUserContext.getCurrentId();
//
//    //查询当前用户的订单数据
//    LambdaQueryWrapper<Orders> orderIdwrapper = new LambdaQueryWrapper<>();
//    orderIdwrapper.eq(userId != null, Orders::getUserId, userId);
//    List<Long> orderId = orderService.list(orderIdwrapper).stream().map(Orders::getId).toList();
//    LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
//    wrapper.in(orderId.size() > 0, OrderDetail::getOrderId, orderId);
//    orderDetailService.page(pageInfo, wrapper);
//    //返回数据
//    return Result.success(pageInfo);
//  }

  //订单管理
  @GetMapping("/userPage")
  public Result<Page<OrdersDto>> userPage(int page, int pageSize) {

    Page<OrdersDto> ordersDtoPage = orderService.userGetPage(page, pageSize);

    return Result.success(ordersDtoPage);
  }

  //再来一单
  @PostMapping("/again")
  public Result<String> again(@RequestBody Orders order) {
    //取得orderId

    orderService.againOrder(order);

    return Result.success("再来一单");
  }


  @GetMapping("/page")
  public Result<Page<OrdersDto>> page(int page, int pageSize, String number, String beginTime,
      String endTime) {
    //构造分页构造器
    Page<Orders> pageInfo = new Page<>(page, pageSize);

    Page<OrdersDto> ordersDtoPage = new Page<>();
    //构造条件构造器
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    //根据number进行模糊查询
    queryWrapper.like(!StringUtils.isEmpty(number), Orders::getNumber, number);
    //根据Datetime进行时间范围查询
    if (beginTime != null && endTime != null) {
      queryWrapper.ge(Orders::getOrderTime, beginTime);
      queryWrapper.le(Orders::getOrderTime, endTime);
    }
    //添加排序条件
    queryWrapper.orderByDesc(Orders::getOrderTime);

    //进行分页查询
    orderService.page(pageInfo, queryWrapper);

    //对象拷贝
    BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

    List<Orders> records = pageInfo.getRecords();

    List<OrdersDto> list = records.stream().map((item) -> {
      OrdersDto ordersDto = new OrdersDto();

      BeanUtils.copyProperties(item, ordersDto);
      String name = "用户" + item.getUserId();//后期可以修改
      ordersDto.setUserName(name);
      return ordersDto;
    }).toList();

    ordersDtoPage.setRecords(list);
    return Result.success(ordersDtoPage);
  }

  @PutMapping
  public Result<String> send(@RequestBody Orders orders) {
    Long id = orders.getId();
    Integer status = orders.getStatus();
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Orders::getId, id);
    Orders one = orderService.getOne(queryWrapper);
    one.setStatus(status);
    orderService.updateById(one);
    return Result.success("派送成功");
  }

}