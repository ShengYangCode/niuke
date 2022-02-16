package com.qian.community.service.impl;


import com.qian.community.dao.LoginTicketMapper;
import com.qian.community.dao.UserMapper;
import com.qian.community.entity.LoginTicket;
import com.qian.community.entity.User;
import com.qian.community.service.UserService;
import com.qian.community.util.CommunityConstant;
import com.qian.community.util.MailClient;
import com.qian.community.util.communityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domin}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }


    @Override
    public Map<String, Object> register(User user) {

        Map<String, Object> map = new HashMap<>();

        if (user == null) throw new IllegalArgumentException("参数不能为null");
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        // 验证账号是否存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg","账号已存在");
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg","邮箱已存在");
            return map;
        }
        // 注册用户
        user.setSalt(communityUtil.getUUId().substring(0,5));
        user.setPassword(communityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(communityUtil.getUUId());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    @Override
    public int activation(int userId, String code) {

        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FALLURE;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // null 处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        // 合法性
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        // 验证密码
        String s = communityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(s)) {
            map.put("passwordMsg", "密码错误");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId()).setTicket(communityUtil.getUUId())
                .setStatus(0).setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    /**
     * 更改头像的业务方方法
     * @param userId
     * @param headerUrl
     * @return
     */
    @Override
    public int updateHeader(int userId, String headerUrl) {

        return userMapper.updateHeader(userId, headerUrl);
    }

    /**
     * 查询凭证的业务方法
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket) {

        return loginTicketMapper.selectByTicket(ticket);
    }

    @Override
    public void updatePassword(int id, String password, String newPassword) {

        User user = userMapper.selectById(id);

        String s = communityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(s)) {

            throw new RuntimeException("密码错误");
        } else {
            newPassword = communityUtil.md5(newPassword + user.getSalt());
            userMapper.updatePassword(id,newPassword);
        }

    }

    @Override
    public User findUserByName(String name) {
        return userMapper.selectByName(name);
    }
}
