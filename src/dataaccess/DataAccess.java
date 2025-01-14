package dataaccess;

import java.util.HashMap;

import business.Author;
import business.Book;
import business.Checkout;
import business.LibraryMember;
import dataaccess.DataAccessFacade.StorageType;

public interface DataAccess { 
	public HashMap<String,Book> readBooksMap();
	public HashMap<String, Author> readAuthorMap();
	public HashMap<String,User> readUserMap();
	public HashMap<String, LibraryMember> readMemberMap();

	public void saveNewMember(LibraryMember member);
	public void saveNewBook(Book book);
	public User getCurrentUser();
	public void setCurrentUser(User user);
}
