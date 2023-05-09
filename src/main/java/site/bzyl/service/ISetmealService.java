package site.bzyl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.commom.Result;
import site.bzyl.entity.Setmeal;

public interface ISetmealService extends IService<Setmeal> {
    Result<IPage> getPage(Integer page, Integer pageSize, String name);
}
