package site.bzyl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.commom.Result;
import site.bzyl.dto.SetmealDTO;
import site.bzyl.service.ISetmealService;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private ISetmealService setmealService;

    @GetMapping("/page")
    public Result<IPage> page(@Param("page") Integer page,
                              @Param("pageSize")Integer pageSize,
                              @Param("name") String name) {
        return setmealService.getPage(page, pageSize, name);
    }

    @PostMapping
    public Result<String> addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        return setmealService.saveSetmealWithDishes(setmealDTO);
    }

    @DeleteMapping
    public Result<String> deleteByIds(@Param("ids") String ids) {
        return setmealService.deleteByIds(ids);
    }

}
