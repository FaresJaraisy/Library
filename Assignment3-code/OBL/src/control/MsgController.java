package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import entity.Book;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.Message;
import entity.MsgParser;
import entity.User;
/**
 * This class controls the functionality of the {@link entity.Message} entity.
 * {@code implements} {@link entity.IClient} as it needs to receive responses from the server.
 * Uses the design pattern Singleton to assure that <b>ONLY ONE INSTANCE</b> is created for the lifetime of the program.
 */
public class MsgController implements IClient {
	/**
	 * instance variables: 
	 * client - a {@link control.Client} instance to send messages to the server.
	 * messageSingleton - the single instance of this class
	 * sem - a semaphore that blocks the main thread until a response is received from the server.
	 * semaphore is acquired each time a request to the server is sent and released after the response arrives.
	 * msgResult, NumOfMessages, deleteResult - different variables of different types that store the return results from the server.
	 */
	private Client client;
	private static MsgController messageSingleton;
	private Semaphore sem;
	private ArrayList<Message> msgResult;
	int NumOfMessages;
	boolean deleteResult;
	
	/**
	 * a private constructor that connects the client to the server and initializes some instance variables
	 * including the semaphore which is initialized to 0.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 */
	private  MsgController(String host, int port) {
		try {
			client = new Client(host,port,this);
			sem = new Semaphore(0);
			msgResult= new ArrayList<>();
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
	 * @return the instance of MsgController
	 */
	public synchronized static MsgController getInstance(String host, int port) {
		if(messageSingleton == null)
			messageSingleton = new MsgController(host,port);
		return messageSingleton;
	}
	/**
	 * disconnects the client from the server.
	 */
	public void disconnectClient() {
		 this.client.quit();
	}
	
	/**
	 * sends a request to the server to get all messages for a specific librarian.
	 * @param belong - the ID of the librarian.
	 * @return an ArrayList of Messages containing all messages returned from the server.
	 */
	public ArrayList<Message> getMyMessages(String belong) {
		MsgParser<Message> msg = new MsgParser<>();
		msg.setTask(ConstantsAndGlobalVars.getMessagesTask);
		Message messageToSend = new Message(belong);
		msg.addToCommPipe(messageToSend);
		try {
			client.sendMessageToServer(msg);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msgResult;
		
	}
	/**
	 * sends a request to the server to get number of messages of a specific librarian.
	 * @param belong - the ID of the librarian 
	 * @return the number of messages
	 */
	public int getNumberOfMyMessages(String belong) {
		MsgParser<String> msg = new MsgParser<>();
		msg.setTask(ConstantsAndGlobalVars.getNumberOfMessagesTask);
		msg.addToCommPipe(belong);
		try {
			client.sendMessageToServer(msg);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return NumOfMessages;
		
	}
	/**
	 * sends a request to the server to delete a message.
	 * @param message - the message to delete
	 * @return true if succeeded, false otherwise
	 */
	public boolean deleteMessage(Message message)
	{
		MsgParser<Message> msg = new MsgParser<>();
		msg.setTask(ConstantsAndGlobalVars.deleteMessageTask);
		msg.addToCommPipe(message);
		try {
			client.sendMessageToServer(msg);
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deleteResult;
	}
	
	/**
	 * receives a message from the server and behaves differently depending on the task,
	 * each task requires a different return type and/or value.
	 * at the end it releases the semaphore. 
	 */
	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		// TODO Auto-generated method stub
		if(msg.getTask().equals(ConstantsAndGlobalVars.getMessagesTask)) {
			if(!(msg.getCommPipe().isEmpty())) {
				msgResult = (ArrayList<Message>)(msg.getCommPipe());
				}
			
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.getNumberOfMessagesTask)) {
			if(!(msg.getCommPipe().isEmpty())) {
				String num = (String)(msg.getCommPipe().get(0));
				NumOfMessages = Integer.parseInt(num);
				}
			
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.deleteMessageTask)) {
			if(!(msg.getCommPipe().isEmpty())) {
				deleteResult = (boolean)(msg.getCommPipe().get(0));
			
				}
			
		}
		
		sem.release();
	}
	
}
