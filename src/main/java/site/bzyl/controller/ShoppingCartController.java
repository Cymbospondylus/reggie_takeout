package site.bzyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.commom.Result;
import site.bzyl.entity.ShoppingCart;
import site.bzyl.service.IShoppingCartService;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 展示购物车
     * @return
     */
    @GetMapping("/list")
    public Result<List> list() {
        return shoppingCartService.getList();
    }

    /**
     * 向购物车添加菜品
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        return shoppingCartService.add(shoppingCart, session);
    }
}
