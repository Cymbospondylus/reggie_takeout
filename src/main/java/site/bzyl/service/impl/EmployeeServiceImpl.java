package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import site.bzyl.constant.HttpConstant;
import site.bzyl.dao.EmployeeMapper;
import site.bzyl.domain.Employee;
import site.bzyl.commom.Result;
import site.bzyl.dto.EmployeeDTO;
import site.bzyl.service.IEmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
    @Autowired
    private EmployeeMapper mapper;

    @Override
    public Result<EmployeeDTO> login(HttpServletRequest request, String username, String password) {
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
        EmployeeDTO employeeDto = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDto);

        // 保存到 session 中, key不能是id，因为用户不知道自己的id
        request.getSession().setAttribute(HttpConstant.CURRENT_LOGIN_EMPLOYEE_ID, employeeDto.getId());

        return Result.success(employeeDto);
    }

    @Override
    public Result<String> addEmployee(HttpServletRequest request, Employee employee) {
        // 设置默认密码 123456 todo 应当修改为身份证后四位MD5加密
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);

        // 设置 创建时间 和 修改时间 为 当前时间
        LocalDateTime now = LocalDateTime.now();
        employee.setCreateTime(now);
        employee.setUpdateTime(now);

        // 设置 创建人 和 修改人 为 当前管理员用户
        Long currentEmployeeId = (Long) request.getSession().getAttribute(HttpConstant.CURRENT_LOGIN_EMPLOYEE_ID);
        employee.setCreateUser(currentEmployeeId);
        employee.setUpdateUser(currentEmployeeId);

        boolean result = save(employee);

        if (!result) {
            return Result.error("添加员工失败！");
        }

        return Result.success("添加成功！");
    }

    @Override
    public Result<Page> getPage(Integer page, Integer pageSize, String name) {

        // 分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        // MP提供的条件查询api, 使用 condition 就能比 if(name != null) 更优雅地判空
        // 声明 queryWrapper 的时候要记得加泛型<Employee> 否则不能用方法引用, like 的参数列表会变为 (boolean condition, R column, Object val)
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);


        // IService 里的 page 方法调用了BaseMapper的 selectPage 方法, 最后结果直接封装到传入的 Page 对象（pageInfo）
        // 不需要自定义类 PageData 了, 框架已经封装好可直接传入
        page(pageInfo, lqw);

        return Result.success(pageInfo);
    }
}
