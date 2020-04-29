package org.dubbo.service.impl;

import org.dubbo.dao.UnitDao;
import org.dubbo.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author 1099442418@qq.com
 * @Date 2020-04-29 10:23
 * @Description 单元测试服务实现类
 */
@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitDao unitDao;

    @Override
    public String sayHello() {
        Integer delete = unitDao.delete(1L);
        System.out.println(delete);
        return "hello unit";
    }
}
