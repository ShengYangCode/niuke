package com.qian.community.service;


import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Page;


import java.util.List;

public interface DiscussPostService {

    List<DiscussPost> selectDiscussPosts(int userId, Page page);
    int selectDiscussPostRows(int userId);
}
