package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.BorrowingRecord;
import com.aw.librarysystem.repository.BorrowingRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final BorrowingRecordRepository borrowingRecordRepository;

    public StatisticsService(BorrowingRecordRepository borrowingRecordRepository) {
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    public List<BorrowingRecord> getOverdueRecords() {
        return borrowingRecordRepository.findByDueDateBeforeAndReturnDateIsNull(LocalDate.now());
    }

    public Map<String, Long> getBorrowReturnStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Long> stats = new HashMap<>();
        long borrowedCount = borrowingRecordRepository.countByBorrowDateBetween(startDate, endDate);
        long returnedCount = borrowingRecordRepository.countByReturnDateBetween(startDate, endDate);
        stats.put("borrowed", borrowedCount);
        stats.put("returned", returnedCount);
        return stats;
    }
}