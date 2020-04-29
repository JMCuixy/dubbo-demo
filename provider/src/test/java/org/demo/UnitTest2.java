package org.demo;

import org.dubbo.dao.UnitDao;
import org.dubbo.service.impl.UnitServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * @Author 1099442418@qq.com
 * @Date 2020-04-29 10:35
 * @Description 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitTest2 {

    @Mock
    private UnitDao unitDao;
    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    public void unitTest() {
        // mock 调用
        when(unitDao.delete(anyLong())).thenReturn(1);
        Assert.assertEquals("hello unit", unitService.sayHello());
    }
}
