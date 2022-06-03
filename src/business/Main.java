package business;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;

public class Main {

	public static void main(String[] args) {
		System.out.println(allWhoseZipContains3());
		System.out.println(allHavingOverdueBook());
		System.out.println(allHavingMultipleAuthors());

	}
	//Returns a list of all ids of LibraryMembers whose zipcode contains the digit 3
	public static List<String> allWhoseZipContains3() {
		DataAccess da = new DataAccessFacade();
		Collection<LibraryMember> members = da.readMemberMap().values();
		List<LibraryMember> mems = new ArrayList<>(members);
		//implement
		return mems.stream()
				.filter(m -> m.getAddress().getZip().contains("3"))
				.map(LibraryMember::getMemberId)
				.collect(Collectors.toList());
		
	}
	//Returns a list of all ids of  LibraryMembers that have an overdue book
	public static List<String> allHavingOverdueBook() {
		DataAccess da = new DataAccessFacade();
		Collection<LibraryMember> members = da.readMemberMap().values();
		List<LibraryMember> mems = new ArrayList<>(members);
		HashSet<String> overdueMembers = new HashSet<>();
		for(LibraryMember m : mems) {
			Checkout[] checkouts = m.getCheckouts();
			for(Checkout c: checkouts) {
				CheckoutEntry[] entries = c.getEntries();
				for(CheckoutEntry e: entries) {
					if (Instant.now().isAfter(e.getDueDate())) {
						overdueMembers.add(m.getMemberId());
					}
				}
			}
		}
		return overdueMembers.stream().toList();
	}
	
	//Returns a list of all isbns of  Books that have multiple authors
	public static List<String> allHavingMultipleAuthors() {
		DataAccess da = new DataAccessFacade();
		Collection<Book> books = da.readBooksMap().values();
		List<Book> bs = new ArrayList<>(books);
		//implement
		return bs.stream()
				.filter(b -> b.getAuthors().size() > 1)
				.map(Book::getIsbn)
				.collect(Collectors.toList());

	}

}
