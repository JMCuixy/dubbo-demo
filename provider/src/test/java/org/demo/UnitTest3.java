package org.demo;

import org.dubbo.dao.UnitDao;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Iterator;

import static org.mockito.Mockito.*;

/**
 * @Author 1099442418@qq.com
 * @Date 2020-04-29 11:35
 * @Description 1.0
 */
public class UnitTest3 {

    // 触发创建带有 @Mock 注解的对象
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private UnitDao unitDao;

    @Test
    public void test() {
        Iterator iterator = mock(Iterator.class);
        // mock 调用
        when(iterator.next()).thenReturn("hello");
        doReturn(1).when(unitDao).delete(anyLong());
        // 断言
        Assert.assertEquals("hello", iterator.next());
        Assert.assertEquals(new Integer(1), unitDao.delete(1L));
    }
}
