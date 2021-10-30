package com.qian.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @description:
 * @author: qian
 * @createDate: 2021/10/30
 */
public class communityUtil {

    // 生成随机字符串
    public static String getUUId() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    // MD5加密
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
