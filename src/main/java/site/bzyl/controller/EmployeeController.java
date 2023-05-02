package site.bzyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.bzyl.domain.Employee;
import site.bzyl.domain.Result;
import site.bzyl.service.IEmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;

    @PostMapping("/login")
    public Result login(@RequestBody Employee employee) {
        System.out.println(employee);
        return employeeService.login(employee.getUsername(), employee.getPassword());
    }
}
