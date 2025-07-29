package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.enums.BookCopyStatus;
import com.aw.librarysystem.repository.BookCopyRepository;
import com.aw.librarysystem.repository.BorrowingRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final BookCopyRepository bookCopyRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    public DashboardService(BookCopyRepository bookCopyRepository, BorrowingRecordRepository borrowingRecordRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();

        long totalCopies = bookCopyRepository.count();
        long availableCopies = bookCopyRepository.countByBookAndStatus(null, BookCopyStatus.AVAILABLE); // Note: This repository method needs an update
        long overdueCount = borrowingRecordRepository.findByDueDateBeforeAndReturnDateIsNull(LocalDate.now()).size();

        stats.put("totalCopies", totalCopies);
        stats.put("availableCopies", availableCopies);
        stats.put("overdueCount", overdueCount);

        return stats;
    }
}