package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.BookCopy;
import com.aw.librarysystem.entity.BorrowingRecord;
import com.aw.librarysystem.entity.Member;
import com.aw.librarysystem.entity.enums.BookCopyStatus;
import com.aw.librarysystem.entity.enums.BorrowingStatus;
import com.aw.librarysystem.entity.enums.MemberStatus;
import com.aw.librarysystem.repository.BookCopyRepository;
import com.aw.librarysystem.repository.BorrowingRecordRepository;
import com.aw.librarysystem.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingService {

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final MemberRepository memberRepository;
    private final BookCopyRepository bookCopyRepository;

    public BorrowingService(BorrowingRecordRepository borrowingRecordRepository,
                            MemberRepository memberRepository,
                            BookCopyRepository bookCopyRepository) {
        this.borrowingRecordRepository = borrowingRecordRepository;
        this.memberRepository = memberRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Transactional
    public BorrowingRecord borrowBook(Integer memberId, Integer copyId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Member not found with ID: " + memberId));

        BookCopy bookCopy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new IllegalStateException("Book copy not found with ID: " + copyId));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new IllegalStateException("Member account is not active.");
        }

        if (bookCopy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new IllegalStateException("Book copy is not available for borrowing.");
        }

        boolean hasOverdueBooks = !borrowingRecordRepository
                .findByDueDateBeforeAndReturnDateIsNull(LocalDate.now()).isEmpty();
        if (hasOverdueBooks) {
            throw new IllegalStateException("Cannot borrow. Member has overdue books.");
        }

        int monthlyCap = 10;
        if (member.getMonthlyBorrows() >= monthlyCap) {
            throw new IllegalStateException("Monthly borrowing limit has been reached.");
        }

        member.setMonthlyBorrows(member.getMonthlyBorrows() + 1);
        member.setLifetimeBorrows(member.getLifetimeBorrows() + 1);
        memberRepository.save(member);

        bookCopy.setStatus(BookCopyStatus.BORROWED);
        bookCopyRepository.save(bookCopy);

        BorrowingRecord newRecord = new BorrowingRecord();
        newRecord.setMember(member);
        newRecord.setBookCopy(bookCopy);
        newRecord.setBorrowDate(LocalDate.now());
        int borrowingPeriodDays = 30;
        newRecord.setDueDate(LocalDate.now().plusDays(borrowingPeriodDays));
        newRecord.setStatus(BorrowingStatus.BORROWED);

        return borrowingRecordRepository.save(newRecord);
    }

    @Transactional
    public BorrowingRecord returnBook(Integer copyId) {
        BookCopy bookCopy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new IllegalStateException("Book copy not found with ID: " + copyId));

        if (bookCopy.getStatus() != BookCopyStatus.BORROWED) {
            throw new IllegalStateException("This book is not currently borrowed.");
        }

        BorrowingRecord recordToReturn = borrowingRecordRepository
                .findByBookCopyIdAndReturnDateIsNull(copyId)
                .orElseThrow(() -> new IllegalStateException("No active borrowing record found for this book copy."));

        recordToReturn.setReturnDate(LocalDate.now());
        recordToReturn.setStatus(BorrowingStatus.RETURNED);
        bookCopy.setStatus(BookCopyStatus.AVAILABLE);

        bookCopyRepository.save(bookCopy);
        return borrowingRecordRepository.save(recordToReturn);
    }

    @Transactional
    public BorrowingRecord renewBook(Integer recordId) {
        BorrowingRecord recordToRenew = borrowingRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalStateException("Borrowing record not found with ID: " + recordId));

        if (recordToRenew.getStatus() == BorrowingStatus.RETURNED) {
            throw new IllegalStateException("Cannot renew a book that has already been returned.");
        }

        int maxRenewals = 2;
        if (recordToRenew.getRenewalCount() >= maxRenewals) {
            throw new IllegalStateException("Maximum renewal limit reached for this book.");
        }

        int renewalPeriodDays = 14;
        recordToRenew.setDueDate(recordToRenew.getDueDate().plusDays(renewalPeriodDays));
        recordToRenew.setRenewalCount(recordToRenew.getRenewalCount() + 1);

        if (recordToRenew.getStatus() == BorrowingStatus.OVERDUE && recordToRenew.getDueDate().isAfter(LocalDate.now())) {
            recordToRenew.setStatus(BorrowingStatus.BORROWED);
        }

        return borrowingRecordRepository.save(recordToRenew);
    }

    public List<BorrowingRecord> getMemberHistory(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("Member not found with ID: " + memberId));
        return borrowingRecordRepository.findByMemberOrderByBorrowDateDesc(member);
    }

    @Transactional
    public void updateOverdueStatuses() {
        List<BorrowingRecord> overdueRecords = borrowingRecordRepository
                .findByDueDateBeforeAndReturnDateIsNull(LocalDate.now());

        for (BorrowingRecord record : overdueRecords) {
            if (record.getStatus() != BorrowingStatus.OVERDUE) {
                record.setStatus(BorrowingStatus.OVERDUE);
                borrowingRecordRepository.save(record);
            }
        }
    }

    public Page<BorrowingRecord> findAllRecords(Pageable pageable) {
        return borrowingRecordRepository.findAll(pageable);
    }
}