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
import site.bzyl.util.BaseContext;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {

    @Override
    public Result<List> getList() {
        // 获取当前登录用户id
        Long userId = BaseContext.getCurrentId();
        // 只展示当前登录用户的购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
        shoppingCartLqw.eq(userId != null, ShoppingCart::getUserId, userId);
        // 查询列表
        List<ShoppingCart> shoppingCarts = this.list(shoppingCartLqw);
        // 返回
        return Result.success(shoppingCarts);
    }

    @Override
     public synchronized Result<String> add(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
        // 必须是当前用户购物车里的, 如果能查出记录说明购物车已有该商品, 数量加一
        Long userId = BaseContext.getCurrentId();
        shoppingCartLqw.eq(ShoppingCart::getUserId, userId);
        // 根据SetmealId查询, 这里前面必须要判空, 否则两个eq条件都加上的话查出来的永远是null
        shoppingCartLqw.eq(shoppingCart.getSetmealId() != null,
                ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        // 根据dishId查询
        shoppingCartLqw.eq(shoppingCart.getDishId() != null,
                ShoppingCart::getDishId, shoppingCart.getDishId());
        // 传过来的可能是setmealId, 也可能是dishId, 所以用ShoppingCart类型对象接受, 满足其一就可以查询
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
            // 设置创建时间为当前时间, 因为字段不一样, 和其他实体使用公共字段注入会出问题(没有updateTime)
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 添加到购物车中
            this.save(shoppingCart);
        }

        return Result.success("添加成功！");
    }

    @Override
    public Result<String> cleanShoppingCart(HttpSession session) {
        // 获取当前登录用户id
        Long userId = BaseContext.getCurrentId();
        // 根据用户id删除所有购物车记录
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
        shoppingCartLqw.eq(ShoppingCart::getUserId, userId);
        this.remove(shoppingCartLqw);
        // 响应结果
        return Result.success("清空购物车成功！");
    }

    @Override
     public synchronized Result<String> sub(ShoppingCart shoppingCart, HttpSession session) {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
        // 查询当前用户购物车里的
        shoppingCartLqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        // 根据SetmealId查询, 这里前面必须要判空, 否则两个eq条件都加上的话查出来的永远是null
        shoppingCartLqw.eq(shoppingCart.getSetmealId() != null,
                ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        // 根据dishId查询
        shoppingCartLqw.eq(shoppingCart.getDishId() != null,
                ShoppingCart::getDishId, shoppingCart.getDishId());

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
