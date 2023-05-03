package site.bzyl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.domain.DataPage;
import site.bzyl.domain.Employee;
import site.bzyl.commom.Result;
import site.bzyl.dto.EmployeeDto;
import site.bzyl.service.IEmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController /* @RestController 相当于 @Controller + 每个方法上的@ResponseBody 可以将 return 的对象转为json */
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;

    /*
    * 员工登录
    * */
    @PostMapping("/login")
    public Result<EmployeeDto> login(HttpServletRequest request, @RequestBody Employee employee) {
        System.out.println(employee);
        return employeeService.login(request, employee.getUsername(), employee.getPassword());
    }

    /*
    * 员工退出
    * */
    @PostMapping("/logout")
    /* 泛型不知道写什么就写个String */
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employeeId");
        return Result.success("退出成功!");
    }

    /*
    * 新增员工
    * */
    @PostMapping
    public Result<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.addEmployee(request, employee);
    }

    @GetMapping("/page")
    public Result<DataPage<EmployeeDto>> getPage(@Param("page") Integer page, @Param("pageSize") Integer pageSize) {
        return employeeService.getPage(page, pageSize);
    }
}
