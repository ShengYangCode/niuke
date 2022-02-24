package com.qian.community.controller;

import com.qian.community.entity.Event;
import com.qian.community.entity.Page;
import com.qian.community.entity.User;
import com.qian.community.event.EventProducer;
import com.qian.community.service.UserService;
import com.qian.community.service.impl.FollowService;
import com.qian.community.util.CommunityConstant;
import com.qian.community.util.HostHolder;
import com.qian.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * FollowServiceController
 *
 * @author yang
 * @date 2022/2/20
 */
@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer producer;


    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setEntityUserId(entityId);
        producer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "关注成功");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.Unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "取消关注成功");
    }
    // 关注人列表
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user1 = userService.findUserById(userId);
        if (user1 == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user1);
        page.setLimit(5);
        Long count = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        page.setRows(Integer.parseInt(count.toString()));
        page.setPath("/followees/" + userId);

        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (followees != null) {
            for (Map map : followees) {
                User user =(User) map.get("user");
                map.put("hasFollowed", hasFollowed(user.getId()));
            }
        }
        model.addAttribute("users", followees);
        return "/site/followee";
    }

    // 粉丝（关注着）列表
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user1 = userService.findUserById(userId);
        if (user1 == null) {
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user", user1);
        page.setLimit(5);
        Long count = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        page.setRows(Integer.parseInt(count.toString()));
        page.setPath("/followers/" + userId);

        List<Map<String, Object>> followers = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (followers != null) {
            for (Map map : followers) {
                User user =(User) map.get("user");
                map.put("hasFollowed", hasFollowed(user.getId()));
            }
        }
        model.addAttribute("users", followers);
        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) return false;
        return followService.hasFollower(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }


}
