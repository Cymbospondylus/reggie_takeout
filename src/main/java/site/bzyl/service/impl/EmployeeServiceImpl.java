package site.bzyl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import site.bzyl.dao.EmployeeMapper;
import site.bzyl.domain.Employee;
import site.bzyl.commom.Result;
import site.bzyl.dto.EmployeeDto;
import site.bzyl.service.IEmployeeService;

import javax.servlet.http.HttpServletRequest;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
    @Autowired
    private EmployeeMapper mapper;

    @Override
    public Result<EmployeeDto> login(HttpServletRequest request, String username, String password) {
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        Employee employee = mapper.selectByUsernameAndPassword(username, password);

        if (employee == null) {
            return Result.error("用户或密码错误");
        }
        EmployeeDto dto = new EmployeeDto();
        BeanUtils.copyProperties(employee, dto);
        return Result.success(dto);
    }
}
