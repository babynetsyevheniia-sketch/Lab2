package com.example.smartlibrary.repository;

import com.example.smartlibrary.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByReaderId(Long readerId);
    List<Loan> findByReaderIdAndReturnDateIsNull(Long readerId);
}