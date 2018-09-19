package com.glodon.controller;

import com.glodon.models.Book;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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
     *
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
     *
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

    /**
     * 添加ES对象, Book的ID就是ES中存储的document的ID，所以最好不要为空，自定义生成的ID太浮夸
     *
     * @return ResponseEntity
     * @throws IOException
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<String> add(@RequestBody Book book) throws IOException {
        // 构造HTTP请求，第一个参数是请求方法，第二个参数是服务器的端点，host默认是http://localhost:9200，
        // endpoint直接指定为index/type的形式
        Request request = new Request("POST", new StringBuilder("/book/book/").
                append(book.getId()).toString());
        // 设置其他一些参数比如美化json
        request.addParameter("pretty", "true");

        JSONObject jsonObject = new JSONObject(book);
        System.out.println(jsonObject.toString());
        // 设置请求体并指定ContentType，如果不指定默认为APPLICATION_JSON
        request.setEntity(new NStringEntity(jsonObject.toString()));

        // 发送HTTP请求
        Response response = client.performRequest(request);

        // 获取响应体, id: AWXvzZYWXWr3RnGSLyhH
        String responseBody = EntityUtils.toString(response.getEntity());
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 根据id获取ES对象
     *
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getBookById(@PathVariable("id") String id) {
        Request request = new Request("GET", new StringBuilder("/book/book/").
                append(id).toString());
        // 添加json返回优化
        request.addParameter("pretty", "true");
        Response response = null;
        String responseBody = null;
        try {
            // 执行HHTP请求
            response = client.performRequest(request);
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return new ResponseEntity<>("can not found the book by your id", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 根据id更新Book
     *
     * @param id
     * @param book
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateBook(@PathVariable("id") String id, @RequestBody Book book) throws IOException {
        // 构造HTTP请求
        Request request = new Request("POST", new StringBuilder("/book/book/").
                append(id).append("/_update").toString());
        request.addParameter("pretty", "true");

        // 将数据丢进去，这里一定要外包一层“doc”，否则内部不能识别
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("doc", new JSONObject(book));
        request.setEntity(new NStringEntity(jsonObject.toString()));

        // 执行HTTP请求
        Response response = client.performRequest(request);

        // 获取返回的内容
        String responseBody = EntityUtils.toString(response.getEntity());

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 使用脚本更新Book
     * @param id
     * @param
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/update2/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateBook2(@PathVariable("id") String id, @RequestParam("name") String name) throws IOException {
        // 构造HTTP请求
        Request request = new Request("POST", new StringBuilder("/book/book/").
                append(id).append("/_update").toString());
        request.addParameter("pretty", "true");

        JSONObject jsonObject = new JSONObject();
        // 创建脚本语言，如果是字符变量，必须加单引号
        StringBuilder op1 = new StringBuilder("ctx._source.name=").append("'" + name + "'");
        jsonObject.put("script", op1);

        request.setEntity(new NStringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON));

        // 执行HTTP请求
        Response response = client.performRequest(request);

        // 获取返回的内容
        String responseBody = EntityUtils.toString(response.getEntity());

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteById(@PathVariable("id") String id) throws IOException {
        Request request = new Request("DELETE", new StringBuilder("/book/book/").append(id).toString());
        request.addParameter("pretty", "true");
        // 执行HTTP请求
        Response response = client.performRequest(request);
        // 获取结果
        String responseBody = EntityUtils.toString(response.getEntity());

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}
