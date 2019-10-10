package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import entity.Book;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.MsgParser;
import entity.User;
/**
 * This class controls the functionality of the {@link entity.User} entity.
 * {@code implements} {@link entity.IClient} as it needs to receive responses from the server.
 * Uses the design pattern Singleton to assure that <b>ONLY ONE INSTANCE</b> is created for the lifetime of the program.
 */
public class UserController implements IClient{
	/**
	 * Instance variables:
	 * client - a {@link control.Client} instance to send messages to the server.
	 * userSingleton - the single instance of this class
	 * sem - a semaphore that blocks the main thread until a response is received from the server.
	 * semaphore is acquired each time a request to the server is sent and released after the response arrives.
	 * currentUser, logInResult, logoutResult, 
	 * 	updateSettinResult, Msg - different variables of different types that store the return results from the server.
	 */
	private Client client;
	private static UserController userSingleton;
	private Semaphore sem;
	private User currentUser;
	private enums.LogInStatus logInResult;
	private boolean logoutResult;
	private boolean updateSettinResult;
	MsgParser Msg;
	/**
	 * a private constructor that connects the client to the server and initializes some instance variables
	 * including the semaphore which is initialized to 0.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 */
	private  UserController(String host, int port) {
		try {
			client = new Client(host,port,this);
			sem = new Semaphore(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * disconnects the client from the server.
	 */
	public void disconnectClient() {
		 this.client.quit();
	}
	/**
	 * returns the instance if it exists, creates one and returns it if it doesn't exist.
	 * marked as {@code synchronized} to ensure thread safety.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 * @return the instance of LibrarianController
	 */
	public synchronized static UserController getInstance(String host, int port) {
		if(userSingleton == null)
			userSingleton = new UserController(host,port);
		return userSingleton;
	}
	/**
	 * sends a request to the server to log in.
	 * @param username - the user ID.
	 * @param password - the password
	 * @param table - the table in which to search for the user
	 * @return a MsgParser object containing a User object if it exists and a login result
	 * (Wrong password, already logged in, user doesn't exist).
	 * @throws InterruptedException
	 */
	public MsgParser login(String username, String password,String table) throws InterruptedException {
		MsgParser<User> msg = new MsgParser<>();
		User user = new User(username,password);
		
		msg.setTask(ConstantsAndGlobalVars.loginTask);
		msg.setTableName(table);
		msg.addToCommPipe(user);
		
		client.sendMessageToServer(msg);
		sem.acquire();
		return this.Msg;
	}
	/**
	 * sends a request to the server to update the settings of the specified user.
	 * @param currentUser - the user for whom to change the settings for.
	 * @return true if succeeded, false otherwise.
	 * @throws InterruptedException - thrown should acquiring the semaphore encounter a problem
	 */
	public boolean updateSetting(User currentUser) throws InterruptedException{
		MsgParser<User> msg = new MsgParser<>();
		msg.addToCommPipe(currentUser);
		msg.setTask(ConstantsAndGlobalVars.updateSettingTask);
		client.sendMessageToServer(msg);
		// add semaphore and return actual result
		sem.acquire();
		//System.out.println("setting updated successfully");
		return updateSettinResult;
	}
	/**
	 * sends a request to the server to logout.
	 * @param currentUser - the user.
	 * @return true if succeeded, false otherwise.
	 * @throws InterruptedException - thrown should acquiring the semaphore encounter a problem.
	 */
	public boolean logout(User currentUser) throws InterruptedException {
		MsgParser<User> msg = new MsgParser<>();
		msg.addToCommPipe(currentUser);
		msg.setTask(ConstantsAndGlobalVars.logoutTask);
		
		client.sendMessageToServer(msg);
		sem.acquire();
		/*
		 * 
		 */
		//System.out.println("user logged out");
		return logoutResult;
	}
	/**
	 * receives a message from the server and behaves differently depending on the task,
	 * each task requires a different return type and/or value.
	 * at the end it releases the semaphore. 
	 */
	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		if(msg.getTask().equals(ConstantsAndGlobalVars.loginTask)) {
			//if the task is log in, retrieve data from server.
			this.Msg=msg;
			logInResult = msg.getReturnResult();
			if(logInResult == enums.LogInStatus.Success)
				currentUser = ((User) msg.getCommPipe().get(0));
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.logoutTask)) {
			String result=(String)msg.getCommPipe().get(0);
			if(result.equals("Success"))
				logoutResult = true;
			else
				logoutResult = false;
		}
		/*----setting------*/
		if(msg.getTask().equals(ConstantsAndGlobalVars.updateSettingTask)) {
			String result=(String)msg.getCommPipe().get(0);
			if(result.equals("Success"))
				updateSettinResult = true;
			else
				updateSettinResult = false;
		}
		sem.release();	
	}
	public User getCurrentUser() {
		return currentUser;
	}
}
