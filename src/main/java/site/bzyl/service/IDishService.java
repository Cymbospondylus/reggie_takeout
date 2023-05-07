package site.bzyl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.commom.Result;
import site.bzyl.domain.Dish;

public interface IDishService extends IService<Dish> {
    Result<IPage> getPage(Integer page, Integer pageSize);
}
