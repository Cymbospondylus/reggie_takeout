package site.bzyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.commom.Result;
import site.bzyl.entity.AddressBook;
import site.bzyl.service.IAddressService;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private IAddressService addressService;
    /**
     * 获取地址列表
     */
    @GetMapping("list")
    public Result<List<AddressBook>> list() {
        return addressService.listAddress();
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result<String> addAddressBook(@RequestBody AddressBook addressBook, HttpSession session) {
        return addressService.addAddressBook(addressBook, session);
    }

    /**
     * 修改默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<String> updateDefaultAddress(@RequestBody AddressBook addressBook) {
        return addressService.updateDefaultAddress(addressBook);
    }

    /**
     * 获取当前登录用户的默认地址
     * @param session
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefaultAddress(HttpSession session) {
        return addressService.getDefaultAddress(session);
    }

    /**
     * 查询单个地址信息，用于修改地址时的地址回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        return Result.success(addressService.getById(id));
    }

    /**
     * 编辑收货地址并保存
     * @param addressBook
     * @return
     */
    @PutMapping
    public Result<String> updateById(@RequestBody AddressBook addressBook) {
        addressService.updateById(addressBook);
        return Result.success("修改成功！");
    }

    @DeleteMapping
    public Result<String> deleteById(@RequestParam List<Long> ids) {
        return addressService.deleteAddressBookByIds(ids);
    }
}
