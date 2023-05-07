package site.bzyl.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectBatchByIds;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.dao.DishMapper;
import site.bzyl.domain.Dish;
import site.bzyl.service.IDishService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    @Override
    public Result<IPage> getPage(Integer page, Integer pageSize, String name) {
        // 条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Dish::getSort);
        if (name != null)
            lqw.like(Dish::getName, name);
        // 分页
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 查询
        page(pageInfo, lqw);

        return Result.success(pageInfo);
    }

    @Override
    public Result<String> enableOrDisableDish(Integer status, String ids) {
        String[] stringIds = ids.split(",");
        List<Dish> dishList = new ArrayList<>();
        for (String stringId : stringIds) {
            Dish dish = getById(stringId);
            dish.setStatus(status);
            dishList.add(dish);
        }

        // 批量修改状态
        updateBatchById(dishList);

        return Result.success("状态修改成功！");
    }

    @Override
    public Result<String> deleteByIds(String ids) {
        removeByIds(Arrays.asList(ids.split(",")));
        Arrays.asList(ids);
        return Result.success("删除成功！");
    }
}
