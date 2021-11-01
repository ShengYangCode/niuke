package com.qian.community.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @description:
 * @author: qian
 * @createDate: 2021/11/1
 */
@Data
@Accessors(chain = true)
public class LoginTicket {

    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
