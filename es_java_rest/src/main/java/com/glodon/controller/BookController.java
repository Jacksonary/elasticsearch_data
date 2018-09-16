package com.glodon.controller;

import com.glodon.models.Book;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.RequestLine;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author liuwg-a
 * @date 2018/9/16 18:41
 * @description
 */
@RestController
@RequestMapping("/rest/book")
public class BookController {
    @Autowired
    private RestClient client;

//    // RequestOptions类保存应在同一应用程序中的多个请求之间共享的部分请求
//    private static final RequestOptions COMMON_OPTIONS;
//
//    static {
//        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        // 添加所有请求所需的任何标头。
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        // 自定义响应使用者
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
//        COMMON_OPTIONS = builder.build();
//    }

    @RequestMapping(value = "/go", method = RequestMethod.GET)
    public ResponseEntity<String> go() {
        return new ResponseEntity<>("go", HttpStatus.OK);
    }

    /**
     * 同步执行HTTP请求
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/es", method = RequestMethod.GET)
    public ResponseEntity<String> getEsInfo() throws IOException {
        // 构造HTTP请求，第一个参数是请求方法，第二个参数是服务器的端点，host默认是http://localhost:9200
        Request request = new Request("GET", "/");
//        // 设置其他一些参数比如美化json
//        request.addParameter("pretty", "true");
//        // 设置请求体
//        request.setEntity(new NStringEntity("{\"json\":\"text\"}", ContentType.APPLICATION_JSON));
//        // 还可以将其设置为String，默认为ContentType为application/json
//        request.setJsonEntity("{\"json\":\"text\"}");

        /*
        performRequest是同步的，将阻塞调用线程并在请求成功时返回Response，如果失败则抛出异常
        内部属性可以取出来通过下面的方法
         */
        Response response = client.performRequest(request);
//        // 获取请求行
//        RequestLine requestLine = response.getRequestLine();
//        // 获取host
//        HttpHost host = response.getHost();
//        // 获取状态码
//        int statusCode = response.getStatusLine().getStatusCode();
//        // 获取响应头
//        Header[] headers = response.getHeaders();
        // 获取响应体
        String responseBody = EntityUtils.toString(response.getEntity());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }


    /**
     * 异步执行HTTP请求
     * @return
     */
    @RequestMapping(value = "/es/asyn", method = RequestMethod.GET)
    public ResponseEntity<String> asynchronous() {
        Request request = new Request(
                "GET",
                "/");
        client.performRequestAsync(request, new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                System.out.println("异步执行HTTP请求并成功");
            }

            @Override
            public void onFailure(Exception exception) {
                System.out.println("异步执行HTTP请求并失败");
            }
        });
        return null;
    }

    /**
     * 并行异步执行HTTP请求
     */
    @RequestMapping(value = "/ps", method = RequestMethod.POST)
    public void parallAsyn(@RequestBody Book[] documents) {
//        final CountDownLatch latch = new CountDownLatch(documents.length);
//        for (int i = 0; i < documents.length; i++) {
//            Request request = new Request("PUT", "/posts/doc/" + i);
//            //let's assume that the documents are stored in an HttpEntity array
//            request.setEntity(documents[i]);
//            client.performRequestAsync(
//                    request,
//                    new ResponseListener() {
//                        @Override
//                        public void onSuccess(Response response) {
//
//                            latch.countDown();
//                        }
//
//                        @Override
//                        public void onFailure(Exception exception) {
//
//                            latch.countDown();
//                        }
//                    }
//            );
//        }
//        latch.await();
    }
}
