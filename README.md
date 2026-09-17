# 
# Library Management System

A console-based Library Management System built in core Java, developed as a VITyarthi "Build Your Own Project" submission.

## Overview

The system allows a librarian to manage a book catalogue, register and manage members, and track book issue/return transactions, including automatic overdue fine calculation. Data is persisted to CSV files so records survive between runs.

## Features

- **Book Management** — add, update, delete, list, and search books (by title/author/genre/ISBN)
- **Member Management** — register, update, delete, and list members; borrowing limit enforcement
- **Transaction Management** — issue books, return books, automatic 14-day loan period, overdue fine calculation (₹5/day), fine payment
- **Reporting** — list all transactions, list overdue books
- **Persistence** — CSV-based storage in the `data/` directory (auto-created on first run)
- **Validation & Error Handling** — custom checked exceptions for duplicate records, unavailable books, and invalid members

## Technologies Used

- Java (JDK 17+)
- Core Java only — Collections Framework, java.time, java.io (no external dependencies)

## Project Structure\
src/com/library/
├── model/ Book, Person (abstract), Member, Transaction
├── exceptions/ BookNotAvailableException, InvalidMemberException, DuplicateRecordException
├── service/ Library (core business logic / facade)
├── util/ FileHandler (CSV persistence)
├── main/ Main (console UI / entry point)
└── test/ LibraryTest (self-contained validation tests)

## Prerequisites

- A computer running Windows, macOS, or Linux
- Java Development Kit (JDK) version 17 or later — no other software, database, or dependency is required

### Installing the JDK (if not already installed)

- **Windows/macOS**: download and install the JDK from [Adoptium Temurin](https://adoptium.net/) (choose version 17 or later, "JDK" not "JRE")
- **Linux (Debian/Ubuntu)**: `sudo apt-get update && sudo apt-get install -y openjdk-17-jdk`

Verify the installation by opening a terminal (Command Prompt / PowerShell on Windows, Terminal on macOS/Linux) and running:
java -version
javac -version
Both commands should print a version number of 17 or higher.

## Steps to Install & Run

1. **Download the project**: clone this repository, or download it as a ZIP and extract it.
2. **Compile the source code** from the project root directory:
   - On Linux/macOS:
   -  find src -name "*.java" > sources.txt
 javac -d bin @sources.txt
   - On Windows (PowerShell):
   -    - On Windows (PowerShell):
           Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } > sources.txt
 javac -d bin "@sources.txt"
   This creates a `bin/` folder containing the compiled `.class` files. No build tool (Maven/Gradle) is required.
3. **Run the application** from the project root:
4. java -cp bin com.library.main.Main
5. 4. Use the on-screen numbered menu to manage books, members, and transactions. The application creates a `data/` folder automatically on first run and saves all changes there as CSV files, so your records persist the next time you launch it.
5. **Exit** the application by entering `0` at the main menu.

## Instructions for Testing

Run the included self-contained test suite (uses a separate `test-data/` directory, so it never touches your real records):
java -cp bin com.library.test.LibraryTest

Expected output: 6 tests, all `PASS`, covering book addition, duplicate rejection, issue/return flow, unavailable-book handling, invalid-member handling, and overdue fine calculation.

## Notes

- Loan period: 14 days from issue date
- Fine: ₹5 per day overdue
- A member cannot borrow more than 3 books at once, or while an outstanding fine is unpaid
- 
