package org.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggie.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {

  void saveDefaultAddress(AddressBook addressBook);
}
