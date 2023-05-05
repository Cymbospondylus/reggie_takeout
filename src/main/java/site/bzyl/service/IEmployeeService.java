package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.domain.DataPage;
import site.bzyl.domain.Employee;
import site.bzyl.commom.Result;
import site.bzyl.dto.EmployeeDTO;

import javax.servlet.http.HttpServletRequest;


public interface IEmployeeService extends IService<Employee> {

    Result<EmployeeDTO> login(HttpServletRequest request, String username, String password);

    Result<String> addEmployee(HttpServletRequest request, Employee employee);

    Result<DataPage<EmployeeDTO>> getPage(Integer page, Integer pageSize);
}
