package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.Book;
import com.aw.librarysystem.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;

    @BeforeEach
    void setUp() {
        book1 = new Book();
        book1.setId(1);
        book1.setTitle("The Hobbit");
        book1.setAuthor("J.R.R. Tolkien");
        book1.setIsbn("12345");
    }

    @Test
    void findAllBooks_ShouldReturnListOfBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book1));

        List<Book> books = bookService.findAllBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        verify(bookRepository).findAll();
    }

    @Test
    void findBookById_ShouldReturnBook_WhenFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book1));

        Optional<Book> foundBook = bookService.findBookById(1);

        assertTrue(foundBook.isPresent());
        assertEquals("The Hobbit", foundBook.get().getTitle());
        verify(bookRepository).findById(1);
    }

    @Test
    void findBookById_ShouldReturnEmpty_WhenNotFound() {
        when(bookRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Book> foundBook = bookService.findBookById(99);

        assertFalse(foundBook.isPresent());
        verify(bookRepository).findById(99);
    }

    @Test
    void searchBooks_ShouldReturnMatchingBooks() {
        when(bookRepository.findByTitleContainingIgnoreCase("Hobbit")).thenReturn(List.of(book1));

        List<Book> results = bookService.searchBooks("Hobbit");

        assertEquals(1, results.size());
        assertEquals("The Hobbit", results.get(0).getTitle());
        verify(bookRepository).findByTitleContainingIgnoreCase("Hobbit");
    }

    @Test
    void searchBooks_ShouldReturnEmptyList_WhenNoMatch() {
        when(bookRepository.findByTitleContainingIgnoreCase("Matrix")).thenReturn(Collections.emptyList());

        List<Book> results = bookService.searchBooks("Matrix");

        assertTrue(results.isEmpty());
        verify(bookRepository).findByTitleContainingIgnoreCase("Matrix");
    }

    @Test
    void saveBook_ShouldSaveAndReturnBook_ForNewBook() {
        // Arrange
        Book newBook = new Book();
        newBook.setTitle("The Silmarillion");
        newBook.setIsbn("54321");
        when(bookRepository.findByIsbn("54321")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        // Act
        Book savedBook = bookService.saveBook(newBook);

        // Assert
        assertNotNull(savedBook);
        assertEquals("The Silmarillion", savedBook.getTitle());
        verify(bookRepository).save(newBook);
    }

    @Test
    void saveBook_ShouldThrowException_WhenIsbnExists() {
        Book newBook = new Book();
        newBook.setIsbn("12345");
        when(bookRepository.findByIsbn("12345")).thenReturn(Optional.of(book1));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookService.saveBook(newBook);
        });

        assertEquals("A book with ISBN 12345 already exists.", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBookById_ShouldCallDelete_WhenBookExists() {
        when(bookRepository.existsById(1)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1);

        assertDoesNotThrow(() -> bookService.deleteBookById(1));

        verify(bookRepository).deleteById(1);
    }

    @Test
    void deleteBookById_ShouldThrowException_WhenBookDoesNotExist() {
        when(bookRepository.existsById(99)).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookService.deleteBookById(99);
        });

        assertEquals("Book not found with ID: 99", exception.getMessage());
        verify(bookRepository, never()).deleteById(99);
    }
}