package com.qian.community.controller;

import com.qian.community.entity.DiscussPost;
import com.qian.community.entity.Page;
import com.qian.community.service.EsService;
import com.qian.community.service.LikeService;
import com.qian.community.service.UserService;
import com.qian.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SearchController
 *
 * @author yang
 * @date 2022/2/26
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private EsService esService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {

        // 搜索帖子
        org.springframework.data.domain.Page<DiscussPost> discussPosts =
                esService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据 还需要用户的点赞的数据
        List<Map<String, Object>> resVo = new ArrayList<>();

        if (discussPosts != null) {
            for (DiscussPost post : discussPosts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞的数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                resVo.add(map);
            }
        }
        model.addAttribute("discussPosts", resVo);
        model.addAttribute("keyword", keyword);

        // 分页
        page.setPath("/search?keyword=" + keyword);
        // 获取总数据
        page.setRows(discussPosts == null ? 0 : (int) discussPosts.getTotalElements());

        return "/site/search";
    }
}
