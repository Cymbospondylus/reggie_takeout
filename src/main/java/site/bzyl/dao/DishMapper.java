package site.bzyl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import site.bzyl.domain.Dish;
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
