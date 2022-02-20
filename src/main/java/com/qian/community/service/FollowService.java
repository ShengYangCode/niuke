package com.qian.community.service;

import com.qian.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * FollowService
 *
 * @author yang
 * @date 2022/2/20
 */
@Service
public class FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 关注
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 用户的key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                // 粉丝的key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                operations.multi();
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());


                return operations.exec();
            }
        });
    }

    // 取关
    public void Unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 用户的key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                // 粉丝的key
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, userId);
                operations.multi();
                operations.opsForZSet().remove(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().remove(followerKey, userId, System.currentTimeMillis());


                return operations.exec();
            }
        });
    }

    // 获取用户的实体的关注数量
    public Long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey); // 统计出数量
    }
    // 获取实体的粉丝数量
    public Long findFollowerCount(int entity, int UserId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entity, UserId);
        return redisTemplate.opsForZSet().zCard(followerKey); // 统计出数量
    }

    // 查询用户是否关注了实体
    public boolean hasFollower(int userId, int entityType, int entityId) {

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }
}
