package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import entity.Book;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.MsgParser;
import entity.Reservations;
/**
 * This class controls the functionality of the {@link entity.Book} entity.
 * {@code implements} {@link entity.IClient} as it needs to receive responses from the server.
 * Uses the design pattern Singleton to assure that <b>ONLY ONE INSTANCE</b> is created for the lifetime of the program.
 */
public class BookController implements IClient{
	/**
	 * Instance variables:
	 * client - a {@link control.Client} instance to send messages to the server.
	 * singleton - the single instance of this class
	 * sem - a semaphore that blocks the main thread until a response is received from the server.
	 * semaphore is acquired each time a request to the server is sent and released after the response arrives.
	 * totalNumberOfBooks, totalNumberOfReserves, addBookResult, removeBookResult, updateBookResult, addReserveResult,
	 *  resultBook, allCategories - different variables of different types that store the return results from the server.
	 */
	private Client client;
	private static BookController singleton = null;
	private Semaphore sem;
	private int totalNumberOfBooks;
	private int totalNumberOfReserves;
	private boolean addBookResult = false;
	private boolean removeBookResult = false;
	private boolean updateBookResult = false;
	private int addReserveResult;
	private Book resultBook = null;
	private ArrayList<String> allCategories;
	
	/**
	 * a private constructor that connects the client to the server and initializes some instance variables
	 * including the semaphore which is initialized to 0.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 */
	private BookController(String host,int port) {
		try {
			client = new Client(host, port,this);
			sem = new Semaphore(0);
			allCategories = new ArrayList<>();
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
	 * @return the instance of BookController
	 */
	public synchronized static BookController getInstance(String host,int port) {
		if(singleton == null)
			singleton = new BookController(host,port);
		return singleton;
	}
	/**
	 * disconnects the client from the server.
	 */
	public void disconnectClient() {
		 this.client.quit();
	}
	/**
	 * sends a request to the server to get total number of books.
	 * @return number of books.
	 */
	public int getNumberOfBooks() {
		MsgParser mp = new MsgParser();
		mp.setTask(ConstantsAndGlobalVars.getNumberOfBooksTask);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalNumberOfBooks;
	}
	/**
	 * sends a request to the server to add a new book.
	 * @param b - the book to add
	 * @return true if addition succeeded, false otherwise.
	 */
	public boolean addBook(Book b) {
		MsgParser<Book> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.addBookTask);
		mp.addToCommPipe(b);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return addBookResult;

	}
	/**
	 * sends a request to the server to update an existing book.
	 * @param b - the book to update
	 * @return true if update succeeded, false otherwise.
	 */
	public boolean updateBook(Book b) {
		MsgParser<Book> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.updateBookTask);
		mp.addToCommPipe(b);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return updateBookResult;

	}
	/**
	 * sends a request to the server to fetch a book from the DB.
	 * @param catalogNumber - the catalog number of the book to be fetched
	 * @return the book with the passed catalog number if it exists, null otherwise.
	 */
	public Book getBook(String catalogNumber) {
		MsgParser<String> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.getBookTask);
		mp.addToCommPipe(catalogNumber);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(resultBook != null)
			System.out.println("success");
		
		return resultBook;
	}
	/**
	 * sends a request to the server to delete an existing book.
	 * @param newReservePermission - the catalog number of the book to delete
	 * @return true if deletion succeeded, false otherwise.
	 */
	public boolean removeBook(String catalognumber) {
		MsgParser<String> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.removeBookTask);
		mp.addToCommPipe(catalognumber);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return removeBookResult; 
	}
	/**
	 * sends a request to the server to fetch all existing categories.
	 * @return an arrayList containing all categories existing in the DB 
	 */
	public ArrayList<String> getAllCategories(){
		MsgParser<String> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.getAllCategoriesTask);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allCategories;
	}
	/**
	 * gets the total number of reservations on the book with the passed catalog number.
	 * @param catalogNumber - the catalog number of the book for which to fetch number of reservations.
	 * @return number of reservations on that book
	 */
	public int getNumberOfReserves(String catalogNumber) {
		MsgParser<String> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.getNumberOfReservesTask);
		mp.addToCommPipe(catalogNumber);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		return totalNumberOfReserves;
	}
	/**
	 * 
	 * @param catalogNumber - the catalog number of the book to reserve
	 * @param userID - the user ID of the reserver.
	 * @return -1 for SQL Exception, 0 for success, 1 for duplicate reservation (i.e. already reserved),
	 * 2 couldn't add the tuple, 3 no sufficient permissions.
	 */
	public int addReserve(String catalogNumber,String userID) {
		MsgParser<String> mp = new MsgParser<>();
		mp.setTask(ConstantsAndGlobalVars.addReserveTask);
		mp.addToCommPipe(userID);
		mp.addToCommPipe(catalogNumber);
		try {
			client.sendMessageToServer(mp);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return addReserveResult;
		
	}
	//handle messages from server...
	/**
	 * receives a message from the server and behaves differently depending on the task,
	 * each task requires a different return type and/or value.
	 * at the end it releases the semaphore. 
	 */
	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumberOfBooksTask)) {
			if((int) msg.getCommPipe().get(0) > 0)
				totalNumberOfBooks = (int) msg.getCommPipe().get(0);
			else
				totalNumberOfBooks = 0;
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.addBookTask)) {
			addBookResult = (Boolean) msg.getCommPipe().get(0);
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.removeBookTask)) {
			removeBookResult = (Boolean) msg.getCommPipe().get(0);
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.getBookTask)) {
			if(!msg.getCommPipe().isEmpty())
				resultBook = (Book) msg.getCommPipe().get(0);
			System.out.println(resultBook);
			System.out.println("reddd");
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.updateBookTask)) {
			updateBookResult = (Boolean) msg.getCommPipe().get(0);
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.getBookTask)) {
			if(!msg.getCommPipe().isEmpty())
				resultBook = (Book) msg.getCommPipe().get(0);
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.getAllCategoriesTask)) {
			if(!msg.getCommPipe().isEmpty())
				allCategories = (ArrayList<String>) msg.getCommPipe();
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumberOfReservesTask)) {
			if(!msg.getCommPipe().isEmpty())
				totalNumberOfReserves = (int) msg.getCommPipe().get(0);
			else
				totalNumberOfReserves = -1;
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.addReserveTask)) {
			addReserveResult = (int) msg.getCommPipe().get(0);
		}
		sem.release();
	}
}
