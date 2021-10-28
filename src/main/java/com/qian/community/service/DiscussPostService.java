package com.qian.community.service;


import com.qian.community.entity.DiscussPost;


import java.util.List;

public interface DiscussPostService {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
    int selectDiscussPostRows(int userId);
}
