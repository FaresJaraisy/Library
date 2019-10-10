package control;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import entity.BookCopies;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.MsgParser;
/**
 * This class controls the functionality of the {@link entity.BookCopies} entity.
 * {@code implements} {@link entity.IClient} as it needs to receive responses from the server.
 * Uses the design pattern Singleton to assure that <b>ONLY ONE INSTANCE</b> is created for the lifetime of the program.
 */
public class BookCopyController implements IClient{
	/**
	 * Instance variables:
	 * client - a {@link control.Client} instance to send messages to the server.
	 * singleton - the single instance of this class
	 * sem - a semaphore that blocks the main thread until a response is received from the server.
	 * semaphore is acquired each time a request to the server is sent and released after the response arrives.
	 * addCopyResult, numberOfAvailableCopies - integer variables that store the return results from the server.
	 */
	private Client client;
	private static BookCopyController singleton = null;
	private Semaphore sem;
	private int addCopyResult = 0;
	private int numberOfAvailableCopies;
	//===========================================================//
	/**
	 * a private constructor that connects the client to the server and initializes some instance variables
	 * including the semaphore which is initialized to 0.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 */
	private BookCopyController(String host,int port) {
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
	 * @return the instance of BookCopyController
	 */
	public synchronized static BookCopyController getInstance(String host,int port) {
		if(singleton == null)
			singleton = new BookCopyController(host,port);
		return singleton;
	}
	/**
	 * disconnects the client from the server.
	 */
	public void disconnectClient() {
		 this.client.quit();
	}
	//===========================================================//
	/**
	 * sends a request to the server to add a new book copy.
	 * @param bookcopy - the book copy to add
	 * @return 0 if number of total copies for the book equals number of copies in the table,
	 * 1 if addition succeeded, 2 if an error occurred.
	 */
	public int addCopy(BookCopies bookcopy) {
		MsgParser<BookCopies> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.addCopyTask);
		mp.addToCommPipe(bookcopy);
		
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		return addCopyResult;
	}
	/**
	 * sends a request to the server to get number of available copies.
	 * @param catalogNumber - the catalog number of the book.
	 * @return (-1) if an error occurred or number of available copies (a value greater or equals to zero) otherwise.
	 */
	public int getNumberOfAvailableCopies(String catalogNumber) {
		MsgParser<String> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.getNumberOfAvailableCopies);
		mp.addToCommPipe(catalogNumber);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberOfAvailableCopies;
	}
	//===========================================================//

	//receive responses from server.
	/**
	 * receives a message from the server and behaves differently depending on the task,
	 * each task requires a different return type and/or value.
	 * at the end it releases the semaphore. 
	 */
	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		if(msg.getTask().equals(ConstantsAndGlobalVars.addCopyTask)) {
			if(msg.getCommPipe().isEmpty()) {
				addCopyResult = 0;
			}else {
				boolean result = (Boolean) msg.getCommPipe().get(0);
				addCopyResult = (result)?1:2;
			}
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumberOfAvailableCopies)) {
			if(msg.getCommPipe().isEmpty()) {
				numberOfAvailableCopies = -1;
			}else {
				numberOfAvailableCopies = (int) msg.getCommPipe().get(0);
			}
		}
		sem.release();
	}
}
