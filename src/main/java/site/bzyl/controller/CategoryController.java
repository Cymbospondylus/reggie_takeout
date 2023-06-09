package site.bzyl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.commom.Result;
import site.bzyl.entity.Category;
import site.bzyl.service.ICategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<IPage> page(@Param("page") Integer page, @Param("pageSize") Integer pageSize) {
        return categoryService.getPage(page, pageSize);
    }

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("添加成功！");
    }

    @PutMapping
    public Result<String> updateById(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success("修改成功！");
    }

    @DeleteMapping
    public Result<String> deleteById(@Param("id") Long id) {
        return categoryService.deleteById(id);
    }

    /**
     * 根据类型查询分类
     * @param category
     * @return
     */
    // 用对象接受url传参，不需要注解，只需要对象中存在与url参数相同的字段
    @GetMapping("/list")
    public Result<List> list(Category category) {
        return categoryService.listCategories(category);
    }

}

