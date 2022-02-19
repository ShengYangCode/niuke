package com.qian.community.controller;

import com.qian.community.entity.User;
import com.qian.community.service.LikeService;
import com.qian.community.util.HostHolder;
import com.qian.community.util.communityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * LikeController
 *
 * @author yang
 * @date 2022/2/18
 */
@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // 返回结果
        Map<String, Object> res = new HashMap<>();
        res.put("likeCount", likeCount);
        res.put("likeStatus", likeStatus);
        return communityUtil.getJSONString(0, null, res);
    }
}
