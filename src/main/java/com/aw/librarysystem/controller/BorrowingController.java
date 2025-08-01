package com.aw.librarysystem.controller;

import com.aw.librarysystem.entity.BorrowingRecord;
import com.aw.librarysystem.service.BorrowingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/borrowing")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @GetMapping
    public String listAllRecords(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<BorrowingRecord> recordPage = borrowingService.findAllRecords(pageable);
        model.addAttribute("recordPage", recordPage);
        return "borrowing/list";
    }

    @GetMapping("/borrow")
    public String showBorrowForm() {
        return "borrowing/borrow-form";
    }

    @PostMapping("/borrow")
    public String processBorrow(@RequestParam Integer memberId, @RequestParam Integer copyId, RedirectAttributes redirectAttributes) {
        try {
            borrowingService.borrowBook(memberId, copyId);
            redirectAttributes.addFlashAttribute("successMessage", "Book copy #" + copyId + " successfully borrowed by member #" + memberId);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/borrowing/borrow";
    }

    @GetMapping("/return")
    public String showReturnForm() {
        return "borrowing/return-form";
    }

    @PostMapping("/return")
    public String processReturn(@RequestParam Integer copyId, RedirectAttributes redirectAttributes) {
        try {
            borrowingService.returnBook(copyId);
            redirectAttributes.addFlashAttribute("successMessage", "Book copy #" + copyId + " successfully returned.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/borrowing/return";
    }

    @GetMapping("/renew")
    public String showRenewForm() {
        return "borrowing/renew-form";
    }

    @PostMapping("/renew")
    public String processRenew(@RequestParam Integer copyId, RedirectAttributes redirectAttributes) {
        try {
            BorrowingRecord activeRecord = borrowingRecordRepository.findByBookCopyIdAndReturnDateIsNull(copyId)
                    .orElseThrow(() -> new IllegalStateException("No active borrowing record found for this book copy."));

            borrowingService.renewBook(activeRecord.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Book copy #" + copyId + " successfully renewed.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/borrowing/renew";
    }
}