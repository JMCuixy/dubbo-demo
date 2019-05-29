package org.dubbo.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import org.dubbo.service.EchoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : cuixiuyin
 * @date : 2019/5/28
 */
@RestController
public class EchoApi {

    @Reference
    private EchoService echoService;

    @RequestMapping("/api/echo")
    public String echoApi() {
        return echoService.ehco();
    }
}
