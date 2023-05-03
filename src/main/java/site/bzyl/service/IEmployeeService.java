package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.domain.Employee;
import site.bzyl.commom.Result;
import site.bzyl.dto.EmployeeDto;

import javax.servlet.http.HttpServletRequest;


public interface IEmployeeService extends IService<Employee> {

    Result<EmployeeDto> login(HttpServletRequest request, String username, String password);

    Result<String> addEmployee(HttpServletRequest request, Employee employee);
}
