package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.commom.Result;
import site.bzyl.entity.Orders;

import javax.servlet.http.HttpSession;

public interface IOrderService extends IService<Orders> {
    Result<String> submit(Orders orders);
}
