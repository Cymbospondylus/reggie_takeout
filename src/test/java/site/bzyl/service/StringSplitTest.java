package site.bzyl.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class StringSplitTest {
    @Test
    public void test() {
        String ids = "1397862198033297410,1397860242057375745,1397860578738352129,1397860792492666881,1397860963880316929,1397861683434139649";
        List<String> stringList = Arrays.asList(ids.split(","));
        stringList.forEach(s -> System.out.println(s));
    }
}
