package business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dataaccess.Auth;
import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;
import dataaccess.User;

public class SystemController implements ControllerInterface {
	public static Auth currentAuth = null;
	public static User currentUser = null;

	public SystemController() {
		DataAccess da = new DataAccessFacade();
		currentUser = da.getCurrentUser();
	}
	
	public void login(String id, String password) throws LoginException {
		DataAccess da = new DataAccessFacade();
		HashMap<String, User> map = da.readUserMap();
		if(!map.containsKey(id)) {
			throw new LoginException("ID " + id + " not found");
		}
		String passwordFound = map.get(id).getPassword();
		if(!passwordFound.equals(password)) {
			throw new LoginException("Password incorrect");
		}
		da.setCurrentUser(map.get(id));
		currentAuth = map.get(id).getAuthorization();
	}

	public void logout() {
		DataAccess da = new DataAccessFacade();
		da.setCurrentUser(null);
	}
	@Override
	public List<String> allMemberIds() {
		DataAccess da = new DataAccessFacade();
		List<String> retval = new ArrayList<>();
		retval.addAll(da.readMemberMap().keySet());
		return retval;
	}
	
	@Override
	public List<String> allBookIds() {
		DataAccess da = new DataAccessFacade();
		List<String> retval = new ArrayList<>();
		retval.addAll(da.readBooksMap().keySet());
		return retval;
	}

	@Override
	public void addMember(LibraryMember member) {
		DataAccess da = new DataAccessFacade();
		if (!currentUser.getAuthorization().equals(Auth.LIBRARIAN))
			da.saveNewMember(member);
	}

	@Override
	public void addBook(Book book) {
		DataAccess da = new DataAccessFacade();
		if (!currentUser.getAuthorization().equals(Auth.LIBRARIAN))
			da.saveNewBook(book);
	}

	@Override
	public LibraryMember getMemberById(String memberId) throws LibrarySystemException {
		DataAccess da = new DataAccessFacade();
		LibraryMember member = da.readMemberMap().get(memberId);
		if (member != null)
			return member;
		throw new LibrarySystemException("Member doesn't exist");
	}

	@Override
	public Book getBookByIsbn(String isbn) throws LibrarySystemException {
		DataAccess da = new DataAccessFacade();
		Book book = da.readBooksMap().get(isbn);
		if (book != null)
			return book;
		throw new LibrarySystemException("Book doesn't exist");
	}
}
