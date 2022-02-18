package com.qian.community.controller;

import com.qian.community.entity.Comment;
import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Page;
import com.qian.community.entity.User;
import com.qian.community.service.CommentService;
import com.qian.community.service.DiscussPostService;
import com.qian.community.service.LikeService;
import com.qian.community.service.UserService;
import com.qian.community.util.CommunityConstant;
import com.qian.community.util.HostHolder;
import com.qian.community.util.communityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.ParagraphView;
import java.util.*;

/**
 * DiscussPostController
 *
 * @author yang
 * @date 2022/2/12
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return communityUtil.getJSONString(403,"你还没有登录");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        return communityUtil.getJSONString(0,"发布成功！") ;

    }
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        // 贴子信息
        model.addAttribute("post",post);

        // 用户信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        // 点赞信息
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        // 点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);

        // 评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 查询出帖子一级评论列表
        List<Comment> comment = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 响应给前端的集合
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (comment != null) {
            // 还需要给前端返回回用户的数据 头像等
            for (Comment comment1 : comment) {
                // 给每个评论封装数据
                Map<String, Object> commentVo = new HashMap<>();
                User user1 = userService.findUserById(comment1.getUserId());
                commentVo.put("user", user1);
                commentVo.put("comment", comment1);

                // 点赞信息
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment1.getId());
                commentVo.put("likeCount", likeCount);
                // 点赞状态
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment1.getId());
                commentVo.put("likeStatus", likeStatus);

                // 查询二级评论
                List<Comment> comment2 = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment1.getId(), 0, Integer.MAX_VALUE);
                // 封装二级评论的数据， 一个一级评论 对应多个二级评论
                List<Map<String, Object>> towCommentVoList = new ArrayList<>();
                if (comment2 != null) {
                    for (Comment reply : comment2) {
                        // 给每个评论封装数据
                        Map<String, Object> commentVo2 = new HashMap<>();
                        User user2 = userService.findUserById(reply.getUserId());
                        commentVo2.put("user", user2);
                        commentVo2.put("reply", reply);

                        // 判断回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        commentVo2.put("target", target);
                        towCommentVoList.add(commentVo2);

                        // 点赞信息
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        commentVo2.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        commentVo2.put("likeStatus", likeStatus);
                    }

                }
                // 添加二级评论的数据
                commentVo.put("replys", towCommentVoList);
                // 一级回复数量
                int replyCount = commentService.findCountByEntity(ENTITY_TYPE_COMMENT, comment1.getId());
                commentVo.put("replyCount", replyCount);

                // 封装好的一级评论  一级评论里封装了二级评论
                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }

}
