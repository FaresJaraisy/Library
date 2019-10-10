package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
/**
 * The entity class that stores manual delays objects.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class ManualDelays implements Serializable{
	private String libraraianID;
	private Date date;
	private String userID;
	private String barcode;
	private Timestamp borrowDate;
	/**
	 * a full-argument constructor
	 * @param libraraianID
	 * @param date
	 * @param userID
	 * @param barcode
	 * @param borrowDate
	 */
	public ManualDelays(String libraraianID,Date date,String userID,String barcode,Timestamp borrowDate) {
		this.libraraianID=libraraianID;
		this.date=date;
		this.userID=userID;
		this.barcode=barcode;
		this.borrowDate=borrowDate;
	}
	/**
	 * 
	 * @return
	 */
	public String getLibraraianID() {
		return libraraianID;
	}
	/**
	 * 
	 * @param libraraianID
	 */
	public void setLibraraianID(String libraraianID) {
		this.libraraianID = libraraianID;
	}
	/**
	 * 
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * 
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
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
	public Timestamp getBorrowDate() {
		return borrowDate;
	}
	/**
	 * 
	 * @param borrowDate
	 */
	public void setBorrowDate(Timestamp borrowDate) {
		this.borrowDate = borrowDate;
	}
}
