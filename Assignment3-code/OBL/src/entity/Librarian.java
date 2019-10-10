package entity;

import java.io.Serializable;
/**
 * The entity class that stores librarians.
 * {@code extends} {@link entity.User}
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 */
public class Librarian extends User implements Serializable{
	/**
	 * instance variables:
	 */
	private String employeeNumber;
	private String departmentName;
	private String role;
	/**
	 * empty constructor:
	 */
	public Librarian() {}
	/**
	 * {@inheritDoc}
	 * @param userID
	 * @param fName
	 * @param lName
	 * @param phoneNumber
	 * @param password
	 * @param email
	 * @param status
	 * @param employeeNumber
	 * @param departmentName
	 * @param role
	 */
	public Librarian(String userID, String fName, String lName, String phoneNumber, String password, String email,enums.UserStatus status,String employeeNumber,String departmentName,String role)
	{
		super(userID, fName,  lName,  phoneNumber,  password, email,status);
		this.employeeNumber=employeeNumber;
		this.departmentName=departmentName;
		this.role=role;
	}
	/**
	 * 
	 * @return
	 */
	public String getEmployeeNumber() {
		return employeeNumber;
	}
	/**
	 * 
	 * @param employeeNumber
	 */
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}
	/**
	 * 
	 * @return
	 */
	public String getDepartmentName() {
		return departmentName;
	}
	/**
	 * 
	 * @param departmentName
	 */
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	/**
	 * 
	 * @return
	 */
	public String getRole() {
		return role;
	}
	/**
	 * 
	 * @param role
	 */
	public void setRole(String role) {
		this.role = role;
	}
}
