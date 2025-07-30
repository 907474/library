package com.aw.librarysystem.repository;

import com.aw.librarysystem.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Book> searchByTitleOrIsbn(@Param("query") String query, Pageable pageable);
}