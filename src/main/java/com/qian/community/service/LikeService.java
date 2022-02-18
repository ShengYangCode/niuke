package com.qian.community.service;

/**
 * LikeService
 *
 * @author yang
 * @date 2022/2/18
 */

public interface LikeService {


    void like(int userId, int entityType, int entityId);

    long findEntityLikeCount(int entityType, int entityId);

    int findEntityLikeStatus(int userId, int entityType, int entityId);
}
