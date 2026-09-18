import java.io.*;
import java.time.LocalDate;
import java.util.*;

// ==========================================
// Custom Exceptions
// ==========================================
class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}

class InvalidMemberException extends Exception {
    public InvalidMemberException(String message) {
        super(message);
    }
}

class DuplicateRecordException extends Exception {
    public DuplicateRecordException(String message) {
        super(message);
    }
}

// ==========================================
// Models
// ==========================================
class Person {
    private String id;
    private String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}

class Member extends Person {
    private List<String> borrowedBookIsbns;

    public Member(String id, String name) {
        super(id, name);
        this.borrowedBookIsbns = new ArrayList<>();
    }

    public List<String> getBorrowedBookIsbns() { 
        return borrowedBookIsbns; 
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Borrowed: %d book(s)", getId(), getName(), borrowedBookIsbns.size());
    }
}

class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(String isbn, String title, String author, boolean isAvailable) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }

    @Override
    public String toString() {
        return String.format("ISBN: %s | \"%s\" by %s | %s", 
            isbn, title, author, (isAvailable ? "Available" : "Borrowed"));
    }
}

class Transaction {
    private String memberId;
    private String bookIsbn;
    private String type; // BORROW or RETURN
    private LocalDate date;

    public Transaction(String memberId, String bookIsbn, String type, LocalDate date) {
        this.memberId = memberId;
        this.bookIsbn = bookIsbn;
        this.type = type;
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Member: %s, Book ISBN: %s", date, type, memberId, bookIsbn);
    }
}

// ==========================================
// Service Layer
// ==========================================
class Library {
    private Map<String, Book> books = new LinkedHashMap<>();
    private Map<String, Member> members = new LinkedHashMap<>();
    private List<Transaction> transactions = new ArrayList<>();
    
    private final String DATA_FILE = "library_data.txt";

    public Library() {
        loadData();
    }

    public void addBook(Book book) throws DuplicateRecordException {
        if (books.containsKey(book.getIsbn())) {
            throw new DuplicateRecordException("Book with ISBN " + book.getIsbn() + " already exists.");
        }
        books.put(book.getIsbn(), book);
        saveData();
    }

    public void registerMember(Member member) throws DuplicateRecordException {
        if (members.containsKey(member.getId())) {
            throw new DuplicateRecordException("Member ID " + member.getId() + " is already registered.");
        }
        members.put(member.getId(), member);
        saveData();
    }

    public void borrowBook(String memberId, String isbn) throws Exception {
        Member member = members.get(memberId);
        if (member == null) {
            throw new InvalidMemberException("No member found with ID: " + memberId);
        }

        Book book = books.get(isbn);
        if (book == null) {
            throw new Exception("Book with ISBN " + isbn + " does not exist.");
        }
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("Book \"" + book.getTitle() + "\" is currently checked out.");
        }

        book.setAvailable(false);
        member.getBorrowedBookIsbns().add(isbn);
        transactions.add(new Transaction(memberId, isbn, "BORROW", LocalDate.now()));
        saveData();
    }

    public void returnBook(String memberId, String isbn) throws Exception {
        Member member = members.get(memberId);
        if (member == null) {
            throw new InvalidMemberException("No member found with ID: " + memberId);
        }

        Book book = books.get(isbn);
        if (book == null) {
            throw new Exception("Book with ISBN " + isbn + " does not exist.");
        }
        if (!member.getBorrowedBookIsbns().contains(isbn)) {
            throw new Exception("This member did not borrow this book.");
        }

        book.setAvailable(true);
        member.getBorrowedBookIsbns().remove(isbn);
        transactions.add(new Transaction(memberId, isbn, "RETURN", LocalDate.now()));
        saveData();
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        books.values().forEach(System.out::println);
    }

    public void displayMembers() {
        if (members.isEmpty()) {
            System.out.println("No registered members.");
            return;
        }
        members.values().forEach(System.out::println);
    }

    public void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transaction history.");
            return;
        }
        transactions.forEach(System.out::println);
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Book b : books.values()) {
                writer.println("BOOK," + b.getIsbn() + "," + b.getTitle() + "," + b.getAuthor() + "," + b.isAvailable());
            }
            for (Member m : members.values()) {
                writer.println("MEMBER," + m.getId() + "," + m.getName() + "," + String.join(";", m.getBorrowedBookIsbns()));
            }
        } catch (IOException e) {
            System.err.println("Notice: Could not save data to file (" + e.getMessage() + ")");
        }
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                if (parts[0].equals("BOOK")) {
                    books.put(parts[1], new Book(parts[1], parts[2], parts[3], Boolean.parseBoolean(parts[4])));
                } else if (parts[0].equals("MEMBER")) {
                    Member m = new Member(parts[1], parts[2]);
                    if (parts.length >= 4 && !parts[3].trim().isEmpty()) {
                        for (String isbn : parts[3].split(";")) {
                            m.getBorrowedBookIsbns().add(isbn);
                        }
                    }
                    members.put(m.getId(), m);
                }
            }
        } catch (IOException e) {
            System.err.println("Notice: Failed to load previous library state.");
        }
    }
}

// ==========================================
// Main Application Entry Point
// ==========================================
public class LibraryApp {
    public static void main(String[] args) {
        Library lib = new Library();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Library Menu ---");
            System.out.println("1. Add Book");
            System.out.println("2. Register Member");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Show Books");
            System.out.println("6. Show Members");
            System.out.println("7. View Transactions");
            System.out.println("8. Exit");
            System.out.print("Select an option (1-8): ");

            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter ISBN: ");
                        String isbn = sc.nextLine().trim();
                        System.out.print("Enter Title: ");
                        String title = sc.nextLine().trim();
                        System.out.print("Enter Author: ");
                        String author = sc.nextLine().trim();
                        lib.addBook(new Book(isbn, title, author, true));
                        System.out.println("Book saved.");
                        break;

                    case "2":
                        System.out.print("Enter Member ID: ");
                        String memId = sc.nextLine().trim();
                        System.out.print("Enter Name: ");
                        String name = sc.nextLine().trim();
                        lib.registerMember(new Member(memId, name));
                        System.out.println("Member registered.");
                        break;

                    case "3":
                        System.out.print("Enter Member ID: ");
                        String bMemId = sc.nextLine().trim();
                        System.out.print("Enter Book ISBN: ");
                        String bIsbn = sc.nextLine().trim();
                        lib.borrowBook(bMemId, bIsbn);
                        System.out.println("Checkout complete.");
                        break;

                    case "4":
                        System.out.print("Enter Member ID: ");
                        String rMemId = sc.nextLine().trim();
                        System.out.print("Enter Book ISBN: ");
                        String rIsbn = sc.nextLine().trim();
                        lib.returnBook(rMemId, rIsbn);
                        System.out.println("Return recorded.");
                        break;

                    case "5":
                        System.out.println("\n--- Catalog ---");
                        lib.displayBooks();
                        break;

                    case "6":
                        System.out.println("\n--- Members ---");
                        lib.displayMembers();
                        break;

                    case "7":
                        System.out.println("\n--- Recent Activity ---");
                        lib.displayTransactions();
                        break;

                    case "8":
                        System.out.println("Shutting down.");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
