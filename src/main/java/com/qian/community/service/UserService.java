package com.qian.community.service;

import com.qian.community.entity.User;

import java.util.Map;

public interface UserService {

    User findUserById(int id);

    Map<String,Object> register(User user);

    int activation(int userId, String code);

}
