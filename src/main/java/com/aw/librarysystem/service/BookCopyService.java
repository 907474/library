package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.Book;
import com.aw.librarysystem.entity.BookCopy;
import com.aw.librarysystem.entity.enums.BookCopyStatus;
import com.aw.librarysystem.repository.BookCopyRepository;
import com.aw.librarysystem.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }


    public Optional<BookCopy> findCopyById(Integer id) {
        return bookCopyRepository.findById(id);
    }


    @Transactional
    public BookCopy addBookCopy(Integer bookId, BookCopy newCopy) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found with ID: " + bookId));

        newCopy.setBook(book);
        newCopy.setStatus(BookCopyStatus.AVAILABLE); // Ensure new copies are available
        return bookCopyRepository.save(newCopy);
    }


    @Transactional
    public BookCopy updateBookCopy(Integer copyId, BookCopy updatedCopyDetails) {
        BookCopy existingCopy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new IllegalStateException("BookCopy not found with ID: " + copyId));

        // Update fields
        existingCopy.setLocation(updatedCopyDetails.getLocation());
        existingCopy.setCondition(updatedCopyDetails.getCondition());
        existingCopy.setPrice(updatedCopyDetails.getPrice());
        existingCopy.setAcquisitionDate(updatedCopyDetails.getAcquisitionDate());
        existingCopy.setNotes(updatedCopyDetails.getNotes());

        return bookCopyRepository.save(existingCopy);
    }

    public void deleteBookCopy(Integer copyId) {
        BookCopy copyToDelete = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new IllegalStateException("BookCopy not found with ID: " + copyId));

        // Business Rule: Do not allow deletion of a book that is currently on loan.
        if (copyToDelete.getStatus() == BookCopyStatus.BORROWED) {
            throw new IllegalStateException("Cannot delete a book copy that is currently borrowed.");
        }

        bookCopyRepository.deleteById(copyId);
    }

    public List<BookCopy> findAvailableCopies(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalStateException("Book not found with ID: " + bookId));
        return bookCopyRepository.findByBookAndStatus(book, BookCopyStatus.AVAILABLE);
    }
}