package org.dubbo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * @Author 1099442418@qq.com
 * @Date 2020-04-28 17:52
 * @Description 1.0
 */
@RunWith(Parameterized.class)
public class ParameterizedTestFields {

    @Parameterized.Parameter(0)
    public int m1;
    @Parameterized.Parameter(1)
    public int m2;
    @Parameterized.Parameter(2)
    public int result;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] objects = new Object[][]{{1, 2, 2}, {4, 5, 20}};
        return Arrays.asList(objects);
    }

    @Test
    public void testMultiplyException() {
        MyClass myClass = new MyClass();
        Assert.assertEquals("Result", result, myClass.mul(m1, m2));
    }

    private class MyClass {

        public int mul(int m1, int m2) {
            return m1 * m2;
        }
    }
}
