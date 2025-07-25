package com.aw.librarysystem.repository;

import com.aw.librarysystem.entity.Book;
import com.aw.librarysystem.entity.BookCopy;
import com.aw.librarysystem.entity.enums.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {

    long countByBookAndStatus(Book book, BookCopyStatus status);

    List<BookCopy> findByBookAndStatus(Book book, BookCopyStatus status);
}