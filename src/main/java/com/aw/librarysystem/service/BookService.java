package com.aw.librarysystem.service;

import com.aw.librarysystem.dto.BookCatalogDto;
import com.aw.librarysystem.entity.Book;
import com.aw.librarysystem.entity.Category;
import com.aw.librarysystem.entity.enums.BookCopyStatus;
import com.aw.librarysystem.repository.BookCopyRepository;
import com.aw.librarysystem.repository.BookRepository;
import com.aw.librarysystem.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository, BookCopyRepository bookCopyRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Book> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> findBookById(Integer id) {
        return bookRepository.findById(id);
    }

    public Page<Book> searchBooks(String query, Pageable pageable) {
        return bookRepository.searchByTitleOrIsbn(query, pageable);
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

    @Transactional
    public void addCategoryToBook(Integer bookId, Integer categoryId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found with ID: " + bookId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalStateException("Category not found with ID: " + categoryId));
        book.getCategories().add(category);
        bookRepository.save(book);
    }

    @Transactional
    public void removeCategoryFromBook(Integer bookId, Integer categoryId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found with ID: " + bookId));
        book.getCategories().removeIf(category -> category.getId().equals(categoryId));
        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public List<BookCatalogDto> getCatalogInfo() {
        List<Book> books = bookRepository.findAll();
        List<BookCatalogDto> catalog = new ArrayList<>();
        for (Book book : books) {
            long availableCount = book.getCopies().stream()
                    .filter(copy -> copy.getStatus() == BookCopyStatus.AVAILABLE)
                    .count();
            catalog.add(new BookCatalogDto(book, availableCount));
        }
        return catalog;
    }
}