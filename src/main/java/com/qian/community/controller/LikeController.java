package com.qian.community.controller;

import com.qian.community.entity.Event;
import com.qian.community.entity.User;
import com.qian.community.event.EventProducer;
import com.qian.community.service.LikeService;
import com.qian.community.util.CommunityConstant;
import com.qian.community.util.HostHolder;
import com.qian.community.util.CommunityUtil;
import com.qian.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * LikeController
 *
 * @author yang
 * @date 2022/2/18
 */
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    private EventProducer producer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // 返回结果
        Map<String, Object> res = new HashMap<>();
        res.put("likeCount", likeCount);
        res.put("likeStatus", likeStatus);

        // 触发点赞事件 取消点赞不触发
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityId(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);

            producer.fireEvent(event);
        }

        // 存入redis用于计数帖子分数
        if (entityType == ENTITY_TYPE_POST) {
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }

        return CommunityUtil.getJSONString(0, null, res);
    }
}
