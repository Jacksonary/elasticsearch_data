package com.glodon.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author liuwg-a
 * @date 2018/9/17 8:48
 * @description , dataES自身提供了@Document注解文档，当然它也接受第三方的注解，比如JPA中的@Entity，
 * 但是别混用，比如在Book类上同时用@Entity和@Document注解，一个即可被识别
 * replicas = 0, refreshInterval = "-1"
 */
@Document(indexName = "book", type = "book", shards = 1, replicas = 0, refreshInterval = "-1")
public class Book {

    @Id
    private String id;
    private String name;
    private String author;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
