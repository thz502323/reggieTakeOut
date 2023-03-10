package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.common.BaseUserContext;
import org.reggie.dto.OrdersDto;
import org.reggie.exception.BusinessException;
import org.reggie.entity.*;
import org.reggie.mapper.OrderMapper;
import org.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

  @Autowired
  private ShoppingCartService shoppingCartService;

  @Autowired
  private UserService userService;

  @Autowired
  private AddressBookService addressBookService;

  @Autowired
  private OrderDetailService orderDetailService;

  /**
   * 用户下单
   *
   * @param orders
   */
  @Transactional
  public void submit(Orders orders) {
    //获得当前用户id
    Long userId = BaseUserContext.getCurrentId();

    //查询当前用户的购物车数据
    LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(ShoppingCart::getUserId, userId);
    List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);

    if (shoppingCarts == null || shoppingCarts.size() == 0) {
      throw new BusinessException("购物车为空，不能下单");
    }

    //查询用户数据
    User user = userService.getById(userId);

    //查询地址数据
    Long addressBookId = orders.getAddressBookId();
    AddressBook addressBook = addressBookService.getById(addressBookId);
    if (addressBook == null) {
      throw new BusinessException("用户地址信息有误，不能下单");
    }

    long orderId = IdWorker.getId();//订单号

    AtomicInteger amount = new AtomicInteger(0);

    List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrderId(orderId);
      orderDetail.setNumber(item.getNumber());
      orderDetail.setDishFlavor(item.getDishFlavor());
      orderDetail.setDishId(item.getDishId());
      orderDetail.setSetmealId(item.getSetmealId());
      orderDetail.setName(item.getName());
      orderDetail.setImage(item.getImage());
      orderDetail.setAmount(item.getAmount());
      amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
      return orderDetail;
    }).collect(Collectors.toList());

    orders.setId(orderId);
    orders.setOrderTime(LocalDateTime.now());
    orders.setCheckoutTime(LocalDateTime.now());
    orders.setStatus(2);
    orders.setAmount(new BigDecimal(amount.get()));//总金额
    orders.setUserId(userId);
    orders.setNumber(String.valueOf(orderId));
    orders.setUserName(user.getName());
    orders.setConsignee(addressBook.getConsignee());
    orders.setPhone(addressBook.getPhone());
    orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
        + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
        + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
        + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
    //向订单表插入数据，一条数据
    this.save(orders);
    //向订单明细表插入数据，多条数据
    orderDetailService.saveBatch(orderDetails);
    //清空购物车数据
    shoppingCartService.remove(wrapper);
  }

  @Override
  @Transactional
  public Page<OrdersDto> userGetPage(int page, int pageSize) {
    //构造分页构造器
    Page<Orders> pageInfo = new Page<>(page, pageSize);

    Page<OrdersDto> ordersDtoPage = new Page<>();

    //构造条件构造器
    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

    //添加排序条件
    queryWrapper.orderByDesc(Orders::getOrderTime);

    //进行分页查询
    this.page(pageInfo, queryWrapper);

    //对象拷贝
    BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");

    List<Orders> records = pageInfo.getRecords();

    List<OrdersDto> list = records.stream().map((item) -> {
      OrdersDto ordersDto = new OrdersDto();

      BeanUtils.copyProperties(item, ordersDto);
      Long Id = item.getId();
      //根据id查分类对象
      Orders orders = this.getById(Id);
      String number = orders.getNumber();
      LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
      lambdaQueryWrapper.eq(OrderDetail::getOrderId, number);
      List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);

      ordersDto.setOrderDetails(orderDetailList);
      return ordersDto;
    }).toList();
    ordersDtoPage.setRecords(list);
    return ordersDtoPage;
  }

  @Transactional
  @Override
  public void againOrder(Orders order) {
    Long id = order.getId();
    Orders orders = this.getById(id);
    //设置订单号码
    long orderId = IdWorker.getId();//用于设置id
    orders.setId(orderId);
    //设置订单号码
    String number = String.valueOf(IdWorker.getId());
    orders.setNumber(number);
    //设置下单时间
    orders.setOrderTime(LocalDateTime.now());
    orders.setCheckoutTime(LocalDateTime.now());
    orders.setStatus(2);
    //向订单表中插入一条数据
    this.save(orders);
    //修改订单明细表
    LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(OrderDetail::getOrderId, id);
    List<OrderDetail> list = orderDetailService.list(queryWrapper);
    list = list.stream().peek((item) -> {
      //订单明细表id
      long detailId = IdWorker.getId();
      //设置订单号码
      item.setOrderId(orderId);
      item.setId(detailId);
    }).toList();
    //向订单明细表中插入多条数据
    orderDetailService.saveBatch(list);
  }
}