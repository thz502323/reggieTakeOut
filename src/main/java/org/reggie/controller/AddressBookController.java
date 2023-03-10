package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.reggie.common.BaseUserContext;
import org.reggie.common.Result;
import org.reggie.entity.AddressBook;
import org.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

  @Autowired
  private AddressBookService addressBookService;

  /**
   * 新增
   */
  @PostMapping
  public Result<AddressBook> save(@RequestBody AddressBook addressBook) {
    addressBook.setUserId(BaseUserContext.getCurrentId());
    addressBookService.save(addressBook);
    return Result.success(addressBook);
  }

  /**
   * 修改地址操作
   */

  @PutMapping
  Result<AddressBook> put(@RequestBody AddressBook addressBook){
    addressBook.setUserId(BaseUserContext.getCurrentId());
    addressBookService.updateById(addressBook);
    return Result.success(addressBook);
  }

  /**
   * 删除地址操作
   */

  @DeleteMapping
  Result<String> delete(@RequestParam Long ids){
    addressBookService.removeById(ids);
    return Result.success("删除成功");
  }

  /**
   * 设置默认地址
   */
  @PutMapping("default")
  public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
    log.info("addressBook:{}", addressBook);

    addressBookService.saveDefaultAddress(addressBook);

    return Result.success(addressBook);
  }

  /**
   * 根据id查询地址
   */
  @GetMapping("/{id}")
  public Result<Object> get(@PathVariable Long id) {
    AddressBook addressBook = addressBookService.getById(id);
    if (addressBook != null) {
      return Result.success(addressBook);
    } else {
      return Result.error("没有找到该对象");
    }
  }

  /**
   * 查询默认地址
   */
  @GetMapping("default")
  public Result<AddressBook> getDefault() {
    LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(AddressBook::getUserId, BaseUserContext.getCurrentId()).eq(AddressBook::getIsDefault, 1);

    //SQL:select * from address_book where user_id = ? and is_default = 1
    AddressBook addressBook = addressBookService.getOne(queryWrapper);

    if (null == addressBook) {
      return Result.error("没有找到该对象");
    } else {
      return Result.success(addressBook);
    }
  }

  /**
   * 查询指定用户的全部地址
   */
  @GetMapping("/list")
  public Result<List<AddressBook>> list(AddressBook addressBook) {
    addressBook.setUserId(BaseUserContext.getCurrentId());
    log.info("addressBook:{}", addressBook);

    //条件构造器
    LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId,
        addressBook.getUserId());
    queryWrapper.orderByDesc(AddressBook::getUpdateTime);

    //SQL:select * from address_book where user_id = ? order by update_time desc
    return Result.success(addressBookService.list(queryWrapper));
  }



}
