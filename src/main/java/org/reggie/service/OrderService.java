package org.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.reggie.dto.OrdersDto;
import org.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);

  Page<OrdersDto> userGetPage(int page, int pageSize);

  void againOrder(Orders order);
}
