package site.bzyl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import site.bzyl.domain.Employee;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    @Select("select * from employee where username = #{username} and password = #{password}")
    Employee selectByUsernameAndPassword(String username, String password);
}
