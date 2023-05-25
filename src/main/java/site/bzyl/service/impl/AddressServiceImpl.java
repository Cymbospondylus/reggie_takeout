package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.HttpConstant;
import site.bzyl.constant.SystemConstant;
import site.bzyl.controller.exception.BusinessException;
import site.bzyl.dao.AddressBookMapper;
import site.bzyl.entity.AddressBook;
import site.bzyl.service.IAddressService;
import site.bzyl.util.BaseContext;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Slf4j
public class AddressServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressService {
    @Override
    public Result<List<AddressBook>> listAddress() {
        LambdaQueryWrapper<AddressBook> addressBookLqw = new LambdaQueryWrapper<>();
        // 按照创建时间排序, 这样修改默认地址的时候不会跳来跳去
        addressBookLqw.orderByDesc(AddressBook::getCreateTime);

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

    @Override
    public Result<String> updateDefaultAddress(AddressBook addressBook) {
        log.info("[AddressServiceImpl]BaseContext.getCurrentId：{}", BaseContext.getCurrentId());
        // 查询出当前默认地址, 修改为非默认地址
        LambdaQueryWrapper<AddressBook> addressBookLqw = new LambdaQueryWrapper<>();
        addressBookLqw.eq(AddressBook::getIsDefault, 1);
        AddressBook defaultAddressBook = getOne(addressBookLqw);
        defaultAddressBook.setIsDefault(0);
        updateById(defaultAddressBook);
        // 将传入的新地址设为默认地址
        addressBook.setIsDefault(1);
        updateById(addressBook);

        return Result.success("修改默认地址成功！");
    }

    @Override
    public Result<String> deleteAddressBookByIds(List<Long> ids) {
        ids.forEach(id -> {
            if (this.getById(id).getIsDefault() == 1) {
                throw new BusinessException("当前删除的地址为默认地址，请修改默认地址后重试！");
            }
        });
        this.removeByIds(ids);
        return Result.success("删除成功");
    }
}
