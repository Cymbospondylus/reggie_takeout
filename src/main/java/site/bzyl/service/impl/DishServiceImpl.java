package site.bzyl.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.dao.DishMapper;
import site.bzyl.domain.Dish;
import site.bzyl.service.IDishService;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
}
