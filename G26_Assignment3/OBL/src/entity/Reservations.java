package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * an entity that stores reservation objects {@code implements}
 * {@link java.io.Serializable} since it need to be passed to the server.
 */
public class Reservations implements Serializable {
	/**
	 * instance variables: mysqlDate - the reservation date in java.sql.date format
	 */
	private String userID;
	private String barcode;
	private Date reserveDate;
	private java.sql.Date mysqlDate;
	private enums.ReserveStatus status;
	private Timestamp tS;

	/**
	 * a zero argument constructor
	 */
	public Reservations() {
	}

	/**
	 * 
	 * @return
	 */
	public java.sql.Date getMysqlDate() {
		return mysqlDate;
	}

	/**
	 * 
	 * @param mysqlDate
	 */
	public void setMysqlDate(java.sql.Date mysqlDate) {
		this.mysqlDate = mysqlDate;
	}

	/**
	 * a single argument constructor
	 * 
	 * @param barcode
	 */
	public Reservations(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * a 2 argument constructor
	 * 
	 * @param userID
	 * @param barcode
	 */
	public Reservations(String userID, String barcode) {
		this.userID = userID;
		this.barcode = barcode;
	}

	/**
	 * a 4 argument constructor
	 * @param userID
	 * @param barcode
	 * @param reserveDate
	 * @param status
	 */
	public Reservations(String userID, String barcode, Date reserveDate, enums.ReserveStatus status) {

		this.userID = userID;
		this.barcode = barcode;
		this.reserveDate = reserveDate;
		this.status = status;

	}
	/**
	 * another 4 argument constructor
	 * @param userID
	 * @param barcode
	 * @param tS
	 * @param status
	 */
	public Reservations(String userID, String barcode, Timestamp tS, enums.ReserveStatus status) {

		this.userID = userID;
		this.barcode = barcode;
		this.tS = tS;
		this.status = status;

	}
	/**
	 * another 4 argument constructor
	 * @param userID
	 * @param barcode
	 * @param mysqlDate
	 * @param status
	 */
	public Reservations(String userID, String barcode, java.sql.Date mysqlDate, enums.ReserveStatus status) {

		this.userID = userID;
		this.barcode = barcode;
		this.barcode=barcode;
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
	public Date getReserveDate() {
		return reserveDate;
	}
	/**
	 * 
	 * @param reserveDate
	 */
	public void setReserveDate(Date reserveDate) {
		this.reserveDate = reserveDate;
	}
	/**
	 * 
	 * @return
	 */
	public enums.ReserveStatus getStatus() {
		return status;
	}
	/**
	 * 
	 * @param status
	 */
	public void setStatus(enums.ReserveStatus status) {
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


}

