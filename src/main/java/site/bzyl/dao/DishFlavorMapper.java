package site.bzyl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import site.bzyl.domain.DishFlavor;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
