package com.qian.community.service.impl;

import com.qian.community.dao.CommentMapper;
import com.qian.community.dao.DiscussPostMapper;
import com.qian.community.entity.Comment;
import com.qian.community.service.CommentService;
import com.qian.community.util.CommunityConstant;
import com.qian.community.util.sensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * CommentServiceImpl
 *
 * @author yang
 * @date 2022/2/13
 */
@Service

public class CommentServiceImpl implements CommentService, CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;


    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private sensitiveFilter sensitiveFilter;

    @Override
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public int findCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public void addComment(Comment comment) {

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) { // 只有评论的是帖子才更新
//            DiscussPost discussPost = discussPostMapper.selectDiscussPostById(comment.getEntityId());
//            discussPostMapper.updateCommentCount(discussPost.getId(), discussPost.getCommentCount() + 1);
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(),count);
        }

    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }


}
