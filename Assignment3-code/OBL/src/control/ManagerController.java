package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import entity.ConstantsAndGlobalVars;
import entity.FaultsHistory;
import entity.IClient;
import entity.Librarian;
import entity.MsgParser;

/**
 * This class controls the functionality of the {@link entity.Manager} entity.
 * {@code implements} {@link entity.IClient} as it needs to receive responses from the server.
 * Uses the design pattern Singleton to assure that <b>ONLY ONE INSTANCE</b> is created for the lifetime of the program.
 */

import entity.Permissions;
import entity.User;

/**
 * This class controls the functionality of the {@link entity.Manager} entity.
 * {@code implements} {@link entity.IClient} as it needs to receive responses from the server.
 * Uses the design pattern Singleton to assure that <b>ONLY ONE INSTANCE</b> is created for the lifetime of the program.
 */
public class ManagerController implements IClient{
	/**
	 * Instance variables:
	 * client - a {@link control.Client} instance to send messages to the server.
	 * singleton - the single instance of this class
	 * sem - a semaphore that blocks the main thread until a response is received from the server.
	 * semaphore is acquired each time a request to the server is sent and released after the response arrives.
	 * retVal, result - different variables of different types to store return results from server.
	 */
	private Client client;
	private static ManagerController singleton = null;
	private Semaphore sem;
	private boolean retVal;
	private ArrayList<?> result;
	/**
	 * a private constructor that connects the client to the server and initializes some instance variables
	 * including the semaphore which is initialized to 0.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 */
	private ManagerController(String host,int port) {
		try {
			client = new Client(host, port,this);
			sem = new Semaphore(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * returns the instance if it exists, creates one and returns it if it doesn't exist.
	 * marked as {@code synchronized} to ensure thread safety.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 * @return the instance of ManagerController
	 */

	public synchronized static ManagerController getInstance(String host,int port) {
		if(singleton == null)
			singleton = new ManagerController(host,port);
		return singleton;
	}

	/**
	 * disconnects the client from the server.
	 */
	public void disconnectClient() {
		 this.client.quit();
	}

	/**
	 * sends request to the server to change the status of a member.
	 * @param userID - the ID of the user.
	 * @param newStatus - the new status.
	 * @return true if succeeded, false otherwise
	 * @throws InterruptedException - thrown should acquiring the semaphore encounter a problem.
	 */
	public boolean changeMemberStatus(String userID, String newStatus) throws InterruptedException {
		MsgParser<User> msg = new MsgParser<>();
		User u = new User(userID);
		u.setStatus(enums.UserStatus.valueOf(newStatus));
		msg.setTask(ConstantsAndGlobalVars.changeMemberStatusTask);
		msg.addToCommPipe(u);
		
		client.sendMessageToServer(msg);
		sem.acquire();
		
		return retVal;
	}
	/**
	 * send a request to the server to change the borrow permission.
	 * @param userID - the ID of the user.
	 * @param newBorrowPermission - the new borrow permission.
	 * @return true if succeeded, false otherwise.
	 * @throws InterruptedException - thrown should acquiring the semaphore encounter a problem.
	 */
	public boolean changeBorrowPermission(String userID, String newBorrowPermission) throws InterruptedException {
		MsgParser<Permissions> msg = new MsgParser<>();
		Permissions per = new Permissions(userID);
		
		if(newBorrowPermission.equals("NO")) per.setCanBorrow(false);
		else per.setCanBorrow(true);
		
		msg.setTask(ConstantsAndGlobalVars.changeBorrowPermissionTask);
		msg.addToCommPipe(per);
		
		client.sendMessageToServer(msg);
		sem.acquire();
		
		return retVal;
	}
	/**
	 * send a request to the server to change the reserve permission.
	 * @param userID - the ID of the user.
	 * @param newReservePermission - the new reserve permission.
	 * @return true if succeeded, false otherwise.
	 * @throws InterruptedException - thrown should acquiring the semaphore encounter a problem.
	 */
	public boolean changeReservePermission(String userID, String newReservePermission) throws InterruptedException {
		MsgParser<Permissions> msg = new MsgParser<>();
		Permissions per = new Permissions(userID);
		
		if(newReservePermission.equals("NO")) per.setCanReserve(false);
		else per.setCanReserve(true);
		
		msg.setTask(ConstantsAndGlobalVars.changeReservePermissionTask);
		msg.addToCommPipe(per);
		
		client.sendMessageToServer(msg);
		sem.acquire();
		
		return retVal;
	}
	/**
	 * sends a request to the server to fetch all members from the DB.
	 * @return an ArrayList of Users containing the returned result.
	 * @throws InterruptedException - thrown should acquiring the semaphore encounter a problem.
	 */
	public ArrayList<User> getAllMembers() throws InterruptedException{
		MsgParser<User> msg = new MsgParser<>();
		
		msg.setTask(ConstantsAndGlobalVars.getAllMembersTask);

		client.sendMessageToServer(msg);
		sem.acquire();
		
		return (ArrayList<User>) result;
	}
	/**
	 * sends a request to the server to fetch all employees from the DB.
	 * @return an ArrayList of Librarians containing the returned result.
	 * @throws InterruptedException - thrown should acquiring the semaphore encounter a problem.
	 */
	public ArrayList<Librarian> getAllEmployees() throws InterruptedException{
		MsgParser<Librarian> msg = new MsgParser<>();
		
		msg.setTask(ConstantsAndGlobalVars.getAllEmployeesTask);

		client.sendMessageToServer(msg);
		sem.acquire();
		
		return (ArrayList<Librarian>) result;
	}
	
	
	
	/**
	 * receives a message from the server and behaves differently depending on the task,
	 * each task requires a different return type and/or value.
	 * at the end it releases the semaphore. 
	 */
	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		// TODO Auto-generated method stub
		if(msg.getTask().equals(ConstantsAndGlobalVars.changeMemberStatusTask)) {
			retVal = (boolean) msg.getCommPipe().get(0);
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.changeBorrowPermissionTask)) {
			retVal = (boolean) msg.getCommPipe().get(0);
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.changeReservePermissionTask)) {
			retVal = (boolean) msg.getCommPipe().get(0);
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.getAllMembersTask)) {
			result = (ArrayList<User>)msg.getCommPipe();
		}
		
		if(msg.getTask().equals(ConstantsAndGlobalVars.getAllEmployeesTask)) {
			result = (ArrayList<Librarian>)msg.getCommPipe();
		}
		
		
		sem.release();
	}

}
