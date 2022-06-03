package business;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 *
 */
final public class CheckoutEntry implements Serializable {

    private static final long serialVersionUID = 6120690276685962829L;
    private BookCopy bookCopy;
    private Instant dueDate;

    public CheckoutEntry(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
        setDueDate();
    }

    public CheckoutEntry(BookCopy bookCopy, Instant dueDate) {
        this.bookCopy = bookCopy;
        this.dueDate = dueDate;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate() {
        this.dueDate = Instant.now().plus(this.bookCopy.getBook().getMaxCheckoutLength(), ChronoUnit.DAYS);
    }
}
