package site.bzyl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.HttpConstant;
import site.bzyl.dao.OrderMapper;
import site.bzyl.entity.Orders;
import site.bzyl.service.IOrderService;
import site.bzyl.util.BaseContext;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements IOrderService {
    @Override
    public Result<String> submit(Orders orders) {
        // 获取提交订单的用户id
        Long userId = BaseContext.getCurrentId();
        orders.setUserId(userId);
        // 设置订单提交时间
        orders.setOrderTime(LocalDateTime.now());
        // 设置订单结账时间
        orders.setCheckoutTime(LocalDateTime.now());
        // 保存订单
        /*this.save(orders);*/

        return Result.success("订单提交成功！");
    }
}
