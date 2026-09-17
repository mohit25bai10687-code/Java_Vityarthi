package com.library.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single borrow/return transaction record.
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final int LOAN_PERIOD_DAYS = 14;
    public static final double FINE_PER_DAY = 5.0;

    private String transactionId;
    private String isbn;
    private String memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null while book is still out

    public Transaction(String transactionId, String isbn, String memberId, LocalDate issueDate) {
        this.transactionId = transactionId;
        this.isbn = isbn;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);
        this.returnDate = null;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getMemberId() {
        return memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markReturned(LocalDate date) {
        this.returnDate = date;
    }

    /**
     * Calculates the fine owed based on the return date relative to the due date.
     */
    public double calculateFine(LocalDate onDate) {
        LocalDate effectiveDate = (returnDate != null) ? returnDate : onDate;
        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, effectiveDate);
        return overdueDays > 0 ? overdueDays * FINE_PER_DAY : 0.0;
    }

    @Override
    public String toString() {
        String returned = (returnDate == null) ? "Not returned" : returnDate.format(FMT);
        return String.format("%-8s | ISBN:%-10s | Member:%-8s | Issued:%s | Due:%s | Returned:%s",
                transactionId, isbn, memberId, issueDate.format(FMT), dueDate.format(FMT), returned);
    }

    public String toCsv() {
        String returnedStr = (returnDate == null) ? "" : returnDate.format(FMT);
        return String.join(",", transactionId, isbn, memberId,
                issueDate.format(FMT), dueDate.format(FMT), returnedStr);
    }

    public static Transaction fromCsv(String line) {
        String[] parts = line.split(",", -1);
        Transaction t = new Transaction(parts[0], parts[1], parts[2], LocalDate.parse(parts[3], FMT));
        if (!parts[5].isEmpty()) {
            t.markReturned(LocalDate.parse(parts[5], FMT));
        }
        return t;
    }
}
