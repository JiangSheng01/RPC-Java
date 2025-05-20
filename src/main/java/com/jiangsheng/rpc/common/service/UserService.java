package com.jiangsheng.rpc.common.service;

import com.jiangsheng.rpc.common.pojo.User;

public interface UserService {
    User getUserByUserId(Integer id);
    Integer insertUser(User user);
}
