package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.bzyl.commom.Result;
import site.bzyl.dao.CategoryMapper;
import site.bzyl.domain.Category;
import site.bzyl.service.ICategoryService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public Result<IPage> getPage(Integer page, Integer pageSize) {
        IPage<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort);

        categoryMapper.selectPage(pageInfo, lqw);

        return Result.success(pageInfo);
    }


}
