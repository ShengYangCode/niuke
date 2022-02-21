package com.qian.community;

import com.qian.community.dao.DiscussPostMapper;
import com.qian.community.dao.LoginTicketMapper;
import com.qian.community.dao.MessageMapper;
import com.qian.community.dao.UserMapper;
import com.qian.community.entity.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * MapperTests
 *
 * @author yang
 * @date 2022/2/15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectLetters() {
//        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
//        messages.forEach(System.out::println);


        int status = messageMapper.updateStatus(new ArrayList<>(), 1);
        System.out.println(status);
//        for (Message message : list) {
//            System.out.println(message);
//        }
//
//        int count = messageMapper.selectConversationCount(111);
//        System.out.println(count);
//        int i = messageMapper.selectCountConversations(111);
//        System.out.println(i);
//
//        list = messageMapper.selectLetters("111_112", 0, 10);
//        for (Message message : list) {
//            System.out.println(message);
//        }
//        List<Message> messages = messageMapper.selectOnePrivateMessages("111_112", 0, 10);
//        messages.forEach(System.out::println);
//
//        count = messageMapper.selectLetterCount("111_112");
//        System.out.println(count);
//
//        count = messageMapper.selectLetterUnreadCount(131, "111_131");
//        System.out.println(count);

    }
}
