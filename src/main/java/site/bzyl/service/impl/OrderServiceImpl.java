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
    // todo 这里需要OrderDetail把每个菜品的信息保存起来
    /**
     * 这里页面只提交了地址、支付方式和备注, 因为购物车菜品信息可以根据userId查询
     * 所以通过BaseContext获取userId查表即可, 不需要前端再把购物车数据传回来
     * @param orders
     * @return
     */
    @Override
    public Result<String> submit(Orders orders) {
        // 获取提交订单的用户id
        Long userId = BaseContext.getCurrentId();
        orders.setUserId(userId);
        // 设置订单提交时间
        orders.setOrderTime(LocalDateTime.now());
        // 设置订单结账时间
        orders.setCheckoutTime(LocalDateTime.now());
        // 获取购物车信息

        // 保存订单
        this.save(orders);

        return Result.success("订单提交成功！");
    }
}
