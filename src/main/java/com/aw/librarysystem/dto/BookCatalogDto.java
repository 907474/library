package com.aw.librarysystem.dto;

import com.aw.librarysystem.entity.Book;

public class BookCatalogDto {

    private final Book book;
    private final long availableCopyCount;

    public BookCatalogDto(Book book, long availableCopyCount) {
        this.book = book;
        this.availableCopyCount = availableCopyCount;
    }

    public Book getBook() {
        return book;
    }

    public long getAvailableCopyCount() {
        return availableCopyCount;
    }
}