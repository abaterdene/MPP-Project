package business;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

/**
 *
 */
final public class Checkout implements Serializable {

	private static final long serialVersionUID = 6130690276685962829L;
	private CheckoutEntry[] entries;
	private final Instant checkoutDate = Instant.now();

	public void addEntry(BookCopy bookCopy) {
		CheckoutEntry[] newArr = new CheckoutEntry[entries.length + 1];
		System.arraycopy(entries, 0, newArr, 0, entries.length);
		newArr[entries.length] = new CheckoutEntry(bookCopy);
		entries = newArr;
	}

	public Checkout(CheckoutEntry[] entries) {
		this.entries = entries;
	}

	public CheckoutEntry[] getEntries() {
		return entries;
	}

	public void setEntries(CheckoutEntry[] entries) {
		this.entries = entries;
	}

	public Instant getCheckoutDate() {
		return checkoutDate;
	}
}
