package com.qian.community.service;

import com.qian.community.entity.Comment;

import java.util.List;

/**
 * CommentService
 *
 * @author yang
 * @date 2022/2/13
 */
public interface CommentService {

    List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit);


    int findCountByEntity(int entityType, int entityId);


    void addComment(Comment comment);

    // 根据id查询评论
    Comment findCommentById(int id);
}
