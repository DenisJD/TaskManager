package hexlet.code;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class AppApplicationTests {
    @Test
    void test() throws Exception {
        assertThat(true).isEqualTo(true);
    }
}
