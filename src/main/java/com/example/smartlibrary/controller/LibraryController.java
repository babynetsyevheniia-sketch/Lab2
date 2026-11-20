package com.example.smartlibrary.controller;

import com.example.smartlibrary.dto.LoanRequest;
import com.example.smartlibrary.dto.ReliabilityResponse;
import com.example.smartlibrary.entity.Book;
import com.example.smartlibrary.entity.Loan;
import com.example.smartlibrary.entity.Reader;
import com.example.smartlibrary.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/readers")
    @ResponseStatus(HttpStatus.CREATED)
    public Reader createReader(@RequestBody Reader reader) {
        return libraryService.createReader(reader);
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        return libraryService.createBook(book);
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return libraryService.getAllBooks();
    }

    @PostMapping("/loans")
    @ResponseStatus(HttpStatus.CREATED)
    public Loan issueBook(@RequestBody LoanRequest request) {
        return libraryService.issueBook(request.getReaderId(), request.getBookId());
    }

    @GetMapping("/readers/{readerId}/reliability")
    public ReliabilityResponse checkReliability(@PathVariable Long readerId) {
        return libraryService.getReaderReliabilityInfo(readerId);
    }

    @PostMapping("/loans/{loanId}/return")
    public Loan returnBook(@PathVariable Long loanId) {
        return libraryService.returnBook(loanId);
    }

    @GetMapping("/readers/{readerId}/loans/active")
    public List<Loan> getActiveLoans(@PathVariable Long readerId) {
        return libraryService.getActiveLoansByReader(readerId);
    }
}