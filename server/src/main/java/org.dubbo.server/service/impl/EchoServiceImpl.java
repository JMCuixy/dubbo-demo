package org.dubbo.server.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.dubbo.server.service.EchoService;

/**
 * @Description:
 * @author: cuixiuyin
 * @date: 2018/12/19 22:17
 */
@Service(interfaceClass = EchoService.class)
public class EchoServiceImpl implements EchoService {

    @Override
    public String ehco() {
        return "echo";
    }
}
