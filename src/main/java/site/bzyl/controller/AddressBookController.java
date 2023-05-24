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

    @PutMapping("/default")
    public Result<String> updateDefaultAddress(@RequestBody AddressBook addressBook) {
        return addressService.updateDefaultAddress(addressBook);
    }

    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        return Result.success(addressService.getById(id));
    }
}
