package com.example.smartlibrary.service;

import com.example.smartlibrary.dto.ReliabilityResponse;
import com.example.smartlibrary.entity.*;
import com.example.smartlibrary.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    public LibraryService(BookRepository bookRepository, ReaderRepository readerRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.readerRepository = readerRepository;
        this.loanRepository = loanRepository;
    }

    public Reader createReader(Reader reader) {
        if (readerRepository.existsByEmail(reader.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email вже використовується");
        }
        return readerRepository.save(reader);
    }

    public Book createBook(Book book) {
        book.setStatus(BookStatus.AVAILABLE);
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public Loan issueBook(Long readerId, Long bookId) {
        Reader reader = readerRepository.findById(readerId).orElse(null);
        if (reader == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Читача не знайдено");
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Книгу не знайдено");
        }

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Книга вже видана");
        }

        List<Loan> activeLoans = loanRepository.findByReaderIdAndReturnDateIsNull(readerId);
        int maxBooks = isReaderReliable(readerId) ? 5 : 2;

        if (activeLoans.size() >= maxBooks) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Перевищено ліміт активних позик (" + maxBooks + ")");
        }

        book.setStatus(BookStatus.BORROWED);
        bookRepository.save(book);

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(reader, book, today, today.plusDays(14));
        return loanRepository.save(loan);
    }

    public boolean isReaderReliable(Long readerId) {
        if (!readerRepository.existsById(readerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Читача не знайдено");
        }

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        List<Loan> allLoans = loanRepository.findByReaderId(readerId);

        int recentReturnedCount = 0;

        for (Loan loan : allLoans) {
            if (loan.getReturnDate() == null) {
                if (loan.getDueDate().isBefore(today)) {
                    return false;
                }
            } else {
                if (!loan.getReturnDate().isBefore(thirtyDaysAgo)) {
                    if (loan.getReturnDate().isAfter(loan.getDueDate())) {
                        return false;
                    }
                    recentReturnedCount++;
                }
            }
        }

        return recentReturnedCount >= 3;
    }

    public ReliabilityResponse getReaderReliabilityInfo(Long readerId) {
        boolean reliable = isReaderReliable(readerId);
        return new ReliabilityResponse(readerId, reliable, reliable ? 5 : 2);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Позику не знайдено");
        }

        if (loan.getReturnDate() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Книга вже була повернута");
        }

        loan.setReturnDate(LocalDate.now());

        Book book = loan.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public List<Loan> getActiveLoansByReader(Long readerId) {
        if (!readerRepository.existsById(readerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Читача не знайдено");
        }
        return loanRepository.findByReaderIdAndReturnDateIsNull(readerId);
    }
}