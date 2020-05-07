package org.dubbo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.util.StringUtils;

import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020-04-29 14:02
 * @Description 1.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({StringUtils.class})
public class TestUnit4 {

    @Test
    public void test() {
        mockStatic(StringUtils.class);
        when(StringUtils.getFilename(anyString())).thenReturn("localhost");
        Assert.assertEquals("localhost", StringUtils.getFilename(""));
    }
}
