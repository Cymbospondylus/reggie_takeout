package site.bzyl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import site.bzyl.commom.Result;
import site.bzyl.domain.Dish;
import site.bzyl.dto.DishDTO;

public interface IDishService extends IService<Dish> {
    Result<IPage> getPage(Integer page, Integer pageSize, String name);

    Result<String> enableOrDisableDish(Integer status, String ids);

    Result<String> deleteByIds(String ids);

    /**
     * 设计到dish表和dish_flavor表，需要添加Spring的事务
     * @param dishDTO
     * @return
     */
    @Transactional
    Result<String> addDish(DishDTO dishDTO);

}
