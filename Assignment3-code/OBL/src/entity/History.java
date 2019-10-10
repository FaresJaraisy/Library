package entity;

import java.io.Serializable;
import java.util.Date;
/**
 * The entity class that stores history objects.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class History implements Serializable {
	/**
	 * instance variables:
	 */
	private String userID;
	private String barcode;
	private enums.EventType type;
	private Date eventDate;
	/**
	 * empty contructor
	 */
	public  History() {
		
	}
	/**
	 * a full-argument constructor
	 * @param userID
	 * @param barcode
	 * @param type
	 * @param eventDate
	 */
   public  History(String userID,String barcode,enums.EventType type,Date eventDate) {
	   this.userID=userID;
	   this.barcode=barcode;
	   this.type=type;
	   this.eventDate=eventDate;
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
	public enums.EventType getType() {
		return type;
	}
	/**
	 * 
	 * @param type
	 */
	public void setType(enums.EventType type) {
		this.type = type;
	}
	/**
	 * 
	 * @return
	 */
	public Date getEventDate() {
		return eventDate;
	}
	/**
	 * 
	 * @param eventDate
	 */
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	
}
