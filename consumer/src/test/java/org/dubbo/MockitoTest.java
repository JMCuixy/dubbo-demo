package org.dubbo;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020-04-28 15:14
 * @Description 1.0
 */
public class MockitoTest {

    @Mock
    private Integer mIntent;
    /**
     * 规则 MockitoRule 会自动帮我们调用 MockitoAnnotations.initMocks(this) 去实例化出注解的成员变量，我们就无需手动进行初始化了。
     */
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    //多次触发返回不同值
    @Test
    public void whenThenReturn() {
        //mock一个Iterator类
        Iterator iterator = mock(Iterator.class);
        //预设当iterator调用next()时第一次返回hello，第n次都返回world
        when(iterator.next()).thenReturn("hello").thenReturn("world");
        //使用mock的对象
        String result = iterator.next() + " " + iterator.next() + " " + iterator.next();
        //验证结果
        assertEquals("hello world world", result);
    }

    @Test
    public void whenThenReturn2() {
        //mock一个Map类
        Map<String, String> mapParam = mock(Map.class);
        //预设当iterator调用next()时第一次返回hello，第n次都返回world
        when(mapParam.get("one")).thenReturn("one").thenReturn("first");
        when(mapParam.get("two")).thenReturn("two").thenReturn("second");
        //使用mock的对象
        String result1 = mapParam.get("one") + mapParam.get("two");
        String result2 = mapParam.get("one") + mapParam.get("two");

        String result3 = mapParam.get("three");
        System.out.println(result3);
        assertEquals(null, result3);

        //验证结果
        assertEquals("onetwo", result1);
        assertEquals("firstsecond", result2);
    }

    @Test
    public void whenThenReturnDependentOnMethodParameter() {
        Comparable<Integer> comparable = mock(Comparable.class);
        when(comparable.compareTo(anyInt())).thenReturn(-1);
        //assert
        assertEquals(-1, comparable.compareTo(9));

        Comparable<Integer> todo = mock(Comparable.class);
        when(todo.compareTo(isA(Integer.class))).thenReturn(0);
        //assert
        assertEquals(0, todo.compareTo(new Integer(1)));
    }

    //模拟抛出异常
    @Test(expected = IOException.class)//期望报IO异常
    public void whenThenThrow() throws IOException {
        OutputStream mock = Mockito.mock(OutputStream.class);
        //预设当流关闭时抛出异常
        Mockito.doThrow(new IOException()).when(mock).close();
        mock.close();
    }

    @Test
    public void doReturnWhen() {
        Properties properties = Mockito.mock(Properties.class);
        doReturn("42").when(properties).get("shoeSize");
        String value = (String) properties.get("shoeSize");
        assertEquals("42", value);
    }

    //用spy监控真实对象,设置真实对象行为
    @Test(expected = IndexOutOfBoundsException.class)
    public void spyOnRealObjects() {
        List list = new LinkedList();
        List spy = Mockito.spy(list);
        //下面预设的spy.get(0)会报错，因为会调用真实对象的get(0)，所以会抛出越界异常
        //Mockito.when(spy.get(0)).thenReturn(3);

        //使用doReturn-when可以避免when-thenReturn调用真实对象api
        Mockito.doReturn(999).when(spy).get(999);
        //预设size()期望值
        Mockito.when(spy.size()).thenReturn(100);
        //调用真实对象的api
        spy.add(1);
        spy.add(2);
        Assert.assertEquals(100, spy.size());
        Assert.assertEquals(1, spy.get(0));
        Assert.assertEquals(2, spy.get(1));
        Assert.assertEquals(999, spy.get(999));
    }

    @Test
    public void whenThenReturnLinkedListSpyWrong() {
        // Lets mock a LinkedList
        List<String> list = new LinkedList<>();
        List<String> spy = spy(list);
        // this does not work
        // real method is called so spy.get(0)
        // throws IndexOutOfBoundsException (list is still empty)
        when(spy.get(0)).thenReturn("foo");
        assertEquals("foo", spy.get(0));
    }

    @Test
    public void testVerify() {
        // create and configure mock
        MyClass test = Mockito.mock(MyClass.class);
        when(test.getUniqueId()).thenReturn(43);
        // call method testing on the mock with parameter 12
        test.testing(12);
        test.getUniqueId();
        test.getUniqueId();

        // now check if method testing was called with the parameter 12
        verify(test).testing(ArgumentMatchers.eq(12));

        // was the method called twice?
        verify(test, times(2)).getUniqueId();

        // other alternatives for verifiying the number of method calls for a method
        verify(test, atLeast(2)).getUniqueId();

        verify(test, never()).someMethod("never called");
        verify(test, atLeastOnce()).someMethod("called at least once");
        verify(test, times(5)).someMethod("called five times");
        verify(test, atMost(3)).someMethod("called at most 3 times");
        // This let's you check that no other methods where called on this object.
        // You call it after you have verified the expected method calls.
        verifyNoMoreInteractions(test);

        Comparator comparator = mock(Comparator.class);
        comparator.compare("nihao", "hello");
        //如果你使用了参数匹配，那么所有的参数都必须通过matchers来匹配
        Mockito.verify(comparator).compare(Mockito.anyString(), Mockito.eq("hello"));
        Mockito.verify(comparator).compare(Mockito.eq("nihao"), Mockito.eq("hello"));
        Mockito.verify(comparator).compare("nihao", "hello");
    }

    @Test
    public final void ArgumentCaptor() {
        List<String> asList = Arrays.asList("someElement_test", "someElement");
        final List<String> mockedList = mock(List.class);
        mockedList.addAll(asList);
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);

        verify(mockedList).addAll(captor.capture());
        final List<String> capturedArgument = captor.getValue();
        assertThat(capturedArgument, hasItem("someElement"));
    }

    private class MyClass {

        public Integer getUniqueId() {
            return 123;
        }

        public void testing(int i) {
        }

        public void someMethod(String called_at_least_once) {
        }
    }
}
