package site.bzyl.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmployeeMapperTest {
    @Autowired
    private EmployeeMapper mapper;

    @Test
    public void testSelectById() {
        System.out.println(mapper.selectById(1));
    }
}
