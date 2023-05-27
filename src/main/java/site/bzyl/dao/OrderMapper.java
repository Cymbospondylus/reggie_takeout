package site.bzyl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import site.bzyl.entity.Orders;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
