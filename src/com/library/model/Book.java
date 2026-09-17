package com.library.model;

import java.io.Serializable;

/**
 * Represents a book in the library catalogue.
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Decrements available copies when a book is issued.
     */
    public void decrementAvailable() {
        if (availableCopies > 0) {
            availableCopies--;
        }
    }

    /**
     * Increments available copies when a book is returned.
     */
    public void incrementAvailable() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    @Override
    public String toString() {
        return String.format("%-12s | %-30s | %-20s | %-15s | %3d/%3d",
                isbn, title, author, genre, availableCopies, totalCopies);
    }

    /**
     * Serializes the book to a CSV row for persistence.
     */
    public String toCsv() {
        return String.join(",", isbn, escape(title), escape(author), escape(genre),
                String.valueOf(totalCopies), String.valueOf(availableCopies));
    }

    public static Book fromCsv(String line) {
        String[] parts = line.split(",", -1);
        Book book = new Book(parts[0], unescape(parts[1]), unescape(parts[2]), unescape(parts[3]),
                Integer.parseInt(parts[4]));
        int available = Integer.parseInt(parts[5]);
        while (book.getAvailableCopies() > available) {
            book.decrementAvailable();
        }
        return book;
    }

    private static String escape(String value) {
        return value.replace(",", ";");
    }

    private static String unescape(String value) {
        return value.replace(";", ",");
    }
}
