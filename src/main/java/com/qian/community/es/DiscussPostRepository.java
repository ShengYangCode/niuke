package com.qian.community.es;

import com.qian.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * DiscussPostRepository
 *
 * @author yang
 * @date 2022/2/26
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {



}
