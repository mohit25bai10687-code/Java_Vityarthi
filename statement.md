# Project Statement

## Problem Statement

Small libraries and reading rooms often rely on manual registers or spreadsheets to track book inventory, member borrowing, and due dates. This makes it difficult to know which books are available, who currently holds which book, and who owes overdue fines. The Library Management System addresses this by providing a single, structured console application to manage the full borrow/return lifecycle.

## Scope

The project covers:

- Maintaining a catalogue of books (add, update, delete, search, list)
- Maintaining a register of members (register, update, delete, list)
- Issuing and returning books, with automatic due-date tracking and overdue fine calculation
- Persisting all data locally between sessions

The project does **not** cover: multi-branch libraries, user authentication/roles, online reservations, or a web/GUI front end (out of scope by design, per the CLI-only submission requirement).

## Target Users

- **Librarian / Administrator** — the primary user, operating the system to manage the catalogue, members, and transactions from the terminal.
- **Library Members** — represented as records in the system (registered by the librarian); they do not interact with the terminal directly in this version.

## High-Level Features

- Three functional modules: Book Management, Member Management, Transaction Management
- Borrowing rules: 3-book limit per member, 14-day loan period, ₹5/day overdue fine
- CSV-based persistence (no external database required)
- Input validation and custom exception handling for duplicate records, unavailable books, and invalid members
- Self-contained test suite validating core business rules
