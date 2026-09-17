package com.library.test;

import com.library.exceptions.BookNotAvailableException;
import com.library.exceptions.DuplicateRecordException;
import com.library.exceptions.InvalidMemberException;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.service.Library;

import java.util.List;

/**
 * Lightweight self-contained validation tests for the Library service.
 * Run directly (no external testing framework dependency required),
 * printing PASS/FAIL for each scenario. Uses a separate "test-data"
 * directory so it never touches the application's real data files.
 */
public class LibraryTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        Library library = new Library("test-data");

        testAddAndListBook(library);
        testDuplicateBookRejected(library);
        testIssueAndReturnBook(library);
        testCannotIssueUnavailableBook(library);
        testInvalidMemberRejected(library);
        testOverdueFineCalculation();

        System.out.println("\n=== Test Summary: " + passed + " passed, " + failed + " failed ===");
    }

    private static void testAddAndListBook(Library library) {
        try {
            library.addBook("ISBN001", "Effective Java", "Joshua Bloch", "Programming", 2);
            List<Book> books = library.listBooks();
            assertTrue("Book should appear in catalogue", !books.isEmpty());
            report("testAddAndListBook", true);
        } catch (Exception e) {
            report("testAddAndListBook", false, e.getMessage());
        }
    }

    private static void testDuplicateBookRejected(Library library) {
        try {
            library.addBook("ISBN001", "Duplicate Title", "Someone", "Genre", 1);
            report("testDuplicateBookRejected", false, "Expected DuplicateRecordException was not thrown");
        } catch (DuplicateRecordException e) {
            report("testDuplicateBookRejected", true);
        } catch (Exception e) {
            report("testDuplicateBookRejected", false, "Wrong exception type: " + e);
        }
    }

    private static void testIssueAndReturnBook(Library library) {
        try {
            library.registerMember("M001", "Asha Verma", "asha@example.com");
            Transaction t = library.issueBook("ISBN001", "M001");
            assertTrue("Transaction ID should not be null", t.getTransactionId() != null);
            library.returnBook(t.getTransactionId());
            report("testIssueAndReturnBook", true);
        } catch (Exception e) {
            report("testIssueAndReturnBook", false, e.getMessage());
        }
    }

    private static void testCannotIssueUnavailableBook(Library library) {
        try {
            library.addBook("ISBN002", "Rare Book", "Author X", "Genre", 1);
            library.registerMember("M002", "Ravi Kumar", "ravi@example.com");
            library.registerMember("M003", "Sana Ali", "sana@example.com");
            library.issueBook("ISBN002", "M002"); // takes the only copy
            library.issueBook("ISBN002", "M003"); // should fail
            report("testCannotIssueUnavailableBook", false, "Expected BookNotAvailableException was not thrown");
        } catch (BookNotAvailableException e) {
            report("testCannotIssueUnavailableBook", true);
        } catch (Exception e) {
            report("testCannotIssueUnavailableBook", false, "Wrong exception type: " + e);
        }
    }

    private static void testInvalidMemberRejected(Library library) {
        try {
            library.issueBook("ISBN001", "NON_EXISTENT_MEMBER");
            report("testInvalidMemberRejected", false, "Expected InvalidMemberException was not thrown");
        } catch (InvalidMemberException e) {
            report("testInvalidMemberRejected", true);
        } catch (Exception e) {
            report("testInvalidMemberRejected", false, "Wrong exception type: " + e);
        }
    }

    private static void testOverdueFineCalculation() {
        Transaction t = new Transaction("T999", "ISBNX", "MX", java.time.LocalDate.now().minusDays(20));
        double fine = t.calculateFine(java.time.LocalDate.now());
        boolean correct = fine == 6 * Transaction.FINE_PER_DAY; // 20 - 14 = 6 days overdue
        report("testOverdueFineCalculation", correct,
                correct ? null : "Expected fine of " + (6 * Transaction.FINE_PER_DAY) + " but got " + fine);
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void report(String testName, boolean success) {
        report(testName, success, null);
    }

    private static void report(String testName, boolean success, String detail) {
        if (success) {
            passed++;
            System.out.println("[PASS] " + testName);
        } else {
            failed++;
            System.out.println("[FAIL] " + testName + (detail != null ? " - " + detail : ""));
        }
    }
}
