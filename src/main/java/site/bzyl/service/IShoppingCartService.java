package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.commom.Result;
import site.bzyl.entity.ShoppingCart;

import javax.servlet.http.HttpSession;
import java.util.List;

@Transactional
public interface IShoppingCartService extends IService<ShoppingCart> {
    Result<List> getList(HttpSession session);

    Result<String> add(ShoppingCart shoppingCart, HttpSession session);

    Result<String> cleanShoppingCart(HttpSession session);
}
