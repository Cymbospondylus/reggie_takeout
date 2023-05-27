package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
        // 只能查看当前登录用户的地址信息
        addressBookLqw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        List<AddressBook> addressBookList = this.list(addressBookLqw);

        return Result.success(addressBookList);
    }

    @Override
    public Result<String> addAddressBook(AddressBook addressBook) {
        // 设置当前用户id, 从ThreadLocal中取而不是session
        addressBook.setUserId(BaseContext.getCurrentId());
        // 条件，未来需要再写

        this.save(addressBook);

        return Result.success("添加成功！");
    }

    @Override
    public Result<String> updateDefaultAddress(AddressBook addressBook) {
        log.info("[AddressServiceImpl]BaseContext.getCurrentId：{}", BaseContext.getCurrentId());
        // 查询出当前默认地址, 修改为非默认地址
        LambdaUpdateWrapper<AddressBook> addressBookLuw = new LambdaUpdateWrapper<>();
        // 同一个线程可以用BaseContext直接从ThreadLocal里取userId, 不需要从session中取
        addressBookLuw.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookLuw.set(AddressBook::getIsDefault, 0);
        // sql: update address_book set is_default = 0 where user_id = ?
        this.update(addressBookLuw);

        /* 这端代码刚开始想着把原先的默认地址用getOne查出来, 修改isDefault后再updateById,
           实际上MybatisPlus有更加简便的写法, 可以使用LambdaUpdateWrapper
        AddressBook defaultAddressBook = getOne(addressBookLqw);
        defaultAddressBook.setIsDefault(0);
        updateById(defaultAddressBook);
        */

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

    @Override
    public Result<AddressBook> getDefaultAddress() {
        // 获取当前登录用户
        Long userId = BaseContext.getCurrentId();
        // 查询当前用户默认地址
        LambdaQueryWrapper<AddressBook> addressBookLqw = new LambdaQueryWrapper<>();
        addressBookLqw.eq(userId != null, AddressBook::getUserId, userId);
        // 默认地址
        addressBookLqw.eq(AddressBook::getIsDefault, 1);
        AddressBook defaultAddress = this.getOne(addressBookLqw);
        //todo 还需要写一个无任何地址或无默认地址时跳转到新增地址

        // 返回
        return Result.success(defaultAddress);
    }
}
