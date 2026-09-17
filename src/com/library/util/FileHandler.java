package com.library.util;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing of Book, Member, and Transaction records
 * to CSV files under the data/ directory. Encapsulates all file I/O so
 * the rest of the application never deals with raw file handles.
 */
public class FileHandler {

    private final String dataDirectory;

    public FileHandler(String dataDirectory) {
        this.dataDirectory = dataDirectory;
        File dir = new File(dataDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String pathFor(String fileName) {
        return dataDirectory + File.separator + fileName;
    }

    // ---------- Books ----------

    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(pathFor("books.csv"));
        if (!file.exists()) {
            return books;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    books.add(Book.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load books.csv - " + e.getMessage());
        }
        return books;
    }

    public void saveBooks(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathFor("books.csv")))) {
            for (Book book : books) {
                writer.write(book.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving books.csv - " + e.getMessage());
        }
    }

    // ---------- Members ----------

    public List<Member> loadMembers() {
        List<Member> members = new ArrayList<>();
        File file = new File(pathFor("members.csv"));
        if (!file.exists()) {
            return members;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    members.add(Member.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load members.csv - " + e.getMessage());
        }
        return members;
    }

    public void saveMembers(List<Member> members) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathFor("members.csv")))) {
            for (Member member : members) {
                writer.write(member.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving members.csv - " + e.getMessage());
        }
    }

    // ---------- Transactions ----------

    public List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(pathFor("transactions.csv"));
        if (!file.exists()) {
            return transactions;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    transactions.add(Transaction.fromCsv(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load transactions.csv - " + e.getMessage());
        }
        return transactions;
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathFor("transactions.csv")))) {
            for (Transaction t : transactions) {
                writer.write(t.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions.csv - " + e.getMessage());
        }
    }
}
