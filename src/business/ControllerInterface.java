package business;

import java.util.List;

import business.Book;
import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;

public interface ControllerInterface {
	public void login(String id, String password) throws LoginException;
	public List<String> allMemberIds();
	public List<String> allBookIds();
	public void addMember(LibraryMember member);
	public void addBook(Book book);
	public void addBookCopy(Book book, int numberOfCopies);

	public LibraryMember getMemberById(String member) throws LibrarySystemException;
	public Book getBookByIsbn(String isbn) throws LibrarySystemException;

	public Book getRentableBookByIsbn(String isbn) throws LibrarySystemException;

	public void checkoutBook(Checkout checkout);
}
