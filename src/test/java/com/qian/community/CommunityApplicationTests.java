package com.qian.community;


import com.qian.community.dao.DiscussPostMapper;
import com.qian.community.dao.UserMapper;
import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.User;
import com.qian.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    MailClient mailClient;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        discussPosts.forEach(x -> System.out.println(x));
    }

    @Test
    public void testApplicationContext1() {

        User user = userMapper.selectById(12);
        System.out.println(user);
    }

    @Test
    public void testApplicationContext2() {
        mailClient.sendMail("3167292824@qq.com","test","admin");
    }
    @Test
    public void testApplicationContext3() {
        Context context = new Context();
        context.setVariable("username","nihao");

        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.sendMail("3167292824@qq.com","欢迎",process);
    }

}