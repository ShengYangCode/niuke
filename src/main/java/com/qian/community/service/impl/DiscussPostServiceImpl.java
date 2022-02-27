package com.qian.community.service.impl;

import com.qian.community.dao.DiscussPostMapper;
import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Page;
import com.qian.community.service.DiscussPostService;
import com.qian.community.util.sensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @description:
 * @author: qian
 * @createDate: 2021/10/28
 */
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private sensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    @Override
    public int updateStates(int id, int states) {
        return discussPostMapper.updateStates(id, states);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        //对HTML标签进行转义
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        // 敏感词替换
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(Integer id) {
        return discussPostMapper.selectDiscussPostById(id);
    }


    @Override
    public int selectDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
