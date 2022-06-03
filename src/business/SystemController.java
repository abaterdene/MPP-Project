package business;

import java.lang.reflect.Member;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
	public  List<String> allAuthorIds() {
		DataAccess da = new DataAccessFacade();
		List<String> retval = new ArrayList<>();
		retval.addAll(da.readAuthorMap().keySet());
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
		throw new LibrarySystemException("Book is not available");
	}

	@Override
	public Book getRentableBookByIsbn(String isbn) throws LibrarySystemException {
		Book book = this.getBookByIsbn(isbn);
		if (Objects.nonNull(book) && book.getCopies().length > 0)
			return book;
		throw new LibrarySystemException("Book is not available");
	}

	@Override
	public Author getAuthorById(String authorId) throws LibrarySystemException {
		DataAccess da = new DataAccessFacade();
		Author author = da.readAuthorMap().get(authorId);
		if (author != null)
			return author;
		throw new LibrarySystemException("Author is not available");
	}

	@Override
	public void addBookCopy(Book book, int numberOfCopies){
		DataAccess da = new DataAccessFacade();
		if (!currentUser.getAuthorization().equals(Auth.LIBRARIAN)) {
			for (int i = 1; i < numberOfCopies; i++) {
				book.addCopy();
			}
			da.saveNewBook(book);
		}
	}

	@Override
	public void checkoutBook(LibraryMember member) {
		DataAccess da = new DataAccessFacade();
		da.saveNewMember(member);
	}

	@Override
	public List<LibraryMember> allOverdueMembers() {
		DataAccess da = new DataAccessFacade();
		Collection<LibraryMember> members = da.readMemberMap().values();
		List<LibraryMember> mems = new ArrayList<>(members);
		return mems.stream().filter(m -> m.getOverdueCheckouts().length > 0).collect(Collectors.toList());
	}
}
