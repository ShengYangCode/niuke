package com.qian.community.dao;

import com.qian.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * CommentMapper
 *
 * @author yang
 * @date 2022/2/13
 */
@Mapper
public interface CommentMapper {

    /*根据实体类型查询评论*/
    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);

    /*根据实体类型查询同一个帖子评论的数量*/
    int selectCountByEntity(int entityType, int entityId);

    /*添加评论*/
    int insertComment(Comment comment);
}
