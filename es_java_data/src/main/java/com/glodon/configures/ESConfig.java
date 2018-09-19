package com.glodon.configures;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author liuwg-a
 * @date 2018/9/18 14:04
 * @description 基础包的注释驱动配置，配置自动扫描的repositories根目录
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.glodon.repositories")
public interface ESConfig {
}
