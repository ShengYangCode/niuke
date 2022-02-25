package com.qian.community.dao;

import com.qian.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MessageMapper
 *
 * @author yang
 * @date 2022/2/15
 */
@Mapper
public interface MessageMapper {

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

    // 添加消息
    int insertMessage(Message message);

    //更新消息的状态
    int updateStatus(List<Integer> ids, Integer status);

    /**
     * 查询某个主题下的最新通知
     * @param userId 用户id
     * @param topic 主题
     * @return 一条message 信息
     */
    Message selectLatestNotice(Integer userId, String topic);

    /**
     * 查询某个主题下的通知数量
     * @param userId 用户id
     * @param topic 主题
     * @return 通知的数量
     */
    int selectNoticeCount(Integer userId, String topic);

    /**
     * 查询某个主题下的未读的通知数量
     * @param userId 用户id
     * @param topic 主题 (不传主题表示查询用户通知所有的未读信息)
     * @return 未读的通知数量
     */
    int selectNoticeUnreadCount(Integer userId, String topic);


    /**
     * 查询某个主题下的所有列表
     * @param userId 用户id
     * @param topic 主题
     * @param offset 分页参数
     * @param limit
     * @return 主题下的所有列表
     */
    List<Message> selectNotices(Integer userId, String topic, int offset, int limit);
}
