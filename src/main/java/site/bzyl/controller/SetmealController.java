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

    // @Param在controller参数名和url参数名相同时可省略
    @GetMapping("/page")
    public Result<IPage> page(/*@RequestParam("page")*/ Integer page,
                              /*@RequestParam("pageSize")*/Integer pageSize,
                              /*@RequestParam(value = "name", required = false)*/ String name) {
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

    @PostMapping("/status/{stat}")
    public Result<String> updateStatusByIds(@PathVariable Integer stat, @Param("ids") String ids) {
        return setmealService.updateStatusByIds(stat, ids);
    }

    /**
     * 根据id查单个套餐，数据回显
     */
    @GetMapping("/{id}")
    public Result<SetmealDTO> getById(@PathVariable Long id) {
        return setmealService.getSetmealDTOById(id);
    }

    @PutMapping
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        return setmealService.updateBySetmealDTO(setmealDTO);
    }
}
