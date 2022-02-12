package com.qian.community.util;


import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器
 *
 * @author yang
 * @date 2022/2/12
 */
@Component
public class sensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(sensitiveFilter.class);

    // 替换符
    private final String ERRORWORD = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct // bean初始化时这个方法就会被调用
    public void init() {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive_words.txt");

             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {

                this.addkeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败" + e.getMessage());
        }


    }

    // 敏感词添加到前缀树
    private void addkeyword(String keyword) {
        TrieNode node = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = node.getSubNode(c);
            if (subNode == null) {
                subNode = new TrieNode();
                node.addSubNode(c,subNode);
            }
            node = subNode;
            if (i == keyword.length() - 1) {
                node.isKeywordEnd = true;
            }
        }
    }

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){
            if(position < text.length()) {
                Character c = text.charAt(position);

                // 跳过符号
                if (isSymbol(c)) {
                    if (tempNode == rootNode) {
                        begin++;
                        sb.append(c);
                    }
                    position++;
                    continue;
                }

                // 检查下级节点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    // 以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    // 进入下一个位置
                    position = ++begin;
                    // 重新指向根节点
                    tempNode = rootNode;
                }
                // 发现敏感词
                else if (tempNode.isKeywordEnd()) {
                    sb.append(ERRORWORD);
                    begin = ++position;
                    tempNode = rootNode;
                }
                // 检查下一个字符
                else {
                    position++;
                }
            }
            // position遍历越界仍未匹配到敏感词
            else{
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }
        }
        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    private class TrieNode {


        // 子节点
        private Map<Character, TrieNode> subNode = new HashMap<>();

        // 是否已敏感词结尾
        private boolean isKeywordEnd = false;

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNode.put(c, node);
        }
        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNode.get(c);
        }
    }
}
