package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import entity.Book;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.MsgParser;
/**
 * This class enables an recurring user to search.
 * entity. {@code implements} {@link entity.IClient} as it needs to receive
 * responses from the server. Uses the design pattern Singleton to assure that
 * <b>ONLY ONE INSTANCE</b> is created for the lifetime of the program.
 */
public class ReaderController implements IClient {
	/**
	 * Instance variables: client - a {@link control.Client} instance to send
	 * messages to the server. 
	 * ManualDelaySingleton - the single instance of this class 
	 * sem - a semaphore that blocks the main thread until a response is
	 * received from the server. semaphore is acquired each time a request to the
	 * server is sent and released after the response arrives. 
	 * searchResult - an arrayList that stores the return results from the server.
	 */
	private Client client;
	private static ReaderController readerSingleton = null;
	private Semaphore sem;
	private ArrayList<Book> searchResult;
	/**
	 * a private constructor that connects the client to the server and initializes
	 * some instance variables including the semaphore which is initialized to 0.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 */
	private ReaderController(String host, int port) {
		try {
			client = new Client(host, port, this);
			sem = new Semaphore(0);
			searchResult = new ArrayList<>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * returns the instance if it exists, creates one and returns it if it doesn't
	 * exist. marked as {@code synchronized} to ensure thread safety.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 * @return the instance of ReaderController.
	 */
	public synchronized static ReaderController getInstance(String host, int port) {
		if (readerSingleton == null)
			readerSingleton = new ReaderController(host, port);
		return readerSingleton;
	}
	/**
	 * disconnects the client from the server.
	 */
	public void disconnectClient() {
		this.client.quit();
	}
	/**
	 * sends a request to the server to fetch a list of books that match a certain search pattern.
	 * @param type - the search type.
	 * @param keyWord - the keyword
	 * @return an arrayList of Book objects containing the search result.
	 */
	public ArrayList<Book> searchForBook(String type, String keyWord) {
		MsgParser<String> msg = new MsgParser<>();
		msg.setTask(ConstantsAndGlobalVars.searchBookTask);
		//commPipe[0] = type, commPipe[1] = keyword
		msg.addToCommPipe(type);
		msg.addToCommPipe(keyWord);
		searchResult.clear();
		try {
			client.sendMessageToServer(msg);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchResult;
		
	}
	/**
	 * receives a message from the server and behaves differently depending on the task,
	 * each task requires a different return type and/or value.
	 * at the end it releases the semaphore. 
	 */
	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		// TODO Auto-generated method stub
		if(msg.getTask().equals(ConstantsAndGlobalVars.searchBookTask)) {
			if(!msg.getCommPipe().isEmpty())
				searchResult = (ArrayList<Book>) msg.getCommPipe();
		}
		sem.release();
	}
}
