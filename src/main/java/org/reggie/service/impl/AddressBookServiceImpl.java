package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import org.reggie.common.BaseUserContext;
import org.reggie.entity.AddressBook;
import org.reggie.mapper.AddressBookMapper;
import org.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

  @Override
  public boolean saveBatch(Collection<AddressBook> entityList, int batchSize) {
    return false;
  }

  @Override
  public boolean saveOrUpdateBatch(Collection<AddressBook> entityList, int batchSize) {
    return false;
  }

  @Override
  public boolean updateBatchById(Collection<AddressBook> entityList, int batchSize) {
    return false;
  }

  @Override
  public boolean saveOrUpdate(AddressBook entity) {
    return false;
  }

//  @Override
//  public AddressBook getOne(Wrapper<AddressBook> queryWrapper, boolean throwEx) {
//    return null;
//  }

  @Override
  public Map<String, Object> getMap(Wrapper<AddressBook> queryWrapper) {
    return null;
  }

  @Override
  public <V> V getObj(Wrapper<AddressBook> queryWrapper, Function<? super Object, V> mapper) {
    return null;
  }

  @Override
  @Transactional
  public void saveDefaultAddress(AddressBook addressBook) {
    LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(AddressBook::getUserId, BaseUserContext.getCurrentId());
    wrapper.set(AddressBook::getIsDefault, 0);
    //SQL:update address_book set is_default = 0 where user_id = ?
    this.update(wrapper);

    addressBook.setIsDefault(1);
    //SQL:update address_book set is_default = 1 where id = ?
    this.updateById(addressBook);
  }
}
