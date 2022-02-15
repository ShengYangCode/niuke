package com.qian.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * 会话
 *
 * @author yang
 * @date 2022/2/15
 */
@Data
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;

}
