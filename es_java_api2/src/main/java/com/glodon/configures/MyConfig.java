package com.glodon.configures;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author liuwg-a
 * @date 2018/9/16 15:18
 * @description
 */
@Configuration
public class MyConfig {
    @Bean
    public TransportClient client() throws UnknownHostException {
        // 1. 创建配置文件
        Settings settings = Settings.builder()
                // 添加配置属性， 集群名
                .put("cluster.name", "jack")
                // 创建
                .build();
        // 2. 利用配置文件创建ES客户端
        TransportClient transportClient = new PreBuiltTransportClient(settings);
        // 配置TCP端口，这里注意新版的API由InetSocketTransportAddress改成了TransportAddress
        TransportAddress nodeAddress = new TransportAddress(InetAddress.getByName("localhost"), 9300);
        transportClient.addTransportAddress(nodeAddress);

        return transportClient;
    }
}
