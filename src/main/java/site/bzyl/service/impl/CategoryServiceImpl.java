package site.bzyl.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.constant.RedisCacheConstant;
import site.bzyl.controller.exception.BusinessException;
import site.bzyl.dao.CategoryMapper;
import site.bzyl.entity.Category;
import site.bzyl.entity.Dish;
import site.bzyl.entity.Setmeal;
import site.bzyl.service.ICategoryService;
import site.bzyl.service.IDishService;
import site.bzyl.service.ISetmealService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    @Autowired
    private ISetmealService setmealService;

    @Autowired
    private IDishService dishService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<IPage> getPage(Integer page, Integer pageSize) {
        IPage<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort);

        page(pageInfo, lqw);

        return Result.success(pageInfo);
    }

    @Override
    public Result<String> deleteById(Long id) {
        // 查询要删除的分类有无关联的菜品
        LambdaQueryWrapper<Dish> dishLqw = new LambdaQueryWrapper<>();
        dishLqw.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLqw);
        if (dishCount > 0) {
            throw new BusinessException("当前分类已关联" + dishCount + "个菜品，删除失败！");
        }

        // 查询要删除的分类有无关联的套餐
        LambdaQueryWrapper<Setmeal> setmealLqw = new LambdaQueryWrapper<>();
        setmealLqw.eq(Setmeal::getCategoryId, id);
        int countSetmeal = setmealService.count(setmealLqw);
        if (countSetmeal > 0) {
            throw new BusinessException("当前分类已关联" + countSetmeal + "个套餐，删除失败！");
        }

        removeById(id);
        return Result.success("删除成功！");
    }

    @Override
    public Result<List> listCategories(Category category) {
        // 从缓存中查找categories
        String categoryList = redisTemplate.opsForValue().get(RedisCacheConstant.CATEGORY_LIST);
        //反序列化为List
        List<Category> categories = JSONArray.parseArray(categoryList, Category.class);
        // 如果缓存中存在
        if (categories != null) {
            // 直接返回缓存中的数据
            return Result.success(categories);
        }
        // 如果缓存中不存在, 从数据库中查
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(category.getType() != null, Category::getType, category.getType());
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        categories = list(lqw);
        // 将查询到的结果存入Redis缓存中
        redisTemplate.opsForValue().set(RedisCacheConstant.CATEGORY_LIST, JSON.toJSONString(categories),
                30, TimeUnit.MINUTES);
        return Result.success(categories);
    }


}
