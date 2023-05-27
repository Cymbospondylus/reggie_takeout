package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.commom.Result;
import site.bzyl.entity.AddressBook;

import javax.servlet.http.HttpSession;
import java.util.List;

@Transactional
public interface IAddressService extends IService<AddressBook> {
    Result<List<AddressBook>> listAddress();

    Result<String> addAddressBook(AddressBook addressBook, HttpSession session);

    Result<String> updateDefaultAddress(AddressBook addressBook);

    Result<String> deleteAddressBookByIds(List<Long> ids);

    Result<AddressBook> getDefaultAddress(HttpSession session);
}
