package site.bzyl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.bzyl.constant.HttpConstant;
import site.bzyl.domain.Employee;
import site.bzyl.commom.Result;
import site.bzyl.dto.EmployeeDTO;
import site.bzyl.service.IEmployeeService;

import javax.servlet.http.HttpServletRequest;

@RestController /* @RestController 相当于 @Controller + 每个方法上的@ResponseBody 可以将 return 的对象转为json */
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;

    /*
    * 员工登录
    * */
    @PostMapping("/login")
    public Result<EmployeeDTO> login(HttpServletRequest request, @RequestBody Employee employee) {
        System.out.println(employee);
        return employeeService.login(request, employee.getUsername(), employee.getPassword());
    }

    /*
    * 员工退出
    * */
    @PostMapping("/logout")
    /* 泛型不知道写什么就写个String */
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute(HttpConstant.CURRENT_LOGIN_EMPLOYEE_ID);
        return Result.success("退出成功!");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public Result<String> addEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.addEmployee(request, employee);
    }

    @GetMapping("/page")
    public Result<Page> getPage(@Param("page") Integer page,
                                @Param("pageSize") Integer pageSize,
                                @Param("name") String name) {
        return employeeService.getPage(page, pageSize, name);
    }

    /**
     * 编辑员工信息（可以和 启用/禁用员工功能 复用）
     */
    @PutMapping
    public Result<String> updateEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.updateEmployee(request, employee);
    }
}
