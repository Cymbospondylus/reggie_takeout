package site.bzyl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.commom.Result;
import site.bzyl.entity.Orders;

import javax.servlet.http.HttpSession;

@Transactional
public interface IOrderService extends IService<Orders> {
    Result<String> submit(Orders orders);

    Result<Page> userPage(Integer page, Integer pageSize);

    Result<Page> backendPage(Integer page, Integer pageSize);
}
