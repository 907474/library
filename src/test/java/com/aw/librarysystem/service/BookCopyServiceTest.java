package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.Book;
import com.aw.librarysystem.entity.BookCopy;
import com.aw.librarysystem.entity.enums.BookCopyStatus;
import com.aw.librarysystem.repository.BookCopyRepository;
import com.aw.librarysystem.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookCopyService bookCopyService;

    private Book book;
    private BookCopy bookCopy;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1);
        book.setTitle("The Hobbit");

        bookCopy = new BookCopy();
        bookCopy.setId(101);
        bookCopy.setLocation("FIC-TOL-01");
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);
        bookCopy.setBook(book);
    }

    @Test
    void addBookCopy_ShouldSaveAndReturnCopy() {
        // Arrange
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);

        // Act
        BookCopy newCopyDetails = new BookCopy();
        newCopyDetails.setLocation("FIC-TOL-02");
        BookCopy savedCopy = bookCopyService.addBookCopy(1, newCopyDetails);

        // Assert
        assertNotNull(savedCopy);
        assertEquals(book, savedCopy.getBook());
        assertEquals(BookCopyStatus.AVAILABLE, savedCopy.getStatus());
        verify(bookCopyRepository).save(newCopyDetails);
    }

    @Test
    void deleteBookCopy_ShouldSucceed_WhenCopyIsAvailable() {
        // Arrange
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(bookCopy));
        doNothing().when(bookCopyRepository).deleteById(101);

        // Act & Assert
        assertDoesNotThrow(() -> bookCopyService.deleteBookCopy(101));
        verify(bookCopyRepository).deleteById(101);
    }

    @Test
    void deleteBookCopy_ShouldThrowException_WhenCopyIsBorrowed() {
        // Arrange
        bookCopy.setStatus(BookCopyStatus.BORROWED);
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(bookCopy));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookCopyService.deleteBookCopy(101);
        });

        assertEquals("Cannot delete a book copy that is currently borrowed.", exception.getMessage());
        verify(bookCopyRepository, never()).deleteById(anyInt());
    }

    @Test
    void updateBookCopy_ShouldThrowException_WhenCopyNotFound() {
        // Arrange
        when(bookCopyRepository.findById(999)).thenReturn(Optional.empty());
        BookCopy updatedDetails = new BookCopy();

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookCopyService.updateBookCopy(999, updatedDetails);
        });

        assertEquals("BookCopy not found with ID: 999", exception.getMessage());
    }
}