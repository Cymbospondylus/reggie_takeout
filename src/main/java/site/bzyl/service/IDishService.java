package site.bzyl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.commom.Result;
import site.bzyl.entity.Dish;
import site.bzyl.dto.DishDTO;

import java.util.List;

@Transactional
public interface IDishService extends IService<Dish> {
    Result<IPage> getPage(Integer page, Integer pageSize, String name);

    Result<String> enableOrDisableDish(Integer status, String ids);

    Result<String> deleteByIds(String ids);

    /**
     * 涉及到dish表和dish_flavor表，需要添加Spring的事务
     * @param dishDTO
     * @return
     */

    Result<String> addDish(DishDTO dishDTO);

    Result<DishDTO> getDishAndFlavors(Long id);

    Result<String> updateDishAndFlavors(DishDTO dishDTO);

    Result<List> listDishes(Dish dish);
}
