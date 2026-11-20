package com.example.smartlibrary.dto;

public class ReliabilityResponse {
    private Long readerId;
    private boolean reliable;
    private int maxAllowedBooks;

    public ReliabilityResponse(Long readerId, boolean reliable, int maxAllowedBooks) {
        this.readerId = readerId;
        this.reliable = reliable;
        this.maxAllowedBooks = maxAllowedBooks;
    }

    public Long getReaderId() { return readerId; }
    public boolean isReliable() { return reliable; }
    public int getMaxAllowedBooks() { return maxAllowedBooks; }
}