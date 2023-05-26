package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.HttpConstant;
import site.bzyl.dao.ShoppingCartMapper;
import site.bzyl.entity.Setmeal;
import site.bzyl.entity.ShoppingCart;
import site.bzyl.service.ISetmealService;
import site.bzyl.service.IShoppingCartService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {

    @Override
    public Result<List> getList() {
        // todo 前端的代码可以研究一下，购物车展示成功后才能从分类的列表里展示菜品和套餐的信息
        List<ShoppingCart> shoppingCarts = this.list();

        return Result.success(shoppingCarts);
    }

    @Override
    public Result<String> add(ShoppingCart shoppingCart, HttpSession session) {
        Long userId = (Long) session.getAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
        // 根据名称查询, 如果能查出记录说明购物车已有该商品, 直接令数量加一即可
        shoppingCartLqw.eq(ShoppingCart::getName, shoppingCart.getName());
        // 除了名字相同, 还必须是当前用户购物车里的
        shoppingCartLqw.eq(ShoppingCart::getUserId, userId);
        ShoppingCart existShoppingCart = this.getOne(shoppingCartLqw);
        // 若购物车中已存在该商品
        if (existShoppingCart != null) {
            // 让当前购物车的商品个数加一
            existShoppingCart.setNumber(existShoppingCart.getNumber() + 1);
            // 保存修改
            this.updateById(existShoppingCart);
        } else {
            // 若购物车中没有该商品
            // 设置用户user_id为当前登录用户的id
            if (shoppingCart != null) {
                shoppingCart.setUserId(userId);
            }
            // 添加到购物车中
            this.save(shoppingCart);
        }

        return Result.success("添加成功！");
    }
}
