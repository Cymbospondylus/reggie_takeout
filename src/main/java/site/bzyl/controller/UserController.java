package site.bzyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.commom.Result;
import site.bzyl.dto.UserDTO;
import site.bzyl.entity.User;
import site.bzyl.service.IUserService;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;


    @PostMapping("/sendMsg")
    public Result<String> generateValidationCode(@RequestBody User user, HttpSession session) {
        return userService.generateValidationCode(user, session);
    }
    // RequestParam可以用来处理 Content-Type 为 application/x-www-form-urlencoded 的请求体，Content-Type默认为该属性，也可以接收application/json的「url参数」
    // context-type是application\json的请求体数据只能用@RequstBody接收!!
    // 其他的数据只能用@RequstParam或者Bean接收,不能用@RequstBody接收
    @PostMapping("/login")
    public Result<User> login(@RequestBody UserDTO userDTO, HttpSession session) {
        return userService.login(userDTO, session);
    }

}
