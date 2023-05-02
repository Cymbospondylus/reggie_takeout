package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import site.bzyl.dao.EmployeeMapper;
import site.bzyl.domain.Employee;
import site.bzyl.domain.Result;
import site.bzyl.dto.EmployeeDto;
import site.bzyl.service.IEmployeeService;
import sun.security.provider.MD5;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
    @Autowired
    private EmployeeMapper mapper;

    @Override
    public Result login(String username, String password) {
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        Employee employee = mapper.selectByUsernameAndPassword(username, password);

        if (employee == null) {
            return Result.fail(404, "账号名或密码错误");
        }
        EmployeeDto dto = new EmployeeDto();
        BeanUtils.copyProperties(employee, dto);
        return Result.ok(dto, "登录成功");
    }
}
