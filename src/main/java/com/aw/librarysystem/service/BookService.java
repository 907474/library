package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.Book;
import com.aw.librarysystem.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }


    public Optional<Book> findBookById(Integer id) {
        return bookRepository.findById(id);
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCase(query);
    }

    @Transactional
    public Book saveBook(Book book) {
        if (book.getId() == null) {
            bookRepository.findByIsbn(book.getIsbn()).ifPresent(existingBook -> {
                throw new IllegalStateException("A book with ISBN " + existingBook.getIsbn() + " already exists.");
            });
        }
        return bookRepository.save(book);
    }

    public void deleteBookById(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalStateException("Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }
}