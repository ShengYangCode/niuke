package com.qian.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * 评论表实体类
 *
 * @author yang
 * @date 2022/2/13
 */
@Data
public class Comment {

    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private String content;
    private int targetId;
    private int status;
    private Date createTime;

}
