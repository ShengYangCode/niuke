package com.qian.community.service.impl;


import com.qian.community.dao.LoginTicketMapper;
import com.qian.community.dao.UserMapper;
import com.qian.community.entity.LoginTicket;
import com.qian.community.entity.User;
import com.qian.community.service.UserService;
import com.qian.community.util.CommunityConstant;
import com.qian.community.util.MailClient;
import com.qian.community.util.CommunityUtil;
import com.qian.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Autowired
    private UserMapper userMapper;

/*    @Autowired
    private LoginTicketMapper loginTicketMapper;*/
    
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domin}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        User user = getCache(id);
        if (user == null) {
           return initCache(id);
        }
        return user;
        //    return userMapper.selectById(id);
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
        user.setSalt(CommunityUtil.getUUId().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.getUUId());
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
            clearCache(userId);
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
        String s = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(s)) {
            map.put("passwordMsg", "密码错误");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId()).setTicket(CommunityUtil.getUUId())
                .setStatus(0).setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    @Override
    public void logout(String ticket) {
      //  loginTicketMapper.updateStatus(ticket, 1);
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    /**
     * 更改头像的业务方方法
     * @param userId
     * @param headerUrl
     * @return
     */
    @Override
    public int updateHeader(int userId, String headerUrl) {


        int rows = userMapper.updateHeader(userId, headerUrl);
        if (rows != 0) {
            clearCache(userId);
        }
        return rows;
    }

    /**
     * 查询凭证的业务方法
     * @param ticket
     * @return
     */
    public LoginTicket findLoginTicket(String ticket) {
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    @Override
    public void updatePassword(int id, String password, String newPassword) {

        User user = userMapper.selectById(id);

        String s = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(s)) {

            throw new RuntimeException("密码错误");
        } else {
            newPassword = CommunityUtil.md5(newPassword + user.getSalt());
            userMapper.updatePassword(id,newPassword);
        }

    }

    @Override
    public User findUserByName(String name) {
        return userMapper.selectByName(name);
    }


    // 获取用户权限
    @Override
    public Collection<? extends GrantedAuthority> getAuthoritys(int userId) {
        User user = userMapper.selectById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1 :
                        return AUTHORITY_ADMIN;
                    case 2 :
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

    // 1. 从缓存中取user信息
    private User getCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    // 2. 取不到就初始化缓存
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }
    // 2. 消息变更就删缓存
    private void clearCache(int userId) {
        redisTemplate.delete(RedisKeyUtil.getUserKey(userId));
    }
}
