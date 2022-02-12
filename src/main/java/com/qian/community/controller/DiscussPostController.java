package com.qian.community.controller;

import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.User;
import com.qian.community.service.DiscussPostService;
import com.qian.community.service.UserService;
import com.qian.community.util.HostHolder;
import com.qian.community.util.communityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.ParagraphView;
import java.util.Date;

/**
 * DiscussPostController
 *
 * @author yang
 * @date 2022/2/12
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;


    @Autowired
    private HostHolder hostHolder;

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
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        // 贴子信息
        model.addAttribute("post",post);

        // 用户信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);
        return "/site/discuss-detail";
    }

}
