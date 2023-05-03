package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
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
import javax.servlet.http.HttpSession;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
    @Autowired
    private EmployeeMapper mapper;

    @Override
    public Result<EmployeeDto> login(HttpServletRequest request, String username, String password) {
        // 判空
        if (username == null || password == null) {
            return Result.error("用户名或密码不能为空！");
        }

        // 根据 username 查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        Employee employee = getOne(queryWrapper);

        if (employee == null ){
            return Result.error("用户不存在！");
        }
        // md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 判断用户名密码是否正确
        if (!password.equals(employee.getPassword())) {
            return Result.error("用户名或密码错误！");
        }

        // 查看状态是否被禁用
        if (employee.getStatus() != 1) {
            return Result.error("该员工账号已被禁用！");
        }

        // 封装为 dto 对象
        EmployeeDto employeeDto = new EmployeeDto();
        BeanUtils.copyProperties(employee, employeeDto);

        // 保存到 session 中, key不能是id，因为用户不知道自己的id
        request.getSession().setAttribute("employeeId", employeeDto.getId());

        return Result.success(employeeDto);
    }
}
