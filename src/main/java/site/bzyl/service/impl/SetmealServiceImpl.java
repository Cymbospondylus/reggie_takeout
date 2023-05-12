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
                    setmealDTO.setCategoryName(categoryService.getById(categoryId).getName());

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
}
