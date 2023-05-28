package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.dao.OrderMapper;
import site.bzyl.dto.OrdersDTO;
import site.bzyl.entity.*;
import site.bzyl.service.*;
import site.bzyl.util.BaseContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements IOrderService {
    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IShoppingCartService shoppingCartService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IAddressService addressService;
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

        // 根据用户id获取购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartLqw = new LambdaQueryWrapper<>();
        shoppingCartLqw.eq(userId != null, ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLqw);

        // 根据用户id获取当前用户, 用于获取姓名等信息
        User user = userService.getById(userId);
        // 根据地址id获取默认地址, 用于获取地址和联系人等信息
        AddressBook addressBook = addressService.getById(orders.getAddressBookId());
        // 订单号, 可以使用MybatisPlus提供的id获取器「IdWorker」生成, 因为订单明细表需要订单id, 不能传入null让框架自动生成
        Long orderId = IdWorker.getId();


        // 订单总金额, 用原子整数防止多线程下的并发问题, 最后转换为BigDecimal存储
        AtomicInteger amount = new AtomicInteger(0);

        // 将购物车里的每项菜品都存入订单详细表
        List<OrderDetail> orderDetails = shoppingCarts.stream()
                .map(shoppingCartItem -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setName(shoppingCartItem.getName());
                    orderDetail.setImage(shoppingCartItem.getImage());
                    orderDetail.setOrderId(orderId);
                    orderDetail.setSetmealId(shoppingCartItem.getSetmealId());
                    orderDetail.setDishFlavor(shoppingCartItem.getDishFlavor());
                    orderDetail.setNumber(shoppingCartItem.getNumber());
                    orderDetail.setAmount(shoppingCartItem.getAmount());
                    // 用原子整数的CAS操作更新购物车的金额
                    amount.addAndGet(shoppingCartItem.
                            getAmount().multiply(new BigDecimal(shoppingCartItem.getNumber())).intValue());
                    return orderDetail;
                }).collect(Collectors.toList());

        // 设置订单基本信息
        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        // todo 这里用AtomicInteger来new一个BigDecimal, 构造器用的是int的？
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(addressBook.getPhone());
        // 根据地址簿的信息拼接地址字符串
        orders.setAddress(
                  (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());

        // 保存订单
        this.save(orders);

        // 保存订单详情
        orderDetailService.saveBatch(orderDetails);


        // 清空购物车, Lqw已经定义了eq当前用户id
        /*shoppingCartService.cleanShoppingCart();*/
        shoppingCartService.remove(shoppingCartLqw);

        return Result.success("订单提交成功！");
    }

    @Override
    public Result<Page> userPage(Integer page, Integer pageSize) {
        // 分页查询
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        // 条件查询, 只查询当前登录用户的订单
        LambdaQueryWrapper<Orders> ordersLqw = new LambdaQueryWrapper<>();
        this.page(ordersPage, ordersLqw);
        // userPage页面还需要orderDetails, 所以封装成DTO的page对象返回
        Page<OrdersDTO> ordersDTOPage = new Page<>();
        BeanUtils.copyProperties(ordersPage, ordersDTOPage, "records");
        // 单独处理records
        List<OrdersDTO> ordersDTOList = ordersPage.getRecords().stream()
                .map(order -> {
                    OrdersDTO ordersDTO = new OrdersDTO();
                    BeanUtils.copyProperties(order, ordersDTO);
                    // 通过orderId获取所有菜品信息
                    Long orderId = order.getId();
                    LambdaQueryWrapper<OrderDetail> orderDetailLqw = new LambdaQueryWrapper<>();
                    orderDetailLqw.eq(OrderDetail::getOrderId, orderId);
                    List<OrderDetail> orderDetails = orderDetailService.list(orderDetailLqw);
                    ordersDTO.setOrderDetails(orderDetails);
                    return ordersDTO;
                }).collect(Collectors.toList());
        // 将ordersDTOList存入page对象返回
        ordersDTOPage.setRecords(ordersDTOList);
        return Result.success(ordersDTOPage);
    }
}
