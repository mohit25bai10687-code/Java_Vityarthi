package com.library.service;

import com.library.exceptions.BookNotAvailableException;
import com.library.exceptions.DuplicateRecordException;
import com.library.exceptions.InvalidMemberException;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.util.FileHandler;

import java.time.LocalDate;
import java.util.*;

/**
 * Core controller class coordinating Book Management, Member Management,
 * and Transaction Management. Acts as the single entry point (facade)
 * that the UI layer (Main) interacts with, keeping persistence and
 * business rules encapsulated.
 */
public class Library {

    private final Map<String, Book> books;         // key: ISBN
    private final Map<String, Member> members;      // key: member ID
    private final List<Transaction> transactions;
    private final FileHandler fileHandler;
    private int transactionCounter;

    public Library(String dataDirectory) {
        this.fileHandler = new FileHandler(dataDirectory);
        this.books = new LinkedHashMap<>();
        this.members = new LinkedHashMap<>();
        this.transactions = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        for (Book b : fileHandler.loadBooks()) {
            books.put(b.getIsbn(), b);
        }
        for (Member m : fileHandler.loadMembers()) {
            members.put(m.getId(), m);
        }
        transactions.addAll(fileHandler.loadTransactions());
        transactionCounter = transactions.size();
    }

    /**
     * Persists all in-memory data back to CSV files. Called after every
     * mutating operation to keep storage consistent (durability).
     */
    public void saveData() {
        fileHandler.saveBooks(new ArrayList<>(books.values()));
        fileHandler.saveMembers(new ArrayList<>(members.values()));
        fileHandler.saveTransactions(transactions);
    }

    // ===================== MODULE 1: BOOK MANAGEMENT =====================

    public void addBook(String isbn, String title, String author, String genre, int copies)
            throws DuplicateRecordException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty.");
        }
        if (copies <= 0) {
            throw new IllegalArgumentException("Number of copies must be positive.");
        }
        if (books.containsKey(isbn)) {
            throw new DuplicateRecordException("A book with ISBN " + isbn + " already exists.");
        }
        books.put(isbn, new Book(isbn, title, author, genre, copies));
        saveData();
    }

    public void updateBook(String isbn, String title, String author, String genre) throws BookNotAvailableException {
        Book book = books.get(isbn);
        if (book == null) {
            throw new BookNotAvailableException("No book found with ISBN " + isbn);
        }
        if (title != null && !title.isEmpty()) book.setTitle(title);
        if (author != null && !author.isEmpty()) book.setAuthor(author);
        if (genre != null && !genre.isEmpty()) book.setGenre(genre);
        saveData();
    }

    public void deleteBook(String isbn) throws BookNotAvailableException {
        if (!books.containsKey(isbn)) {
            throw new BookNotAvailableException("No book found with ISBN " + isbn);
        }
        books.remove(isbn);
        saveData();
    }

    public List<Book> listBooks() {
        return new ArrayList<>(books.values());
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Book b : books.values()) {
            if (b.getTitle().toLowerCase().contains(lower)
                    || b.getAuthor().toLowerCase().contains(lower)
                    || b.getIsbn().toLowerCase().contains(lower)
                    || b.getGenre().toLowerCase().contains(lower)) {
                results.add(b);
            }
        }
        return results;
    }

    // ===================== MODULE 2: MEMBER MANAGEMENT =====================

    public void registerMember(String id, String name, String email) throws DuplicateRecordException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be empty.");
        }
        if (members.containsKey(id)) {
            throw new DuplicateRecordException("A member with ID " + id + " already exists.");
        }
        members.put(id, new Member(id, name, email));
        saveData();
    }

    public void updateMember(String id, String name, String email) throws InvalidMemberException {
        Member member = members.get(id);
        if (member == null) {
            throw new InvalidMemberException("No member found with ID " + id);
        }
        if (name != null && !name.isEmpty()) member.setName(name);
        if (email != null && !email.isEmpty()) member.setEmail(email);
        saveData();
    }

    public void deleteMember(String id) throws InvalidMemberException {
        Member member = members.get(id);
        if (member == null) {
            throw new InvalidMemberException("No member found with ID " + id);
        }
        if (member.getBooksBorrowed() > 0) {
            throw new InvalidMemberException("Cannot delete member with unreturned books.");
        }
        members.remove(id);
        saveData();
    }

    public List<Member> listMembers() {
        return new ArrayList<>(members.values());
    }

    // ===================== MODULE 3: TRANSACTION MANAGEMENT =====================

    /**
     * Issues a book to a member, applying all business validation rules.
     */
    public Transaction issueBook(String isbn, String memberId) throws BookNotAvailableException, InvalidMemberException {
        Book book = books.get(isbn);
        if (book == null) {
            throw new BookNotAvailableException("No book found with ISBN " + isbn);
        }
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("No available copies of \"" + book.getTitle() + "\".");
        }
        Member member = members.get(memberId);
        if (member == null) {
            throw new InvalidMemberException("No member found with ID " + memberId);
        }
        if (!member.canBorrow()) {
            throw new InvalidMemberException(
                    "Member " + member.getName() + " cannot borrow (limit reached or unpaid fine).");
        }

        book.decrementAvailable();
        member.incrementBorrowed();

        transactionCounter++;
        String txId = "T" + String.format("%04d", transactionCounter);
        Transaction transaction = new Transaction(txId, isbn, memberId, LocalDate.now());
        transactions.add(transaction);
        saveData();
        return transaction;
    }

    /**
     * Processes the return of a book, calculating and applying any overdue fine.
     */
    public Transaction returnBook(String transactionId) throws BookNotAvailableException, InvalidMemberException {
        Transaction transaction = null;
        for (Transaction t : transactions) {
            if (t.getTransactionId().equals(transactionId) && !t.isReturned()) {
                transaction = t;
                break;
            }
        }
        if (transaction == null) {
            throw new BookNotAvailableException("No active transaction found with ID " + transactionId);
        }

        Book book = books.get(transaction.getIsbn());
        Member member = members.get(transaction.getMemberId());
        if (book == null || member == null) {
            throw new InvalidMemberException("Associated book or member record is missing.");
        }

        LocalDate today = LocalDate.now();
        transaction.markReturned(today);
        double fine = transaction.calculateFine(today);
        if (fine > 0) {
            member.addFine(fine);
        }

        book.incrementAvailable();
        member.decrementBorrowed();
        saveData();
        return transaction;
    }

    public void payFine(String memberId) throws InvalidMemberException {
        Member member = members.get(memberId);
        if (member == null) {
            throw new InvalidMemberException("No member found with ID " + memberId);
        }
        member.clearFine();
        saveData();
    }

    public List<Transaction> listTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> listActiveTransactionsForMember(String memberId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getMemberId().equals(memberId) && !t.isReturned()) {
                result.add(t);
            }
        }
        return result;
    }

    // ===================== REPORTING =====================

    public List<Transaction> listOverdueTransactions() {
        List<Transaction> overdue = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Transaction t : transactions) {
            if (!t.isReturned() && today.isAfter(t.getDueDate())) {
                overdue.add(t);
            }
        }
        return overdue;
    }

    public int getTotalBookCount() {
        return books.size();
    }

    public int getTotalMemberCount() {
        return members.size();
    }
}
