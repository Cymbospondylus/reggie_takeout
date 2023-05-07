package site.bzyl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.commom.Result;
import site.bzyl.domain.Dish;
import site.bzyl.dto.DishDTO;

public interface IDishService extends IService<Dish> {
    Result<IPage> getPage(Integer page, Integer pageSize, String name);

    Result<String> enableOrDisableDish(Integer status, String ids);

    Result<String> deleteByIds(String ids);

    Result<String> addDish(DishDTO dishDTO);

}
