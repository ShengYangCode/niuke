package com.qian.community.dao;

import com.qian.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * userid用于查询个人发布的帖子
     * @param userId
     * @return 全部的帖子
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param 用于给参数起别名
    // 如果只有一个参数并且在动态sql里使用就必须要用@Param起别名
    int selectDiscussPostRows(@Param("userId") int userId);
}
