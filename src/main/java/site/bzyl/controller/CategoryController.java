package site.bzyl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.commom.Result;
import site.bzyl.service.ICategoryService;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping("/page")
    public Result<IPage> page(@Param("page") Integer page, @Param("pageSize") Integer pageSize) {
        return categoryService.getPage(page, pageSize);
    }


}

