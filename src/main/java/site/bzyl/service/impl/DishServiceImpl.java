package site.bzyl.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.dao.DishMapper;
import site.bzyl.domain.Dish;
import site.bzyl.service.IDishService;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    @Override
    public Result<IPage> getPage(Integer page, Integer pageSize) {
        // 条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Dish::getSort);
        // 分页
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 查询
        page(pageInfo, lqw);

        return Result.success(pageInfo);
    }
}
