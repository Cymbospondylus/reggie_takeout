package site.bzyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import site.bzyl.domain.Employee;
import site.bzyl.domain.Result;


public interface IEmployeeService extends IService<Employee> {

    Result login(String username, String password);
}
