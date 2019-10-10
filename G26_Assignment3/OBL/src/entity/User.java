package entity;

import java.io.Serializable;

public class User implements Serializable {
	private String userID;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String membershipNumber;
	private String password;
	private int strikes;
	private enums.UserStatus status;
	private String email;
	
	public User(String userID, String fName, String lName, String phoneNumber, String password, String email,enums.UserStatus status) {
		/*
		 * throw exceptions do not forgot!!!!!!!!!
		 * */
		this.userID = userID;
		this.firstName = fName;
		this.lastName = lName;
		this.phoneNumber=phoneNumber;
		this.password=password;
		this.email=email;
		this.strikes=0;
		this.status=status;
		this.membershipNumber = (Integer.toString((Integer.parseInt(this.userID) % 10000)));
	}
	
	public User() {}
	
	public User(String username) {
		this.userID = username;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setFirstName(String fName) {
		this.firstName = fName;
	}

	public void setLastName(String lName) {
		this.lastName = lName;
	}

	public void setMembershipNumber(String membershipNumber) {
		this.membershipNumber = membershipNumber;
	}

	public User(String username, String password) {
		this.userID = username;
		this.setPassword(password);
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public int getStrikes() {
		return strikes;
	}

	public void setStrikes(int strikes) {
		this.strikes = strikes;
	}

	public enums.UserStatus getStatus() {
		return status;
	}

	public void setStatus(enums.UserStatus status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserID() {
		return userID;
	}
	@Override
	public boolean equals(Object user) {
		return this.userID.equals(((User)user).getUserID());
	}

	@Override
	public String toString() {
		return String.format("User ID: %s%nName: %s %s.%n", userID,firstName,lastName);
	}

}