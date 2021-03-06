package com.qian.community.event;

import com.alibaba.fastjson.JSONObject;
import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Event;
import com.qian.community.entity.Message;
import com.qian.community.service.DiscussPostService;
import com.qian.community.service.EsService;
import com.qian.community.service.MessageService;
import com.qian.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;
import org.thymeleaf.util.MapUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 事件消费者
 *
 * @author yang
 * @date 2022/2/24
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);


    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EsService esService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {

        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        // 解析json
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }
        // 发送通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId",event.getEntityId());

        // 把不方便存的数据都存入content中
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {

        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        // 解析json
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        esService.addDiscussPost(post);
    }

    // 消费删除帖子事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {

        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        // 解析json
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        esService.deleteDiscussPost(event.getEntityId());
    }
}
