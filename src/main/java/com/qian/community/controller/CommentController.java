package com.qian.community.controller;

import com.qian.community.entity.Comment;
import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Event;
import com.qian.community.event.EventProducer;
import com.qian.community.service.CommentService;
import com.qian.community.service.DiscussPostService;
import com.qian.community.service.UserService;
import com.qian.community.util.CommunityConstant;
import com.qian.community.util.CommunityUtil;
import com.qian.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * CommentController
 *
 * @author yang
 * @date 2022/2/14
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {


    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private EventProducer producer;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        commentService.addComment(comment);

        // 触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);

        // 判断是评论的是评论还是帖子 用户id不相同
        if (comment.getEntityType() == ENTITY_TYPE_POST) {

            // 触发事件
            Event event1 = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            producer.fireEvent(event1);

            DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(post.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment comment1 = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(comment1.getUserId());
        }
        producer.fireEvent(event);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
