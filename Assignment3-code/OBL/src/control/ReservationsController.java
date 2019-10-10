package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.MsgParser;
import entity.Reservations;
import entity.User;
/**
 * This class controls the functionality of the {@link entity.Reservations} entity.
 * {@code implements} {@link entity.IClient} as it needs to receive responses from the server.
 * Uses the design pattern Singleton to assure that <b>ONLY ONE INSTANCE</b> is created for the lifetime of the program.
 */
public class ReservationsController implements IClient {
	/**
	 * Instance variables:
	 * client - a {@link control.Client} instance to send messages to the server.
	 * reservationsSingleton - the single instance of this class
	 * sem1 - a semaphore that blocks the main thread until a response is received from the server.
	 * semaphore is acquired each time a request to the server is sent and released after the response arrives.
	 * result - an ArrayList of Reservation objects that store the return results from the server.
	 */
	private Client client;
	private static ReservationsController reservationsSingleton;
	private User currentUser;
	private Semaphore sem1;
	private static ArrayList<Reservations> result;
	/**
	 * a private constructor that connects the client to the server and initializes some instance variables
	 * including the semaphore which is initialized to 0.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 */
	private  ReservationsController(String host, int port) {
		try {
			client = new Client(host,port,this);
			sem1 = new Semaphore(0);
			result= new ArrayList<>();
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
	public synchronized static ReservationsController getInstance(String host, int port) {
		if(reservationsSingleton == null)
			reservationsSingleton = new ReservationsController(host,port);
		return reservationsSingleton;
	}
	/**
	 * sends a request to the server to fetch a list of pending reservations of a specific user.
	 * @param currentUser - the user to fetch the reservations for.
	 * @return an arrayList containing the result.
	 * @throws InterruptedException - thrown should acquiring the semaphore encounter any problems.
	 */
	public ArrayList<Reservations> viewreservations(User currentUser) throws InterruptedException {
		MsgParser<User> msg = new MsgParser<>();
		msg.addToCommPipe(currentUser);
		msg.setTask(ConstantsAndGlobalVars.getReservationsTask);
		
		client.sendMessageToServer(msg);
		
		sem1.acquire();
		return result;
		}
	/**
	 * receives a message from the server and behaves differently depending on the task,
	 * each task requires a different return type and/or value.
	 * at the end it releases the semaphore. 
	 */
	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		if(msg.getTask().equals(ConstantsAndGlobalVars.getReservationsTask)) {
			for (Reservations reservations : (ArrayList<Reservations>)msg.getCommPipe()) {
				result.add(reservations);
			}
			
			sem1.release();
		}
			
	}
}
