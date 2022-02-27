package com.qian.community.service;


import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Page;


import java.util.List;

public interface DiscussPostService {

    int selectDiscussPostRows(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(Integer id);

    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);


    // 修改帖子的类型（加精等）
    int updateType(int id, int type);

    // 修改帖子的状态
    int updateStates(int id, int states);
}
