package glodon.controllers;

import glodon.model.Book;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author liuwg-a
 * @date 2018/9/14 13:57
 * @description
 */
@RestController
@RequestMapping("/es")
public class BookController {
    @Autowired
    private TransportClient client;

    @RequestMapping(value = "/go")
    public String go() {
        return "go1";
    }

    /**
     * 根据id查询
     *
     * @param id book id
     */
    @RequestMapping(value = "/book", method = RequestMethod.GET)
    public ResponseEntity<java.util.Map<String, Object>> get(@RequestParam("id") String id) {
        GetResponse result = client.prepareGet("book", "novel", id).get();
        return new ResponseEntity<>(result.getSource(), HttpStatus.OK);
    }

    /**
     * 添加文档
     *
     * @param id   book id
     * @param name book name
     */
    @RequestMapping(value = "/book", method = RequestMethod.POST)
    public ResponseEntity<String> add(@RequestParam("id") String id, @RequestParam("name") String name) {
        try {
            // 构造ES的文档，这里注意startObject()开始构造，结束构造一定要加上endObject()
            XContentBuilder content = XContentFactory.jsonBuilder().startObject().
                    field("id", id)
                    .field("name", name)
                    .endObject();
            IndexResponse result = client.prepareIndex("book", "novel")
                    .setSource(content).get();
            return new ResponseEntity<>(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据id删除book
     *
     * @param id book id
     */
    @RequestMapping(value = "/book/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable(value = "id") String id) {
        DeleteResponse result = client.prepareDelete("book", "novel", id).get();
        return new ResponseEntity<>(result.getResult().toString(), HttpStatus.OK);
    }

    /**
     * 更新文档
     */
    @RequestMapping(value = "/book", method = RequestMethod.PUT)
    public ResponseEntity<String> update(@RequestBody Book book) {
        System.out.println(book);
        // 根据id查询
        UpdateRequest updateRequest = new UpdateRequest("book", "novel", book.getId().toString());
        try {
            XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
            if (StringUtils.isNotBlank(book.getName())) {
                contentBuilder.field("name", book.getName());
            }
            contentBuilder.endObject();
            updateRequest.doc(contentBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 进行更新
        UpdateResponse updateResponse = new UpdateResponse();
        try {
            updateResponse = client.update(updateRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(updateResponse.getResult().toString(), HttpStatus.OK);
    }

}
