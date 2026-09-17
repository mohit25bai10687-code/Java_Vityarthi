package com.library.model;

import java.io.Serializable;

/**
 * Abstract base class representing a person associated with the library.
 * Demonstrates abstraction and inheritance; Member and Librarian extend this.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected String email;

    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Each subtype defines its own role description.
     */
    public abstract String getRole();
}
