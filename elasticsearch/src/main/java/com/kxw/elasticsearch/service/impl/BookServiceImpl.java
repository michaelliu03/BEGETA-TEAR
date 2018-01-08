/*
package com.kxw.elasticsearch.service.impl;

import java.util.List;

import com.kxw.elasticsearch.model.Book;
import com.kxw.elasticsearch.repository.BookRepository;
import com.kxw.elasticsearch.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

*/
/**
 * http://www.mkyong.com/spring-boot/spring-boot-spring-data-elasticsearch-example/
 *
 * spring data 使用
 *//*

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void delete(Book book) {
        bookRepository.delete(book);
    }

    public Book findOne(String id) {
        return bookRepository.findOne(id);
    }

    public Iterable<Book> findAll() {
        return bookRepository.findAll();
    }

    public Page<Book> findByAuthor(String author, PageRequest pageRequest) {
        return bookRepository.findByAuthor(author, pageRequest);
    }

    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

}*/
