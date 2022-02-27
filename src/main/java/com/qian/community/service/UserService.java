package com.qian.community.service;

import com.qian.community.entity.LoginTicket;
import com.qian.community.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public interface UserService {

    User findUserById(int id);

    Map<String,Object> register(User user);

    int activation(int userId, String code);

    Map<String,Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    int updateHeader(int userId, String headerUrl);


    LoginTicket findLoginTicket(String ticket);

    void updatePassword(int id, String password, String newPassword);

    User findUserByName(String name);

    Collection<? extends GrantedAuthority> getAuthoritys(int userId);
}
