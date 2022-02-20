package com.qian.community.controller;

import com.qian.community.entity.User;
import com.qian.community.service.FollowService;
import com.qian.community.util.HostHolder;
import com.qian.community.util.communityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * FollowServiceController
 *
 * @author yang
 * @date 2022/2/20
 */
@Controller
public class FollowServiceController {

    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;


    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        return communityUtil.getJSONString(0, "关注成功");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.Unfollow(user.getId(), entityType, entityId);

        return communityUtil.getJSONString(0, "取消关注成功");
    }
}
