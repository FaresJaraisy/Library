package entity;

import enums.UserStatus;
/**
 * {@inheritDoc}
 * The entity class that stores managers.
 * {@code extends} {@link entity.Librarian}
 */
public class Manager extends Librarian{
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
	public Manager(String userID, String fName, String lName, String phoneNumber, String password, String email,
			UserStatus status, String employeeNumber, String departmentName, String role) {
		super(userID, fName, lName, phoneNumber, password, email, status, employeeNumber, departmentName, role);
	}
	
}
