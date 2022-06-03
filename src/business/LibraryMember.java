package business;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;

final public class LibraryMember extends Person implements Serializable {
	private String memberId;

	private Checkout[] checkouts;
	
	public LibraryMember(String memberId, String fname, String lname, String tel,Address add) {
		super(fname,lname, tel, add);
		this.memberId = memberId;
		this.checkouts = new Checkout[0];
	}
	
	
	public String getMemberId() {
		return memberId;
	}


	public void addCheckout(Checkout checkout) {
		Checkout[] newArr = new Checkout[checkouts.length + 1];
		System.arraycopy(checkouts, 0, newArr, 0, checkouts.length);
		newArr[checkouts.length] = new Checkout(checkout.getEntries());
		checkouts = newArr;
	}

	public Checkout[] getCheckouts() {
		return checkouts;
	}

	public Checkout[] getOverdueCheckouts() {
		Checkout[] overdues = new Checkout[0];
		for(Checkout c: checkouts) {
			if (c.getOverDues().length > 0) {
				Checkout[] newArr = new Checkout[overdues.length + 1];
				System.arraycopy(overdues, 0, newArr, 0, overdues.length);
				overdues = newArr;
			}
		}
		return overdues;
	}

	@Override
	public String toString() {
		return "Member Info: " + "ID: " + memberId + ", name: " + getFirstName() + " " + getLastName() + 
				", " + getTelephone() + " " + getAddress();
	}

	private static final long serialVersionUID = -2226197306790714013L;
}
