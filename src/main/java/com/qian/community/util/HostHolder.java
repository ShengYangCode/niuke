package com.qian.community.util;

import com.qian.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @description: 存放用户信息
 * @author: qian
 * @createDate: 2021/11/11
 */
@Component
public class HostHolder {

    private static ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }
    public static User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
