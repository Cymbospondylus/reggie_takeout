package site.bzyl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.commom.Result;
import site.bzyl.entity.Orders;
import site.bzyl.service.IOrderService;


@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    /**
     * 点击'去支付'提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submitOrder(@RequestBody Orders orders) {
        return orderService.submit(orders);
    }

    /**
     * 在用户主页查询最新的订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page> userPage(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return orderService.userPage(page, pageSize);
    }
}
