package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.commom.Result;
import site.bzyl.entity.AddressBook;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface IAddressService extends IService<AddressBook> {
    Result<List<AddressBook>> listAddress();

    Result<String> addAddressBook(AddressBook addressBook, HttpSession session);
}
