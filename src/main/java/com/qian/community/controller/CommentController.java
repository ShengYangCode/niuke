package com.qian.community.controller;

import com.qian.community.entity.Comment;
import com.qian.community.service.CommentService;
import com.qian.community.service.DiscussPostService;
import com.qian.community.service.UserService;
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
public class CommentController {


    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
