package site.bzyl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.entity.Employee;
import site.bzyl.commom.Result;
import site.bzyl.dto.EmployeeDTO;

import javax.servlet.http.HttpServletRequest;


public interface IEmployeeService extends IService<Employee> {

    Result<EmployeeDTO> login(HttpServletRequest request, String username, String password);

    Result<String> addEmployee(HttpServletRequest request, Employee employee);

    Result<Page> getPage(Integer page, Integer pageSize, String name);


}
