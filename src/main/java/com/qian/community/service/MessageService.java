package com.qian.community.service;

import com.qian.community.entity.Message;

import java.util.List;

/**
 * MessageService
 *
 * @author yang
 * @date 2022/2/15
 */
public interface MessageService {

    /*查询当前用户的会话列表，每一个列表里只有一条最新的消息*/
    List<Message> selectConversations(int userId, int offset, int limit);

    /*查询用户会话的数量*/
    int selectCountConversations(int userId);

    /*查询某个会话的私信的信息列表*/
    List<Message> selectOnePrivateMessages(String conversationId, int offset, int limit);

    /*查询某个会话的私信的数量*/
    int selectOnePrivateMessageCount(String conversationId);


    /*查询未读私信的数量*/
    int selectPrivateMessageCount(int userId, String conversationId);
}
