package com.library.model;

/**
 * Represents a library member who can borrow books.
 */
public class Member extends Person {
    private static final long serialVersionUID = 1L;

    private int booksBorrowed;
    private double outstandingFine;

    private static final int MAX_BOOKS_ALLOWED = 3;

    public Member(String id, String name, String email) {
        super(id, name, email);
        this.booksBorrowed = 0;
        this.outstandingFine = 0.0;
    }

    @Override
    public String getRole() {
        return "Member";
    }

    public int getBooksBorrowed() {
        return booksBorrowed;
    }

    public double getOutstandingFine() {
        return outstandingFine;
    }

    public void addFine(double amount) {
        this.outstandingFine += amount;
    }

    public void clearFine() {
        this.outstandingFine = 0.0;
    }

    public boolean canBorrow() {
        return booksBorrowed < MAX_BOOKS_ALLOWED && outstandingFine == 0.0;
    }

    public void incrementBorrowed() {
        booksBorrowed++;
    }

    public void decrementBorrowed() {
        if (booksBorrowed > 0) {
            booksBorrowed--;
        }
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | %-25s | Borrowed: %d | Fine: %.2f",
                id, name, email, booksBorrowed, outstandingFine);
    }

    public String toCsv() {
        return String.join(",", id, name.replace(",", ";"), email,
                String.valueOf(booksBorrowed), String.valueOf(outstandingFine));
    }

    public static Member fromCsv(String line) {
        String[] parts = line.split(",", -1);
        Member member = new Member(parts[0], parts[1].replace(";", ","), parts[2]);
        int borrowed = Integer.parseInt(parts[3]);
        for (int i = 0; i < borrowed; i++) {
            member.incrementBorrowed();
        }
        member.addFine(Double.parseDouble(parts[4]));
        return member;
    }
}
