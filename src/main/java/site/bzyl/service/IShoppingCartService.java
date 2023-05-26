package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.commom.Result;
import site.bzyl.entity.ShoppingCart;

import java.util.List;

public interface IShoppingCartService extends IService<ShoppingCart> {
    Result<List> getList();
}
