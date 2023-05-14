package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.HttpConstant;
import site.bzyl.constant.SystemConstant;
import site.bzyl.dao.AddressBookMapper;
import site.bzyl.entity.AddressBook;
import site.bzyl.service.IAddressService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressService {
    @Override
    public Result<List<AddressBook>> listAddress() {
        LambdaQueryWrapper<AddressBook> addressBookLqw = new LambdaQueryWrapper<>();
        addressBookLqw.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> addressBookList = this.list(addressBookLqw);

        return Result.success(addressBookList);
    }

    @Override
    public Result<String> addAddressBook(AddressBook addressBook, HttpSession session) {
        // 获取当前用户id
        Long userId = (Long) session.getAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
        addressBook.setUserId(userId);

        // 条件，未来需要再写

        this.save(addressBook);

        return Result.success("添加成功！");
    }
}
