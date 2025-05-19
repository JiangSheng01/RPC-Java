package com.jiangsheng.rpc.common.service.impl;

import com.jiangsheng.rpc.common.pojo.User;
import com.jiangsheng.rpc.common.service.UserService;

import java.util.Random;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(int id) {
        System.out.println("客户端查询了"+id+"的用户");
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        return User.builder().userName(UUID.randomUUID().toString())
                .id(id)
                .sex(random.nextBoolean())
                .build();
    }

    @Override
    public Integer insertUser(User user) {
        System.out.println("插入数据成功"+user.getUserName());
        return user.getId();
    }

}
