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
import site.bzyl.domain.Category;
import site.bzyl.domain.Dish;
import site.bzyl.domain.DishFlavor;
import site.bzyl.dto.DishDTO;
import site.bzyl.service.ICategoryService;
import site.bzyl.service.IDishFlavorService;
import site.bzyl.service.IDishService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    // 注入dishFlavorService添加菜品口味
    @Autowired
    private IDishFlavorService dishFlavorService;


    @Autowired
    private ICategoryService categoryService;

    @Override
    public Result<IPage> getPage(Integer page, Integer pageSize, String name) {
        // 条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Dish::getSort);
        lqw.like(name != null, Dish::getName, name);
        // 分页
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 查询
        page(pageInfo, lqw);

        // 构造一个DTO泛型的Page对象，尽量拷贝大的集合，而不是一条数据一条数据去拷贝
        Page<DishDTO> dishDTOPage = new Page<>();
        // 可以选择忽略属性拷贝，自己手动拷贝
        BeanUtils.copyProperties(pageInfo, dishDTOPage, "records");
        // 将pageInfo的records中的每条数据都拷贝到dishDTOList并根据id查询分类名称
        List<DishDTO> dishDTOList = pageInfo.getRecords().stream()
                .map((dish) -> {
                    // 用stream的map代替forEach操作
                    DishDTO dishDTO = new DishDTO();
                    BeanUtils.copyProperties(dish, dishDTO);

                    Long categoryId = dishDTO.getCategoryId();

                    Category category = categoryService.getById(categoryId);
                    if (category != null) {
                        dishDTO.setCategoryName(category.getName());
                    }

                    return dishDTO;
                }).collect(Collectors.toList());
        // 返回DTO泛型的page，这个很巧妙
        dishDTOPage.setRecords(dishDTOList);
        return Result.success(dishDTOPage);
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
        // 将Param传递的id拆分，根据id批量删除dish
        List<String> idList = Arrays.asList(ids.split(","));
        removeByIds(idList);
        // 根据id删除dishFlavors
        idList.forEach(dishId -> {
                    LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
                    lqw.eq(DishFlavor::getDishId, dishId);
                    dishFlavorService.list(lqw).forEach(flavor -> {
                                dishFlavorService.removeById(flavor.getId());
                            });
                });
        return Result.success("删除成功！");
    }

    @Override
    public Result<String> addDish(DishDTO dishDTO) {
        // todo 因为涉及到两张表的crud，所以要开启事务功能，把视频看一遍再回去复习一下springboot的事务

        // 添加菜品
        // 不需要new一个新的Dish 可以用dishDTO来save新增，只填充不为null的字段
        save(dishDTO);

        // todo 这里可以用Stream流优化，可以重新学习一下，前面的Ids批量删除也可以用Stream流来写，正好复习一下
        // 添加口味


        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
        dishFlavorService.saveBatch(flavors);

        return Result.success("添加成功！");
    }

    @Override
    public Result<DishDTO> getDishAndFlavors(Long id) {
        // 根据id查询dish
        Dish dish = getById(id);
        // 查询dishFlavors
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(lqw);

        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dish, dishDTO);
        dishDTO.setFlavors(flavors);

        return Result.success(dishDTO);
    }

    @Override
    public Result<String> updateDishAndFlavors(DishDTO dishDTO) {
        // 修改dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        updateById(dish);
        // 修改dishFlavors
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        dishFlavors.forEach(flavor -> flavor.setDishId(dish.getId()));
        dishFlavorService.saveOrUpdateBatch(dishFlavors);

        return Result.success("修改成功！");
    }
}
