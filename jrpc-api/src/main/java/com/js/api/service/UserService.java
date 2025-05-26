package com.js.api.service;

import com.js.api.annotation.Retryable;
import com.js.api.pojo.User;

public interface UserService {
    @Retryable
    User getUserByUserId(Integer id);
    @Retryable
    Integer insertUser(User user);
}
