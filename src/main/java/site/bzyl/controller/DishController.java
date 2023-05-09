package site.bzyl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.commom.Result;
import site.bzyl.dto.DishDTO;
import site.bzyl.entity.Dish;
import site.bzyl.service.IDishService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private IDishService dishService;

    /**
     * 分页查询（name不为空时带模糊查询）
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<IPage> page(@Param("page") Integer page,
                              @Param("pageSize") Integer pageSize,
                              @Param("name") String name) {
        return dishService.getPage(page, pageSize, name);
    }

    /**
     * 用DTO实现添加两个实体，不能用两个RequestBody
     * @param dishDTO
     * @return
     */
    @PostMapping
    public Result<String> addDish(@RequestBody DishDTO dishDTO) {
        return dishService.addDish(dishDTO);
    }

    /**
     * 批量起售/停售菜品
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status, @Param("ids") String ids) {
        return dishService.enableOrDisableDish(status, ids);
    }

    @DeleteMapping
    public Result<String> delete(@Param("ids") String ids) {
        return dishService.deleteByIds(ids);
    }

    @GetMapping("/{id}")
    public Result<DishDTO> getById(@PathVariable Long id) {
        return dishService.getDishAndFlavors(id);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        return dishService.updateDishAndFlavors(dishDTO);
    }


    /**
     * list方法的参数可以用(@Param("categoryId) Long categoryId), 这样相当于list方法和categoryId耦合了
     * 更加通用的做法是把url里的参数封装到「含有」这个字段的对象里，这样list方法就和某个字段解耦了
     * 获取参数的时候可以直接使用(Dish dish)来接受参数, (没太理解为什么可以不用@RequestBody
     * 以及第一种方法使用@Param注解好像也可以传参成功，只要参数的名字和url里的一样就可以
     * 有空把这篇博客读了：https://www.cnblogs.com/eternityz/p/12442406.html#URL%E4%BC%A0%E5%8F%82
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List> list(Dish dish) {
        return dishService.listDishes(dish);
    }
}
