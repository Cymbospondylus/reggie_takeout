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

    /**
     * 后台查询所有订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return orderService.backendPage(page, pageSize);
    }

    /**
     * 用于修改订单状态, 比如后台将订单状态设置为已派送
     * @param order
     * @return
     */
    @PutMapping
    public Result<String> updateOrder(@RequestBody Orders order) {
        orderService.updateById(order);
        return Result.success("修改成功！");
    }
}
