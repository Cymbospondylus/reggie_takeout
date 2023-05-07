package site.bzyl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.commom.Result;
import site.bzyl.domain.Dish;
import site.bzyl.service.IDishService;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    @PostMapping
    public Result<String> addDish(@RequestBody Dish dish) {
        dishService.save(dish);
        return Result.success("添加成功！");
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
}
