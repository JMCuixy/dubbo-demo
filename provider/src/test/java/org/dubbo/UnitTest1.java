package org.dubbo;

import org.dubbo.service.UnitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020-04-29 10:28
 * @Description 平常的单元测试
 */
@SpringBootTest(classes = Provider.class)
@RunWith(SpringRunner.class)
public class UnitTest1 {

    @Autowired
    private UnitService unitService;

    @Test
    public void test() {
        System.out.println("----------------------");
        System.out.println(unitService.sayHello());
        System.out.println("----------------------");
    }
}
