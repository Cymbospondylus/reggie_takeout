package site.bzyl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.dao.ShoppingCartMapper;
import site.bzyl.entity.ShoppingCart;
import site.bzyl.service.IShoppingCartService;

import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
    @Override
    public Result<List> getList() {
        // todo 前端的代码可以研究一下，购物车展示成功后才能从分类的列表里展示菜品和套餐的信息
        List<ShoppingCart> shoppingCarts = this.list();

        return Result.success(shoppingCarts);
    }
}
