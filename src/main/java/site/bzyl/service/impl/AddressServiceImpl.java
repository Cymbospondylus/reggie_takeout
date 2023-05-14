package site.bzyl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.dao.AddressBookMapper;
import site.bzyl.entity.AddressBook;
import site.bzyl.service.IAddressService;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressService {
}
