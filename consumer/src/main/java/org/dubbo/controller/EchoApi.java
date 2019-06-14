package org.dubbo.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dubbo.service.EchoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : cuixiuyin
 * @date : 2019/5/28
 */
@RestController
@Api(value = "EchoApi")
public class EchoApi {

    @Reference
    private EchoService echoService;

    @RequestMapping(value = "/api/get", method = RequestMethod.GET)
    @ApiOperation(value = "echoApi", httpMethod = "GET")
    public String echoApi() {
        return echoService.ehco();
    }

    @RequestMapping(value = "/api/post", method = RequestMethod.POST)
    @ApiOperation(value = "postEchoApi", httpMethod = "POST")
    public String postEchoApi() {
        return echoService.ehco();
    }
}
