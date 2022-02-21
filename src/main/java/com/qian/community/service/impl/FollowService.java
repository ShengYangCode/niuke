package com.qian.community.service.impl;

import com.qian.community.entity.User;
import com.qian.community.service.UserService;
import com.qian.community.util.CommunityConstant;
import com.qian.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * FollowService
 *
 * @author yang
 * @date 2022/2/20
 */
@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

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

    // 查询某用户关注的人
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> UserIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (UserIds == null) return null;
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : UserIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(id);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
    // 查询某用户的粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> UserIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (UserIds == null) return null;
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : UserIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(id);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
