package com.qian.community.service.impl;

import com.qian.community.service.LikeService;
import com.qian.community.util.RedisKeyUtil;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * LikeServiceImpl
 *
 * @author yang
 * @date 2022/2/18
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞
    @Override
    public void like(int userId, int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 判断用户是否点过赞
        Boolean aBoolean = redisTemplate.opsForSet().isMember(redisKey, userId);
        if (aBoolean) {
            redisTemplate.opsForSet().remove(redisKey,userId);
        } else {
            redisTemplate.opsForSet().add(redisKey, userId);
        }
    }
    // 查询实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(redisKey);
    }

    // 查询某人是否对某实体点过赞
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(redisKey, userId) ? 1 : 0;
    }
}
