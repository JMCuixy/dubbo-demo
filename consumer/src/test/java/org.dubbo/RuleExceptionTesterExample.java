package org.dubbo;

import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;

/**
 * @Author xiuyin.cui@luckincoffee.com
 * @Date 2020-04-28 18:29
 * @Description 1.0
 */
public class RuleExceptionTesterExample {

    public ExpectedException exception = ExpectedException.none();

    public void throwsIllegalArgumentExceptionIfIconIsNull(){
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Negative value not allowed");
    }
}
