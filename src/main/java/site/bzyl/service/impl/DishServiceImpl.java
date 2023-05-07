package site.bzyl.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectBatchByIds;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.dao.DishMapper;
import site.bzyl.domain.Dish;
import site.bzyl.domain.DishFlavor;
import site.bzyl.dto.DishDTO;
import site.bzyl.service.IDishFlavorService;
import site.bzyl.service.IDishService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    // 注入dishFlavorService添加菜品口味
    @Autowired
    private IDishFlavorService dishFlavorService;

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

    @Override
    public Result<String> addDish(DishDTO dishDTO) {
        // todo 因为涉及到两张表的crud，所以要开启事务功能，把视频看一遍再回去复习一下springboot的事务

        // 添加菜品
        // todo 不需要new一个新的Dish 可以用dishDTO来save新增，只填充不为null的字段
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        save(dish);

        // todo 这里可以用Stream流优化，可以重新学习一下，前面的Ids批量删除也可以用Stream流来写，正好复习一下
        // 添加口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
        dishFlavorService.saveBatch(flavors);

        return Result.success("添加成功！");
    }
}
