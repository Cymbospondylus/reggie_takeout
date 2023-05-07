package site.bzyl.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommonServiceTest {

    @Test
    public void suffixTest() {
        String originalFilename = "fksdjhfsdjkfhsd.jpg";
        // 截取出后缀.xxx
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        System.out.println(suffix);
    }
}
