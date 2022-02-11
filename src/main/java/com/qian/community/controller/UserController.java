package com.qian.community.controller;


import com.qian.community.annotation.LoginRequired;
import com.qian.community.entity.User;
import com.qian.community.service.UserService;
import com.qian.community.util.HostHolder;
import com.qian.community.util.communityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.security.util.Password;

import javax.mail.Multipart;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @description:
 * @author: qian
 * @createDate: 2021/11/11
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domin}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {

        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {

        // 没穿图片
        if (headerImage == null) {
            model.addAttribute("error", "你还没选择图片");
            return "/site/setting";
        }

        // 修改原始图片的名称避免文件重复导致覆盖
        String fileName = headerImage.getOriginalFilename();
        // 原始文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式错误");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = communityUtil.getUUId() + suffix;

        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("长传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败服务器发生异常" + e.getMessage());
        }

        // 更新当前用户的头像访问路径
        // http://localhost:8080/community/user/header/xxx.png
        User user = HostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName =  uploadPath + "/" + fileName;

        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (ServletOutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(fileName);) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer) ) != -1) {
                os.write(buffer, 0 , b);
            }
        } catch (IOException e) {
            logger.error("读取图形失败", e.getMessage());
            e.printStackTrace();
        }

    }

    @RequestMapping(path = "/password", method = RequestMethod.POST)
    public String uploadHeader(String oldPassword, String newPassword) {

        userService.updatePassword(HostHolder.getUser().getId(), oldPassword, newPassword);
        return "redirect:/logout";
    }
}
