package com.aw.librarysystem.service;

import com.aw.librarysystem.entity.BookCopy;
import com.aw.librarysystem.entity.BorrowingRecord;
import com.aw.librarysystem.entity.Member;
import com.aw.librarysystem.entity.enums.BookCopyStatus;
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

    @BeforeEach
    void setUp() {
        activeMember = new Member();
        activeMember.setId(1);
        activeMember.setStatus(MemberStatus.ACTIVE);
        activeMember.setMonthlyBorrows(0);

        availableCopy = new BookCopy();
        availableCopy.setId(101);
        availableCopy.setStatus(BookCopyStatus.AVAILABLE);
    }

    @Test
    void borrowBook_ShouldSucceed_WhenAllConditionsAreMet() {
        // Arrange
        when(memberRepository.findById(1)).thenReturn(Optional.of(activeMember));
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(availableCopy));
        when(borrowingRecordRepository.findByDueDateBeforeAndReturnDateIsNull(any(LocalDate.class))).thenReturn(Collections.emptyList());
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BorrowingRecord record = borrowingService.borrowBook(1, 101);

        // Assert
        assertNotNull(record);
        assertEquals(BookCopyStatus.BORROWED, availableCopy.getStatus());
        assertEquals(1, activeMember.getMonthlyBorrows());
        assertEquals(1, activeMember.getLifetimeBorrows());
        verify(memberRepository).save(activeMember);
        verify(bookCopyRepository).save(availableCopy);
        verify(borrowingRecordRepository).save(any(BorrowingRecord.class));
    }

    @Test
    void borrowBook_ShouldFail_WhenMemberIsNotActive() {
        activeMember.setStatus(MemberStatus.SUSPENDED);
        when(memberRepository.findById(1)).thenReturn(Optional.of(activeMember));
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(availableCopy));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            borrowingService.borrowBook(1, 101);
        });

        assertEquals("Member account is not active.", exception.getMessage());
    }

    @Test
    void borrowBook_ShouldFail_WhenBookIsNotAvailable() {
        availableCopy.setStatus(BookCopyStatus.BORROWED);
        when(memberRepository.findById(1)).thenReturn(Optional.of(activeMember));
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(availableCopy));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            borrowingService.borrowBook(1, 101);
        });

        assertEquals("Book copy is not available for borrowing.", exception.getMessage());
    }

    @Test
    void borrowBook_ShouldFail_WhenMemberHasOverdueBooks() {
        when(memberRepository.findById(1)).thenReturn(Optional.of(activeMember));
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(availableCopy));
        when(borrowingRecordRepository.findByDueDateBeforeAndReturnDateIsNull(any(LocalDate.class))).thenReturn(List.of(new BorrowingRecord()));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            borrowingService.borrowBook(1, 101);
        });

        assertEquals("Cannot borrow. Member has overdue books.", exception.getMessage());
    }

    @Test
    void borrowBook_ShouldFail_WhenMonthlyCapIsReached() {
        activeMember.setMonthlyBorrows(10); // Set to the cap
        when(memberRepository.findById(1)).thenReturn(Optional.of(activeMember));
        when(bookCopyRepository.findById(101)).thenReturn(Optional.of(availableCopy));
        when(borrowingRecordRepository.findByDueDateBeforeAndReturnDateIsNull(any(LocalDate.class))).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            borrowingService.borrowBook(1, 101);
        });

        assertEquals("Monthly borrowing limit has been reached.", exception.getMessage());
    }
}