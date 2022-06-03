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
	private Instant checkoutDate;

	public void addEntry(BookCopy bookCopy) {
		CheckoutEntry[] newArr = new CheckoutEntry[entries.length + 1];
		System.arraycopy(entries, 0, newArr, 0, entries.length);
		newArr[entries.length] = new CheckoutEntry(bookCopy);
		entries = newArr;
	}

	public void removeLastEntry() {
		CheckoutEntry[] newArr = new CheckoutEntry[entries.length - 1];
		System.arraycopy(entries, 0, newArr, 0, entries.length - 1);
		entries = newArr;
	}

	public Checkout(CheckoutEntry[] entries) {
		this.entries = entries;
		this.checkoutDate = Instant.now();
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

	public CheckoutEntry[] getOverDues() {
//		return Arrays.stream(entries).filter(e -> e.getDueDate().isAfter(Instant.now()));
		CheckoutEntry[] overdues = new CheckoutEntry[0];
		for(CheckoutEntry e: entries) {
			if (e.getDueDate().isBefore(Instant.now())) {
				CheckoutEntry[] newArr = new CheckoutEntry[overdues.length + 1];
				System.arraycopy(overdues, 0, newArr, 0, overdues.length);
				overdues = newArr;
			}
		}
		return overdues;
	}
}
