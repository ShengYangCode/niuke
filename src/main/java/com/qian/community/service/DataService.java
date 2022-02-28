package com.qian.community.service;

import com.qian.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DataService
 *
 * @author yang
 * @date 2022/2/27
 */
@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    // 将指定ip记入uv
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(dateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }
    // 获取指定日期范围内的uv
    public long calculateUV(Date start, Date end) {

        if (start != null || end != null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 获取日期范围内的key
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String uvKey = RedisKeyUtil.getUVKey(dateFormat.format(calendar.getTime()));
            list.add(uvKey);
            // 天数加一
            calendar.add(Calendar.DATE, 1);
        }

        // 统计区间
        String redisKey = RedisKeyUtil.getUVKey(dateFormat.format(start), dateFormat.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, list.toArray());

        // 返回统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    // 用户记入到DAU
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(dateFormat.format(new Date()));

        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }
    // 获取指定日期范围内的DAU
    public long calculateDAU(Date start, Date end) {
        if (start != null || end != null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 获取日期范围内的key
        List<byte[]> listKey = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        // 遍历
        while (!calendar.getTime().after(end)) {
            String dauKey = RedisKeyUtil.getDAUKey(dateFormat.format(calendar.getTime()));
            listKey.add(dauKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        // 进行or运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String key = RedisKeyUtil.getDAUKey(dateFormat.format(start), dateFormat.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        key.getBytes(), listKey.toArray(new byte[0][0]));
                return connection.bitCount(key.getBytes());
            }
        });
    }
}
