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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BookCopyRepository bookCopyRepository;

    @InjectMocks
    private BorrowingService borrowingService;

    private Member activeMember;
    private BookCopy availableCopy;
    private BookCopy borrowedCopy;
    private BorrowingRecord activeRecord;

    @BeforeEach
    void setUp() {
        activeMember = new Member();
        activeMember.setId(1);
        activeMember.setStatus(MemberStatus.ACTIVE);
        activeMember.setMonthlyBorrows(0);

        availableCopy = new BookCopy();
        availableCopy.setId(101);
        availableCopy.setStatus(BookCopyStatus.AVAILABLE);

        borrowedCopy = new BookCopy();
        borrowedCopy.setId(102);
        borrowedCopy.setStatus(BookCopyStatus.BORROWED);

        activeRecord = new BorrowingRecord();
        activeRecord.setId(1001);
        activeRecord.setBookCopy(borrowedCopy);
        activeRecord.setMember(activeMember);
        activeRecord.setBorrowDate(LocalDate.now().minusDays(10));
        activeRecord.setDueDate(LocalDate.now().plusDays(20));
        activeRecord.setStatus(BorrowingStatus.BORROWED);
        activeRecord.setRenewalCount(0);
    }

    @Test
    void borrowBook_ShouldSucceed_WhenAllConditionsAreMet() {
        when(memberRepository.findById(1)).thenReturn(Optional.of(activeMember));
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(availableCopy));
        when(borrowingRecordRepository.findByDueDateBeforeAndReturnDateIsNull(any(LocalDate.class))).thenReturn(Collections.emptyList());
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowingRecord record = borrowingService.borrowBook(1, 101);

        assertNotNull(record);
        assertEquals(BookCopyStatus.BORROWED, availableCopy.getStatus());
        assertEquals(1, activeMember.getMonthlyBorrows());
    }

    // --- Return Scenarios ---

    @Test
    void returnBook_ShouldSucceed_WhenBookIsBorrowed() {
        when(bookCopyRepository.findById(102)).thenReturn(Optional.of(borrowedCopy));
        when(borrowingRecordRepository.findByBookCopyIdAndReturnDateIsNull(102)).thenReturn(Optional.of(activeRecord));
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(activeRecord);

        BorrowingRecord returnedRecord = borrowingService.returnBook(102);

        assertEquals(BorrowingStatus.RETURNED, returnedRecord.getStatus());
        assertEquals(LocalDate.now(), returnedRecord.getReturnDate());
        assertEquals(BookCopyStatus.AVAILABLE, borrowedCopy.getStatus());
        verify(bookCopyRepository).save(borrowedCopy);
        verify(borrowingRecordRepository).save(activeRecord);
    }

    @Test
    void returnBook_ShouldFail_WhenBookIsNotBorrowed() {
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(availableCopy));

        Exception exception = assertThrows(IllegalStateException.class, () -> borrowingService.returnBook(101));
        assertEquals("This book is not currently borrowed.", exception.getMessage());
    }

    // --- Renewal Scenarios ---

    @Test
    void renewBook_ShouldSucceed_WhenRenewable() {
        when(borrowingRecordRepository.findById(1001)).thenReturn(Optional.of(activeRecord));
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(activeRecord);

        LocalDate originalDueDate = activeRecord.getDueDate();
        BorrowingRecord renewedRecord = borrowingService.renewBook(1001);

        assertEquals(1, renewedRecord.getRenewalCount());
        assertTrue(renewedRecord.getDueDate().isAfter(originalDueDate));
    }

    @Test
    void renewBook_ShouldFail_WhenMaxRenewalsReached() {
        activeRecord.setRenewalCount(2); // Max renewals
        when(borrowingRecordRepository.findById(1001)).thenReturn(Optional.of(activeRecord));

        Exception exception = assertThrows(IllegalStateException.class, () -> borrowingService.renewBook(1001));
        assertEquals("Maximum renewal limit reached for this book.", exception.getMessage());
    }
}