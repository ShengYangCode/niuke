package com.qian.community.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息队列事件对象
 *
 * @author yang
 * @date 2022/2/24
 */
@Data
@Accessors(chain = true)
public class Event implements Serializable {

    private String topic; // 主题
    private int userId; // 用户id
    private int entityType; // 实体类型
    private int entityId; // 实体id
    private int entityUserId; // 实体作者
    private Map<String, Object> data = new HashMap<>();

    public Event setData(String key, Object value) {
        data.put(key, value);
        this.data = data;
        return this;
    }
}
