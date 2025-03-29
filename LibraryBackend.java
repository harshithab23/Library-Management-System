import java.util.ArrayList;
import java.util.List;

// Abstract base class representing a general book (Abstraction)
abstract class Book {
    protected String title;        // Book title
    protected String author;       // Book author
    protected double price;        // Price of the book
    protected double rentCost;     // Renting cost of the book
    protected boolean isRented;    // Status of the book (rented or not)

    // Constructor for Book class
    public Book(String title, String author, double price, double rentCost) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.rentCost = rentCost;
        this.isRented = false;      // Initially, the book is not rented
    }

    // Abstract method to rent a book (Polymorphism)
    public abstract boolean rent();

    // Getter methods (Encapsulation)
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public double getRentCost() {
        return rentCost;
    }

    public boolean isRented() {
        return isRented;
    }
}

// RentableBook subclass that extends Book (Inheritance)
class RentableBook extends Book {

    // Constructor for RentableBook
    public RentableBook(String title, String author, double price, double rentCost) {
        super(title, author, price, rentCost);
    }

    // Overriding the rent method
    @Override
    public boolean rent() {
        if (!isRented) {
            isRented = true; // Mark the book as rented
            return true;     // Successful rent
        }
        return false;        // Book is already rented
    }

    // Method to return the rented book
    public void returnBook() {
        isRented = false; // Mark the book as not rented
    }
}

// LibraryBackend class that manages book operations
public class LibraryBackend {
    private List<Book> books = new ArrayList<>(); // List to store books
    private int bookCount = 0; // Counter for the number of books

    // Method to add a new book
    public void addBook(String title, String author, double price, double rentCost) {
        books.add(new RentableBook(title, author, price, rentCost)); // Create a new RentableBook
        bookCount++; // Increase the book count
    }

    // Method to delete a book if it's not rented
    public void deleteBook(String title) {
        books.removeIf(book -> book.getTitle().equals(title) && !book.isRented()); // Remove book
        bookCount = Math.max(bookCount - 1, 0); // Decrease count if book is deleted
    }

    // Method to rent a book
    public boolean rentBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                if (book.rent()) {
                    bookCount--; // Decrease count when a book is rented
                    return true; // Successful rent
                }
            }
        }
        return false; // Book not found or already rented
    }

    // Method to return a rented book
    public boolean returnBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title) && book.isRented()) {
                ((RentableBook) book).returnBook(); // Call returnBook on RentableBook
                bookCount++; // Increase count when a book is returned
                return true; // Successful return
            }
        }
        return false; // Book not found or not rented
    }

    // Method to get the inventory of books
    public List<Book> getInventory() {
        return books; // Return the list of books
    }

    // Getter for the book count
    public int getBookCount() {
        return bookCount; // Return the current book count
    }
}
