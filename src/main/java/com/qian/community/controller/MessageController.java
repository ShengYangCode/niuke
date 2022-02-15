package com.qian.community.controller;

import com.qian.community.entity.Message;
import com.qian.community.entity.Page;
import com.qian.community.entity.User;
import com.qian.community.service.CommentService;
import com.qian.community.service.MessageService;
import com.qian.community.service.UserService;
import com.qian.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MessageController
 *
 * @author yang
 * @date 2022/2/15
 */
@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;
    // 总私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectCountConversations(user.getId()));

        List<Message> messageList = messageService.selectConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();

        if (messageList != null) {
            for (Message message : messageList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("unreadCount", messageService.selectPrivateMessageCount(user.getId(),message.getConversationId()));
                map.put("letterCount", messageService.selectOnePrivateMessageCount(message.getConversationId()));

                // 获奖发信息给我的用户id
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        // 查询总会话中未读消息数量
        int letterUnreadCount = messageService.selectPrivateMessageCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        return "/site/letter";
    }
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {

        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.selectOnePrivateMessageCount(conversationId));

        // 单个私信详情
        List<Message> messages = messageService.selectOnePrivateMessages(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();

        if (messages != null) {
            for (Message message : messages) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));
        return "/site/letter-detail";

    }

    private User getLetterTarget(String conversationId) {
        String[] s = conversationId.split("_");
        Integer id1 = Integer.valueOf(s[0]);
        Integer id2 = Integer.valueOf(s[1]);
        if (hostHolder.getUser().getId() == id1) {
            return userService.findUserById(id2);
        } else {
            return userService.findUserById(id1);
        }
    }

}
