package entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
/**
 * This class is used to store messages sent to the server and back.
 * {@code implements} {@link java.io.Serializable} since it need to be passed to the server.
 * @param <E> - the type of variables to store in the ArrayList
 */
public class MsgParser<E> implements Serializable {
	/**
	 * instance variables:
	 * task - the task the server will do.
	 * returnResult - the return result of the log in task
	 * isExist - 
	 * per - stores the permissions of a user.
	 * type - stores the book type
	 * updatecopyresult - stores the result of the insertion update of into the table 'BookCopies' (success, failure)
	 * numOfCopies - stores total number of copies for a book
	 * numOfBorrowCopies - stores the total number of borrowed copies for a book.
	 * tableName - used to indicate in which table to search for the user when logging in.
	 * BorrowDate - stores the date of the borrow.
	 * returnDate - stores the date of return.
	 * updateborrowResult, delayResult, updateReservationResult - the status of the insertion, update to the table (success,failure or Occur - duplicate entry)
	 * commPipe - An ArrayList, the communication pipe between the server and the client (and vice versa), 
	 * 				the client sends the parameters of the task via this pipe,  
	 * 				and the server takes the parameters, clears the pipe and sends the result of the task via this pipe too.
	 * intResult - stores all integer results 
	 */
	private String task; // task ={login,borrowbook,....}
	//private enums.ReturnType returnType;
	//private boolean returnResult;
	private enums.LogInStatus returnResult;
	private enums.ExistStatus isExist;
	private enums.UserPermissions per;
	private enums.BookType type;
	private enums.Result updateborrowresult;
	private enums.UpdateCopyResult updatecopyresult;
	private int numOfCopies;
	private int numOfBorrowCopies;
	private String tableName;
	private Timestamp BorrowDate;
	private Date returnDate;
	private enums.Result delayResult;
	private enums.Result updateReservationsResult;
	private ArrayList<E> commPipe; // communication pipe between server and client
	private int intResult;
	/**
	 * a zero argument constructor, initializes the pipe.
	 */
	public MsgParser(){
		commPipe = new ArrayList<>();
	}
	/**
	 * gets the task.
	 * @return the task assigned to the server.
	 */
	public String getTask() {
		return task;
	}
	/**
	 * set the task assigned to the server.
	 * @param task - the task to be assigned
	 */
	public void setTask(String task) {
		this.task = task;
	}
	/*public enums.ReturnType getReturnType() {
		return returnType;
	}
	public void setReturnType(enums.ReturnType returnType) {
		this.returnType = returnType;
	}*/
	/**
	 * 
	 * @return the communication pipe
	 */
	public ArrayList<E> getCommPipe() {
		return commPipe;
	}
	/**
	 * adds an object to the communication pipe.
	 * @param object - the object to add
	 */
	public void addToCommPipe(E object) {
		this.commPipe.add(object);
	}
	/**
	 * clears the communication pipe of all objects.
	 */
	public void clearCommPipe()
	{
		this.commPipe.clear();
	}
	/**
	 * 
	 * @param result
	 */
	public void setReturnResult(enums.LogInStatus result) {
		this.returnResult = result;
	}
	/**
	 * 
	 * @return
	 */
	public enums.LogInStatus getReturnResult() {
		return this.returnResult;
	}
	/**
	 * 
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * 
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * 
	 * @return
	 */
	public enums.ExistStatus getIsExist() {
		return isExist;
	}
	/**
	 * 
	 * @param isExist
	 */
	public void setIsExist(enums.ExistStatus isExist) {
		this.isExist = isExist;
	}
	/**
	 * 
	 * @return
	 */
	public enums.UserPermissions getPer() {
		return per;
	}
	/**
	 * 
	 * @param per
	 */
	public void setPer(enums.UserPermissions per) {
		this.per = per;
	}
	/**
	 * 
	 * @return
	 */
	public enums.BookType getType() {
		return type;
	}
	/**
	 * 
	 * @param type
	 */
	public void setType(enums.BookType type) {
		this.type = type;
	}
	/**
	 * 
	 * @return
	 */
	public enums.Result getBorrowresult() {
		return updateborrowresult;
	}
	/**
	 * 
	 * @param borrowresult
	 */
	public void setBorrowresult(enums.Result borrowresult) {
		this.updateborrowresult = borrowresult;
	}
	/**
	 * 
	 * @return
	 */
	public int getNumOfCopies() {
		return numOfCopies;
	}
	/**
	 * 
	 * @param numOfCopies
	 */
	public void setNumOfCopies(int numOfCopies) {
		this.numOfCopies = numOfCopies;
	}
	/**
	 * 
	 * @return
	 */
	public int getNumOfBorrowCopies() {
		return numOfBorrowCopies;
	}
	/**
	 * 
	 * @param numOfBorrowCopies
	 */
	public void setNumOfBorrowCopies(int numOfBorrowCopies) {
		this.numOfBorrowCopies = numOfBorrowCopies;
	}
	/**
	 * 
	 * @return
	 */
	public enums.UpdateCopyResult getUpdatecopyresult() {
		return updatecopyresult;
	}
	/**
	 * 
	 * @param updatecopyresult
	 */
	public void setUpdatecopyresult(enums.UpdateCopyResult updatecopyresult) {
		this.updatecopyresult = updatecopyresult;
	}
	/**
	 * 
	 * @return
	 */
	public Timestamp getBorrowDate() {
		return BorrowDate;
	}
	/**
	 * 
	 * @param borrowDate
	 */
	public void setBorrowDate(Timestamp borrowDate) {
		BorrowDate = borrowDate;
	}
	/**
	 * 
	 * @return
	 */
	public enums.Result getDelayResult() {
		return delayResult;
	}
	/**
	 * 
	 * @param delayResult
	 */
	public void setDelayResult(enums.Result delayResult) {
		this.delayResult = delayResult;
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
	public enums.Result getUpdateReservationsResult() {
		return updateReservationsResult;
	}
	/**
	 * 
	 * @param updateReservationsResult
	 */
	public void setUpdateReservationsResult(enums.Result updateReservationsResult) {
		this.updateReservationsResult = updateReservationsResult;
	}
	/**
	 * 
	 * @return
	 */
	public int getIntResult() {
		return intResult;
	}
	/**
	 * 
	 * @param intResult
	 */
	public void setIntResult(int intResult) {
		this.intResult = intResult;
	}
	
}
