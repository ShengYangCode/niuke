package com.qian.community;

import com.qian.community.dao.DiscussPostMapper;
import com.qian.community.entity.DiscussPost;
import com.qian.community.es.DiscussPostRepository;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * esTest
 *
 * @author yang
 * @date 2022/2/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class esTest {

    @Autowired
    private DiscussPostRepository repository;

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Test
    public void insert() {
        repository.save(discussPostMapper.selectDiscussPostById(241));
        repository.save(discussPostMapper.selectDiscussPostById(242));
        repository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void insert2() {
        repository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        repository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        repository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        repository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        repository.saveAll(discussPostMapper.selectDiscussPosts(112 , 0, 100));
        repository.saveAll(discussPostMapper.selectDiscussPosts(131 , 0, 100));
        repository.saveAll(discussPostMapper.selectDiscussPosts(132 , 0, 100));
        repository.saveAll(discussPostMapper.selectDiscussPosts(133 , 0, 100));
        repository.saveAll(discussPostMapper.selectDiscussPosts(134 , 0, 100));


    }

    @Test
    public void select() {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("<em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("<em>")
                ).build();
        // 对于用接口方式的查询，不会把标签合并在返回内容里
        // 底层获取到了高亮显示的值，但是不会进行处理
        Page<DiscussPost> page = repository.search(searchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }

    @Test
    public void select1() {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("<em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("<em>")
                ).build();

        elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {

                return null;
            }
        });
    }
}
