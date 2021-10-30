package com.qian.community.service.impl;

import com.qian.community.dao.DiscussPostMapper;
import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Page;
import com.qian.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: qian
 * @createDate: 2021/10/28
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, Page page) {

        return discussPostMapper.selectDiscussPosts(userId,page.getOffset(),page.getLimit());
    }

    @Override
    public int selectDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}