package com.aw.librarysystem.controller;

import com.aw.librarysystem.entity.Book;
import com.aw.librarysystem.entity.BookCopy;
import com.aw.librarysystem.repository.AudienceRepository;
import com.aw.librarysystem.repository.CategoryRepository;
import com.aw.librarysystem.service.BookCopyService;
import com.aw.librarysystem.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookController {

    private final BookService bookService;
    private final BookCopyService bookCopyService;
    private final CategoryRepository categoryRepository;
    private final AudienceRepository audienceRepository;

    public BookController(BookService bookService, BookCopyService bookCopyService,
                          CategoryRepository categoryRepository, AudienceRepository audienceRepository) {
        this.bookService = bookService;
        this.bookCopyService = bookCopyService;
        this.categoryRepository = categoryRepository;
        this.audienceRepository = audienceRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String listBooks(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Book> books;
        if (query != null && !query.isEmpty()) {
            books = bookService.searchBooks(query);
        } else {
            books = bookService.findAllBooks();
        }
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        return "books/list";
    }

    @GetMapping("/catalog")
    public String showCatalog(Model model) {
        model.addAttribute("books", bookService.findAllBooks());
        return "catalog";
    }

    @GetMapping("/books/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("allCategories", categoryRepository.findAll());
        model.addAttribute("allAudiences", audienceRepository.findAll());
        return "books/form";
    }

    @PostMapping("/books")
    public String saveBook(@ModelAttribute("book") Book book) {
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/books/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Book book = bookService.findBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        model.addAttribute("book", book);
        model.addAttribute("allCategories", categoryRepository.findAll());
        model.addAttribute("allAudiences", audienceRepository.findAll());
        return "books/form";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable("id") Integer id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }

    @GetMapping("/books/details/{id}")
    public String showBookDetails(@PathVariable("id") Integer id, Model model) {
        Book book = bookService.findBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));

        model.addAttribute("book", book);
        model.addAttribute("newCopy", new BookCopy());
        return "books/details";
    }

    @PostMapping("/books/{bookId}/copies")
    public String addBookCopy(@PathVariable("bookId") Integer bookId,
                              @ModelAttribute("newCopy") BookCopy newCopy) {
        bookCopyService.addBookCopy(bookId, newCopy);
        return "redirect:/books/details/" + bookId;
    }
}