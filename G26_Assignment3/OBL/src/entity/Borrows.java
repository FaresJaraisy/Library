package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import enums.BorrowStatus;

/**
 * The entity class that stores borrows. {@code implements}
 * {@link java.io.Serializable} since it need to be passed to the server.
 */
public class Borrows implements Serializable {
	/**
	 * tS - to store dates and times, not necessarily the current date and time.
	 */
	protected String userID;
	protected String barcode;
	protected String librarianID;
	protected Date borrowDate;
	protected Date returnDate;
	protected Date actualReturnDate;
	protected enums.BorrowStatus status;
	protected Timestamp tS;

	/**
	 * an empty constructor
	 */
	public Borrows() {

	}

	/**
	 * a 5-agrument constructor.
	 * @param userID
	 * @param barcode
	 * @param librarianID
	 * @param returnDate
	 * @param status
	 */
	public Borrows(String userID, String barcode, String librarianID, Date returnDate, enums.BorrowStatus status) {
		this.userID = userID;
		this.barcode = barcode;
		this.librarianID = librarianID;
		this.returnDate = returnDate;
		this.status = status;
	}
	/**
	 * 
	 * @return
	 */
	public Timestamp gettS() {
		return tS;
	}
	/**
	 * 
	 * @param tS
	 */
	public void settS(Timestamp tS) {
		this.tS = tS;
	}
	/**
	 * another 5-argument constructor.
	 * @param userID
	 * @param barcode
	 * @param librarianID
	 * @param tS
	 * @param status
	 */
	public Borrows(String userID, String barcode, String librarianID, Timestamp tS, enums.BorrowStatus status) {
		this.userID = userID;
		this.barcode = barcode;
		this.librarianID = librarianID;
		this.tS = tS;
		this.status = status;
	}
	/**
	 * a 3-argument constructor.
	 * @param userID
	 * @param barcode
	 * @param status
	 */
	public Borrows(String userID, String barcode, enums.BorrowStatus status) {
		this.userID = userID;
		this.barcode = barcode;
		this.status = status;
	}
	/**
	 * a full-argument constructor
	 * @param userID
	 * @param barcode
	 * @param librarianID
	 * @param borrowDate
	 * @param returnDate
	 * @param actualReturnDate
	 * @param status
	 */
	public Borrows(String userID, String barcode, String librarianID, Date borrowDate, Date returnDate,
			Date actualReturnDate, enums.BorrowStatus status) {

		this.userID = userID;
		this.barcode = barcode;
		this.librarianID = librarianID;
		this.borrowDate = borrowDate;
		this.returnDate = returnDate;
		this.actualReturnDate = actualReturnDate;
		this.status = status;

	}
	/**
	 * 
	 * @return
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * 
	 * @param userID
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}
	/**
	 * 
	 * @return
	 */
	public String getBarcode() {
		return barcode;
	}
	/**
	 * 
	 * @param barcode
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	/**
	 * 
	 * @return
	 */
	public String getLibrarianID() {
		return librarianID;
	}
	/**
	 * 
	 * @param librarianID
	 */
	public void setLibrarianID(String librarianID) {
		this.librarianID = librarianID;
	}
	/**
	 * 
	 * @return
	 */
	public Date getBorrowDate() {
		return borrowDate;
	}
	/**
	 * 
	 * @param borrowDate
	 */
	public void setBorrowDate(Date borrowDate) {
		this.borrowDate = borrowDate;
	}
	/**
	 * 
	 * @return
	 */
	public Date getReturnDate() {
		return returnDate;
	}
	/**
	 * 
	 * @param returnDate
	 */
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}
	/**
	 * 
	 * @return
	 */
	public Date getActualReturnDate() {
		return actualReturnDate;
	}
	/**
	 * 
	 * @param actualReturnDate
	 */
	public void setActualReturnDate(Date actualReturnDate) {
		this.actualReturnDate = actualReturnDate;
	}
	/**
	 * 
	 * @return
	 */
	public enums.BorrowStatus getStatus() {
		return status;
	}
	/**
	 * 
	 * @param status
	 */
	public void setStatus(enums.BorrowStatus status) {
		this.status = status;
	}
}
