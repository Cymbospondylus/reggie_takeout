package site.bzyl.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.RedisCacheConstant;
import site.bzyl.dao.DishMapper;
import site.bzyl.entity.Category;
import site.bzyl.entity.Dish;
import site.bzyl.entity.DishFlavor;
import site.bzyl.dto.DishDTO;
import site.bzyl.service.ICategoryService;
import site.bzyl.service.IDishFlavorService;
import site.bzyl.service.IDishService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    // 注入dishFlavorService添加菜品口味
    @Autowired
    private IDishFlavorService dishFlavorService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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

        // 清除缓存
        redisTemplate.delete(RedisCacheConstant.DISH_LIST);

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

        // 清除缓存
        redisTemplate.delete(RedisCacheConstant.DISH_LIST);

        return Result.success("删除成功！");
    }

    @Override
    public Result<String> addDish(DishDTO dishDTO) {
        // 不需要new一个新的Dish 可以用dishDTO来save新增，只填充不为null的字段
        save(dishDTO);

        // 添加口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
        dishFlavorService.saveBatch(flavors);

        // 清除缓存
        redisTemplate.delete(RedisCacheConstant.DISH_LIST);

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
        Long dishId = dish.getId();
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        dishFlavors.forEach(flavor -> flavor.setDishId(dishId));
        /*dishFlavorService.saveOrUpdateBatch(dishFlavors);*/
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishId);
        List<Long> idList = dishFlavorService.list(lqw)
                .stream()
                .map(flavor -> flavor.getId())
                .collect(Collectors.toList());
        // 对于flavor字段采用先删除再新增代替修改比较好，逻辑删除没有意义
        dishFlavorService.removeByIds(idList);
        dishFlavorService.saveBatch(dishFlavors);

        // 清除缓存
        redisTemplate.delete(RedisCacheConstant.DISH_LIST);

        return Result.success("修改成功！");
    }

    @Override
    public Result<List> listDishes(Dish dish) {
        /**
         * todo 有个很寄的问题, 缓存会导致选了一个菜品后其他的都不能选口味了
         */

        // 获取菜品的分类id
        Long categoryId = dish.getCategoryId();
        // 根据分类id从缓存中查
        String dishes = redisTemplate.opsForValue().get(RedisCacheConstant.DISH_LIST + categoryId);
        List<Dish> dishDTOList = JSON.parseArray(dishes, Dish.class);
        // 如果缓存中存在该分类下菜品信息
        if (dishDTOList != null) {
            // 直接返回
            return Result.success(dishDTOList);
        }
        // 根据分类id从数据库查
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        // 对要查询的条件进行判断，非空则根据该条件查询，对可能查找到的字段都进行一次判断，就可以通用地根据不同传参来实现方法复用
        lqw.eq(categoryId != null, Dish::getCategoryId, categoryId);
        // 只查询起售的菜品
        lqw.eq(Dish::getStatus, 1);
        // 按更新时间排序
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        // 查询
        List<Dish> dishList = this.list(lqw);
        // 用dishDTOList封装带有口味的菜品列表
        // 对于实体集合到DTO集合的拷贝, 用stream流+BeanUtils的形式比较简洁
        dishDTOList = dishList.stream()
                .map(dish1 -> {
                    // 对每个dish对象都创建一个DTO对象
                    DishDTO dishDTO = new DishDTO();
                    // 拷贝属性
                    BeanUtils.copyProperties(dish1, dishDTO);
                    // 根据dishId查询口味信息
                    LambdaQueryWrapper<DishFlavor> dishFlavorLqw = new LambdaQueryWrapper<>();
                    dishFlavorLqw.eq(DishFlavor::getDishId, dishDTO.getId());
                    List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLqw);
                    // 若存在口味信息, 则存入DTO中返回
                    if (dishFlavors != null && !dishFlavors.isEmpty()) {
                        dishDTO.setFlavors(dishFlavors);
                    }
                    return dishDTO;
                }).collect(Collectors.toList());

        // 将结果存入Redis缓存
        redisTemplate.opsForValue().set(RedisCacheConstant.DISH_LIST + categoryId,
                JSON.toJSONString(dishDTOList), 30, TimeUnit.MINUTES);

        return Result.success(dishDTOList);
    }
}
