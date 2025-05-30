package com.js.provider.impl;



import com.js.api.pojo.User;
import com.js.api.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.UUID;

@Slf4j
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        log.info("客户端查询了ID={}的用户", id);
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        return User.builder().userName(UUID.randomUUID().toString())
                .id(id)
                .gender(random.nextBoolean())
                .build();
    }

    @Override
    public Integer insertUser(User user) {
        log.info("插入数据成功，用户名={}", user.getUserName());
        return user.getId();
    }

}
