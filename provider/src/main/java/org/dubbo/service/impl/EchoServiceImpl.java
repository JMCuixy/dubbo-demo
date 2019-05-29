package org.dubbo.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import org.dubbo.service.EchoService;

/**
 * @Description:
 * @author: cuixiuyin
 * @date: 2018/12/19 22:17
 */
@Service(interfaceClass = EchoService.class)
public class EchoServiceImpl implements EchoService {

    @Override
    public String ehco() {
        return "Dubbo";
    }
}
