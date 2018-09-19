package com.glodon.repositories;

import com.glodon.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author liuwg-a
 * @date 2018/9/17 8:55
 * @description 创建一个Repository的相关接口（可以加注解@RepositoryDefinition），这个接口必须继承Repository接口，
 * 这里的ElasticsearchRepository的上级上级。。。接口就是继承自Repository
 * ElasticsearchRepository接口泛型通常写成<存储的实体类型, 主键类型>，这样就将这个仓库定制化为某个文档的专用，比如这里
 * 就是Book文档的专用，我们也可以定义更加通用的Repository，比如
 *
 * @NoRepositoryBean
 * interface MyBaseRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {
 *   …
 * }
 *
 */
public interface BookRepository extends ElasticsearchRepository<Book, String> {

    /*
    在Spring Data reppository支持直接按名字解析，但是起的方法名要按照一定的规则来取，解析的时候会
    剥离以下的套词，然后对剩下的部分进行解析，查询的套词如下：find…By, read…By, query…By, count…By和get…By，
    反正不管怎样，By后面就是实际查询条件的开始，一般都是基于实体的属性作条件，条件之间使用And或者Or来连接，比如
    findBookByIdAndName, findBookByNameOrAuth
     */
    Book findByName(String name);
    List<Book> findByAuthor(String author);
    Book findBookById(String id);
}
