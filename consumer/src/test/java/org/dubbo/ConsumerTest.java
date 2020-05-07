package org.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;
import org.dubbo.service.EchoService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void test() {
        System.out.println("----------------------------------------");
        System.out.println(echoService.ehco());
        System.out.println("----------------------------------------");
    }

    @Test
    public void mockitTest() {
        EchoService echoService = mock(EchoService.class);
        when(echoService.ehco()).thenReturn("Hello World");
        String ehco = echoService.ehco();
        Assert.assertEquals(ehco, "Hello World");
    }

}
