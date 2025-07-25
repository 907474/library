package com.aw.librarysystem.repository;

import com.aw.librarysystem.entity.BorrowingRecord;
import com.aw.librarysystem.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Integer> {

    List<BorrowingRecord> findByMemberOrderByBorrowDateDesc(Member member);

    List<BorrowingRecord> findByMemberAndReturnDateIsNull(Member member);

    long countByMemberAndReturnDateIsNull(Member member);

    List<BorrowingRecord> findByDueDateBeforeAndReturnDateIsNull(LocalDate today);

    Optional<BorrowingRecord> findByBookCopyIdAndReturnDateIsNull(Integer bookCopyId);
}