package com.qian.community.service.impl;

import com.qian.community.dao.DiscussPostMapper;
import com.qian.community.dao.MessageMapper;
import com.qian.community.entity.Message;
import com.qian.community.service.MessageService;
import com.qian.community.util.sensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * MessageServiceImpl
 *
 * @author yang
 * @date 2022/2/15
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private com.qian.community.util.sensitiveFilter sensitiveFilter;

    @Override
    public List<Message> selectConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId,offset,limit);
    }

    @Override
    public int selectCountConversations(int userId) {
        return messageMapper.selectCountConversations(userId);
    }

    @Override
    public List<Message> selectOnePrivateMessages(String conversationId, int offset, int limit) {
        return messageMapper.selectOnePrivateMessages(conversationId,offset,limit);
    }

    @Override
    public int selectOnePrivateMessageCount(String conversationId) {
        return messageMapper.selectOnePrivateMessageCount(conversationId);
    }

    @Override
    public int selectPrivateMessageCount(int userId, String conversationId) {
        return messageMapper.selectPrivateMessageCount(userId, conversationId);
    }

    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }
}
