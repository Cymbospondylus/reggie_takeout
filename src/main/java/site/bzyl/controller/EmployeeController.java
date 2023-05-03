package site.bzyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.domain.Employee;
import site.bzyl.commom.Result;
import site.bzyl.dto.EmployeeDto;
import site.bzyl.service.IEmployeeService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;

    @PostMapping("/login")
    public Result<EmployeeDto> login(HttpServletRequest request, @RequestBody Employee employee) {
        System.out.println(employee);
        return employeeService.login(request, employee.getUsername(), employee.getPassword());
    }
}
