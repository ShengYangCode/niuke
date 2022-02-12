package com.qian.community.service;


import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Page;


import java.util.List;

public interface DiscussPostService {

    int selectDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(Integer id);

    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);
}
