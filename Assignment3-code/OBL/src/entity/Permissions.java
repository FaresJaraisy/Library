package entity;

import java.io.Serializable;
/**
 * This entity class stores all Permission objects for a user
 */
public class Permissions implements Serializable{
	/**
	 * instance variables:
	 * userID - the ID of the user.
	 * canBorrow, canReserve - two flags that indicate whether the user can borrow or can reserve respectively.
	 */
	private String userID;
	private boolean canBorrow;
	private boolean canReserve;
	/**
	 * a single argument constructor
	 * @param userID
	 */
	public Permissions(String userID) {
		this.userID = userID;
	}
	/**
	 * a full argument constructor
	 * @param userID
	 * @param canBorrow
	 * @param canReserve
	 */
	public Permissions(String userID, boolean canBorrow, boolean canReserve) {
		
		this.userID = userID;
		this.setCanBorrow(canBorrow);
		this.setCanReserve(canReserve);
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
	public boolean isCanBorrow() {
		return canBorrow;
	}
	/**
	 * 
	 * @param canBorrow
	 */
	public void setCanBorrow(boolean canBorrow) {
		this.canBorrow = canBorrow;
	}
	/**
	 * 
	 * @return
	 */
	public boolean isCanReserve() {
		return canReserve;
	}
	/**
	 * 
	 * @param canReserve
	 */
	public void setCanReserve(boolean canReserve) {
		this.canReserve = canReserve;
	}

}
