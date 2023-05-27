package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.HttpConstant;
import site.bzyl.controller.exception.BusinessException;
import site.bzyl.dao.ShoppingCartMapper;
import site.bzyl.entity.ShoppingCart;
import site.bzyl.service.IShoppingCartService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {

    @Override
    public Result<List> getList(HttpSession session) {
        // 获取当前登录用户id
        Long userId = (Long) session.getAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
        // 只展示当前登录用户的购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCArtLqw = new LambdaQueryWrapper<>();
        shoppingCArtLqw.eq(userId != null, ShoppingCart::getUserId, userId);
        // 查询列表
        List<ShoppingCart> shoppingCarts = this.list(shoppingCArtLqw);
        // 返回
        return Result.success(shoppingCarts);
    }

    @Override
     public synchronized Result<String> add(ShoppingCart shoppingCart, HttpSession session) {
            Long userId = (Long) session.getAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
            LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
            // 根据SetmealId查询, 如果能查出记录说明购物车已有该商品, 直接令数量加一即可
            shoppingCartLqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
            // 除了SetmealId相同, 还必须是当前用户购物车里的
            shoppingCartLqw.eq(ShoppingCart::getUserId, userId);
            ShoppingCart existShoppingCart = this.getOne(shoppingCartLqw);
            /**
             * 这里存在线程安全问题, 若多个请求尝试添加一个购物车里没有的菜品
             * 会导致在表中创建多条数据而不是创建一条数据并修改菜品的number
             * todo 目前能想到的是用互斥锁, 但是不起作用, 只能先别点那么快
             */
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

    @Override
    public Result<String> cleanShoppingCart(HttpSession session) {
        // 获取当前登录用户id
        Long userId = (Long) session.getAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
        // 根据用户id删除所有购物车记录
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
        shoppingCartLqw.eq(userId != null, ShoppingCart::getUserId, userId);
        this.remove(shoppingCartLqw);

        // 响应结果
        return Result.success("清空购物车成功！");
    }

    @Override
     public synchronized Result<String> sub(ShoppingCart shoppingCart, HttpSession session) {
        Long userId = (Long) session.getAttribute(HttpConstant.CURRENT_LOGIN_USER_ID);
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
        // 根据SetmealId查询, 如果能查出记录说明购物车已有该商品
        shoppingCartLqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        // 除了套餐id相同, 还必须是当前用户购物车里的
        shoppingCartLqw.eq(ShoppingCart::getUserId, userId);
        ShoppingCart existShoppingCart = this.getOne(shoppingCartLqw);
        /**
         * 这里存在线程安全问题, 若多个请求尝试添加一个购物车里没有的菜品
         * 会导致在表中创建多条数据而不是创建一条数据并修改菜品的number
         * todo 目前能想到的是用互斥锁, 但是不起作用, 只能先别点那么快
         */
        // 若购物车中存在该商品
        if (existShoppingCart != null) {
            // 如果当前只有一份商品
            if (existShoppingCart.getNumber() == 1) {
                // 删除该菜品
                this.removeById(existShoppingCart);
            } else if (existShoppingCart.getNumber() > 1){
                // 当前数量大于1，直接减一即可
                existShoppingCart.setNumber(existShoppingCart.getNumber() - 1);
                // 将修改写回数据库
                this.updateById(existShoppingCart);
            }
        } else {
            // 若购物车中没有该商品
            throw new BusinessException("删除失败, 购物车中没有该菜品！");
        }

        return Result.success("添加成功！");
    }
}
