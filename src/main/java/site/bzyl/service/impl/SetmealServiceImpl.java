package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.bzyl.commom.Result;
import site.bzyl.dao.SetmealMapper;
import site.bzyl.dto.SetmealDTO;
import site.bzyl.entity.Setmeal;
import site.bzyl.entity.SetmealDish;
import site.bzyl.service.ISetmealDishService;
import site.bzyl.service.ISetmealService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {

    @Autowired
    private ISetmealDishService setmealDishService;

    @Override
    public Result<IPage> getPage(Integer page, Integer pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name != null, Setmeal::getName, name);
        lqw.orderByAsc(Setmeal::getUpdateTime);
        this.page(pageInfo, lqw);

        return Result.success(pageInfo);
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
    public Result<String> deleteByIds(String ids) {
        // 删除套餐
        List<String> idList = Arrays.asList(ids.split(","));
        removeByIds(idList);

        // 删除套餐-菜品表中对应该套餐的菜品
        idList.forEach(id -> {
            LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
            lqw.eq(SetmealDish::getSetmealId, id);
            setmealDishService.remove(lqw);
        });

        return Result.success("删除成功！");
    }
}
