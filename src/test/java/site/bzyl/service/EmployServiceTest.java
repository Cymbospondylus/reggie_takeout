package site.bzyl.service;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import site.bzyl.entity.Employee;

@SpringBootTest
@Slf4j
public class EmployServiceTest {
    @Autowired
    private IEmployeeService service;

    @Test
    public void testGetById() {
        Employee employee = service.getById(1);
        log.info("{}", employee);
    }

    @Test
    public void testLogin() {

    }
}
