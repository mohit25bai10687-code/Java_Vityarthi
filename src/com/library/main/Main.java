package com.library.main;

import com.library.exceptions.BookNotAvailableException;
import com.library.exceptions.DuplicateRecordException;
import com.library.exceptions.InvalidMemberException;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.service.Library;

import java.util.List;
import java.util.Scanner;

/**
 * Console entry point providing a menu-driven interface to the
 * Library Management System. Delegates all business logic to the
 * Library service class.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Library library = new Library("data");

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      WELCOME TO THE LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": addBook(); break;
                    case "2": updateBook(); break;
                    case "3": deleteBook(); break;
                    case "4": listBooks(); break;
                    case "5": searchBooks(); break;
                    case "6": registerMember(); break;
                    case "7": updateMember(); break;
                    case "8": deleteMember(); break;
                    case "9": listMembers(); break;
                    case "10": issueBook(); break;
                    case "11": returnBook(); break;
                    case "12": payFine(); break;
                    case "13": listTransactions(); break;
                    case "14": listOverdue(); break;
                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (DuplicateRecordException | BookNotAvailableException | InvalidMemberException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println(" Book Management:");
        System.out.println("  1. Add Book        2. Update Book     3. Delete Book");
        System.out.println("  4. List Books      5. Search Books");
        System.out.println(" Member Management:");
        System.out.println("  6. Register Member 7. Update Member   8. Delete Member");
        System.out.println("  9. List Members");
        System.out.println(" Transaction Management:");
        System.out.println("  10. Issue Book     11. Return Book    12. Pay Fine");
        System.out.println("  13. List Transactions  14. List Overdue Books");
        System.out.println("  0. Exit");
        System.out.print("Enter choice: ");
    }

    // ---------- Book Management ----------

    private static void addBook() throws DuplicateRecordException {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Genre: ");
        String genre = scanner.nextLine().trim();
        System.out.print("Number of copies: ");
        int copies = Integer.parseInt(scanner.nextLine().trim());
        library.addBook(isbn, title, author, genre, copies);
        System.out.println("Book added successfully.");
    }

    private static void updateBook() throws BookNotAvailableException {
        System.out.print("ISBN of book to update: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("New title (blank to keep unchanged): ");
        String title = scanner.nextLine().trim();
        System.out.print("New author (blank to keep unchanged): ");
        String author = scanner.nextLine().trim();
        System.out.print("New genre (blank to keep unchanged): ");
        String genre = scanner.nextLine().trim();
        library.updateBook(isbn, title, author, genre);
        System.out.println("Book updated successfully.");
    }

    private static void deleteBook() throws BookNotAvailableException {
        System.out.print("ISBN of book to delete: ");
        String isbn = scanner.nextLine().trim();
        library.deleteBook(isbn);
        System.out.println("Book deleted successfully.");
    }

    private static void listBooks() {
        List<Book> books = library.listBooks();
        if (books.isEmpty()) {
            System.out.println("No books in catalogue.");
            return;
        }
        System.out.println("ISBN         | Title                          | Author               | Genre           | Copies");
        for (Book b : books) {
            System.out.println(b);
        }
    }

    private static void searchBooks() {
        System.out.print("Enter keyword (title/author/genre/ISBN): ");
        String keyword = scanner.nextLine().trim();
        List<Book> results = library.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        for (Book b : results) {
            System.out.println(b);
        }
    }

    // ---------- Member Management ----------

    private static void registerMember() throws DuplicateRecordException {
        System.out.print("Member ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        library.registerMember(id, name, email);
        System.out.println("Member registered successfully.");
    }

    private static void updateMember() throws InvalidMemberException {
        System.out.print("Member ID to update: ");
        String id = scanner.nextLine().trim();
        System.out.print("New name (blank to keep unchanged): ");
        String name = scanner.nextLine().trim();
        System.out.print("New email (blank to keep unchanged): ");
        String email = scanner.nextLine().trim();
        library.updateMember(id, name, email);
        System.out.println("Member updated successfully.");
    }

    private static void deleteMember() throws InvalidMemberException {
        System.out.print("Member ID to delete: ");
        String id = scanner.nextLine().trim();
        library.deleteMember(id);
        System.out.println("Member deleted successfully.");
    }

    private static void listMembers() {
        List<Member> members = library.listMembers();
        if (members.isEmpty()) {
            System.out.println("No registered members.");
            return;
        }
        for (Member m : members) {
            System.out.println(m);
        }
    }

    // ---------- Transaction Management ----------

    private static void issueBook() throws BookNotAvailableException, InvalidMemberException {
        System.out.print("ISBN of book to issue: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        Transaction t = library.issueBook(isbn, memberId);
        System.out.println("Book issued. Transaction ID: " + t.getTransactionId()
                + " | Due date: " + t.getDueDate());
    }

    private static void returnBook() throws BookNotAvailableException, InvalidMemberException {
        System.out.print("Transaction ID: ");
        String txId = scanner.nextLine().trim();
        Transaction t = library.returnBook(txId);
        double fine = t.calculateFine(t.getReturnDate());
        System.out.println("Book returned successfully.");
        if (fine > 0) {
            System.out.printf("Overdue fine applied: %.2f%n", fine);
        }
    }

    private static void payFine() throws InvalidMemberException {
        System.out.print("Member ID: ");
        String id = scanner.nextLine().trim();
        library.payFine(id);
        System.out.println("Fine cleared successfully.");
    }

    private static void listTransactions() {
        List<Transaction> transactions = library.listTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
            return;
        }
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    private static void listOverdue() {
        List<Transaction> overdue = library.listOverdueTransactions();
        if (overdue.isEmpty()) {
            System.out.println("No overdue books.");
            return;
        }
        for (Transaction t : overdue) {
            System.out.println(t);
        }
    }
}
