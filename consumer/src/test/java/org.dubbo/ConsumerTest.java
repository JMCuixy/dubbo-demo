package org.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;
import org.dubbo.rservice.EchoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @author: cuixiuyin
 * @date: 2019/01/31 18:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Consumer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConsumerTest {

    @Reference
    private EchoService echoService;

    @Test
    public void test() {
        System.out.println("----------------------------------------");
        System.out.println(echoService.ehco());
        System.out.println("----------------------------------------");
    }

}
