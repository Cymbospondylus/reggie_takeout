package site.bzyl.controller.interceptor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.commom.Result;
import site.bzyl.service.IDishService;

@Slf4j
@RestController
@RequestMapping("dish")
public class DishController {
    @Autowired
    private IDishService dishService;

    @GetMapping("/page")
    public Result<IPage> page(@Param("page") Integer page, @Param("pageSize") Integer pageSize) {
        return dishService.getPage(page, pageSize);
    }
}
