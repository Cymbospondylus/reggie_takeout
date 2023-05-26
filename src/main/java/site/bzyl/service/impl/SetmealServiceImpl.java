package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.controller.exception.BusinessException;
import site.bzyl.dao.SetmealMapper;
import site.bzyl.dto.SetmealDTO;
import site.bzyl.entity.Category;
import site.bzyl.entity.Setmeal;
import site.bzyl.entity.SetmealDish;
import site.bzyl.service.ICategoryService;
import site.bzyl.service.ISetmealDishService;
import site.bzyl.service.ISetmealService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {

    @Autowired
    private ISetmealDishService setmealDishService;

    @Autowired
    private ICategoryService categoryService;

    @Override
    public Result<IPage> getPage(Integer page, Integer pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null, Setmeal::getName, name);
        lqw.orderByAsc(Setmeal::getUpdateTime);
        this.page(pageInfo, lqw);

        List<SetmealDTO> dtoList = pageInfo.getRecords().stream()
                .map(setmeal -> {
                    SetmealDTO setmealDTO = new SetmealDTO();
                    BeanUtils.copyProperties(setmeal, setmealDTO);

                    Long categoryId = setmealDTO.getCategoryId();
                    // 尽量不要潜逃写, 否则getById的结果是空再调用getName就出异常了
                    Category category = categoryService.getById(categoryId);
                    if (category != null) {
                        setmealDTO.setCategoryName(category.getName());
                    }
                    return setmealDTO;
                }).collect(Collectors.toList());

        Page<SetmealDTO> DTOPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, DTOPage, "records");
        DTOPage.setRecords(dtoList);

        return Result.success(DTOPage);
    }

    @Override
    public Result<String> saveSetmealWithDishes(SetmealDTO setmealDTO) {
        // 保存setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        this.save(setmeal);

        // 保存dishes
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes()
                .stream()
                .map(setmealDish -> {
                    setmealDish.setSetmealId(setmeal.getId());
                    return setmealDish;
                }).collect(Collectors.toList());

        setmealDishService.saveOrUpdateBatch(setmealDishList);

        return Result.success("添加套餐成功！");
    }

    @Override
    public Result<String> deleteByIds(List<Long> ids) {
        // 利用好框架能够少些if代码，用好Mybatis的关键是先想一下sql代码怎么写
        // select count(*) from setmeal where id in (xxx, xxx)
        LambdaQueryWrapper<Setmeal> setmealLqw = new LambdaQueryWrapper<>();
        setmealLqw.eq(Setmeal::getStatus, 1);
        setmealLqw.in(Setmeal::getId, ids);

        // 判断起售的套餐数是否大于0即可，不需要把每条数据都查到业务层来判断
        int count = this.count(setmealLqw);
        if (count > 0) {
            throw new BusinessException("删除的套餐仍在出售，请停售后重试！");
        }
        // 所有套餐都已停售，根据id删除
        this.removeByIds(ids);

        // 根据ids删除SetmealDish
        // delete from setmeal_dish where setmeal_id in (xxx, xxx)
        LambdaQueryWrapper<SetmealDish> setmealDishLqw = new LambdaQueryWrapper<>();
        setmealDishLqw.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLqw);

        return Result.success("删除成功！");
    }

    @Override
    public Result<String> updateStatusByIds(Integer stat, String ids) {
        // 获取idList
        List<String> idList = Arrays.asList(ids.split(","));
        // 根据id修改Status为stat
        ArrayList<Setmeal> setmeals = new ArrayList<>();
        idList.forEach(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(new Long(id));
            setmeal.setStatus(stat);
            setmeals.add(setmeal);
        });
        // 批量修改
        this.updateBatchById(setmeals);

        return Result.success("售卖状态修改成功！");
    }

    @Override
    public Result<SetmealDTO> getSetmealDTOById(Long id) {
        // 查询setmeal信息并封装成dto对象
        Setmeal setmeal = this.getById(id);
        SetmealDTO setmealDTO = new SetmealDTO();
        BeanUtils.copyProperties(setmeal, setmealDTO);

        // 创建setmealDishes并添加元素
        setmealDTO.setSetmealDishes(new ArrayList<SetmealDish>());
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, id);
        setmealDishService.list(lqw).forEach(setmealDish -> setmealDTO.getSetmealDishes().add(setmealDish));

        return Result.success(setmealDTO);
    }

    @Override
    public Result<String> updateBySetmealDTO(SetmealDTO setmealDTO) {
        // 修改setmeal
        this.updateById(setmealDTO);
        Long setmealId = setmealDTO.getId();
        // 菜品这种能新增也能删除的属性不适合用saveOrUpdate，数据会越来越多，最好的方法是全部删除再新增，为了不重复key舍弃逻辑删除
        setmealDishService.list().forEach(setmealDish -> {
            // 查询setmeal_dish表中关联setmealId的记录，全部删除后再将dto中的dishes插入
            LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SetmealDish::getSetmealId, setmealId);
            setmealDishService.remove(lqw);
        });
        // 设置setmealDishes的setmealId
        setmealDTO.getSetmealDishes().forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        // 清除原有数据后将setmealDishes插入表中完成修改
        setmealDishService.saveBatch(setmealDTO.getSetmealDishes());
        return Result.success("修改成功！");
    }

    @Override
    public Result<List> listSetmeal(Setmeal setmeal) {
        // 条件查询
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        // 若传入的categoryId不为null, 查询所以该分类下起售的套餐
        lqw.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        // 只查询在售套餐
        lqw.eq(Setmeal::getStatus, 1);
        // 根据创建时间排序
        lqw.orderByAsc(Setmeal::getCreateTime);
        List<Setmeal> setmealList = this.list(lqw);

        return Result.success(setmealList);
    }
}
