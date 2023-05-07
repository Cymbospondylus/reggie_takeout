package site.bzyl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.commom.Result;
import site.bzyl.domain.Category;

public interface ICategoryService extends IService<Category> {
    Result<IPage> getPage(Integer page, Integer pageSize);

    Result<String> deleteById(Long id);
}
