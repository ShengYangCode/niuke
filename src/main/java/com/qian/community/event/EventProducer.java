package com.qian.community.event;

import com.alibaba.fastjson.JSONObject;
import com.qian.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 事件生产者
 *
 * @author yang
 * @date 2022/2/24
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理事件
    public void fireEvent(Event event) {

        // 发送事件到指定的主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));

    }
}
