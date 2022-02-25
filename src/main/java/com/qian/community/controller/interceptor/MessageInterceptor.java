package com.qian.community.controller.interceptor;

import com.qian.community.entity.User;
import com.qian.community.service.MessageService;
import com.qian.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理私信数量的拦截器
 *
 * @author yang
 * @date 2022/2/25
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int letterUnreadCount = messageService.selectPrivateMessageCount(user.getId(), null);

            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);

            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);

        }
    }
}
