package com.qian.community.service.impl;

import com.qian.community.service.LikeService;
import com.qian.community.util.RedisKeyUtil;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public void like(int userId, int entityType, int entityId, int entityUserId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                Boolean member = redisOperations.opsForSet().isMember(entityLikeKey, userId);
                redisOperations.multi();
                if (member) {
                    redisOperations.opsForSet().remove(entityLikeKey);
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    redisOperations.opsForSet().add(entityLikeKey, userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();
            }
        });
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

    // 查询用户总收到的点赞的数量
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
