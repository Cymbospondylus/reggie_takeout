package site.bzyl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import site.bzyl.constant.HttpConstant;
import site.bzyl.dao.EmployeeMapper;
import site.bzyl.domain.DataPage;
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
    public Result<DataPage<EmployeeDTO>> getPage(Integer page, Integer pageSize) {
        // 分页查询
        IPage<Employee> empPage = new Page<>(page, pageSize);
        mapper.selectPage(empPage, null);
        List<Employee> records = empPage.getRecords();

        // 为空则返回 null
        if (records == null) {
            return Result.error("员工列表为空！");
        }

        // 拷贝成dto对象
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        records.forEach((employee) -> {
            EmployeeDTO employeeDto = new EmployeeDTO();
            BeanUtils.copyProperties(employee, employeeDto);
            employeeDTOList.add(employeeDto);
        });

        // 封装成 Page 对象返回
        long total = empPage.getTotal();
        DataPage<EmployeeDTO> dataPage = new DataPage<>(employeeDTOList, total);
        return Result.success(dataPage);
    }
}
