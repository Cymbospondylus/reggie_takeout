package site.bzyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.commom.Result;
import site.bzyl.entity.Orders;
import site.bzyl.service.IOrderService;

import javax.servlet.http.HttpSession;

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
    public Result<String> submitOrder(@RequestBody Orders orders, HttpSession session) {
        return orderService.submit(orders, session);
    }
}
