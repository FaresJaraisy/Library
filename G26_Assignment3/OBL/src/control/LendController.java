package control;

import java.io.IOException;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import entity.BookCopies;
import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.MsgParser;
import entity.Reservations;
import entity.User;


/**
 * This class controls the functionality of the lend operation.
 * {@code implements} {@link entity.IClient} as it needs to receive responses
 * from the server. Uses the design pattern Singleton to assure that <b>ONLY ONE
 * INSTANCE</b> is created for the lifetime of the program.
 */
public class LendController implements IClient {
	/**
	 * Instance variables: client - a {@link control.Client} instance to send
	 * messages to the server. lendSingleton - the single instance of this class
	 * sem1 - a semaphore that blocks the main thread until a response is received
	 * from the server. semaphore is acquired each time a request to the server is
	 * sent and released after the response arrives. copy, borrowresult,
	 * updatecopyresult, numOfBorrowCopy - different variables of different types
	 * that store the return results from the server.
	 */

	private Client client;
	private static LendController lendSingleton;
	private User user;
	private BookCopies copy;
	private MsgParser Msg1;
	private MsgParser Msg2;
	private Semaphore sem1;
	private enums.ExistStatus userexist;
	private enums.BookType type;
	private enums.Result borrowresult;
	private enums.UpdateCopyResult updatecopyresult;
	private int numOfBorrowCopy;
	private int reserveExist;
	private enums.Result updateReserveResult;

	/**
	 * a private constructor that connects the client to the server and initializes
	 * some instance variables including the semaphore which is initialized to 0.
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 */
	private LendController(String host, int port) {
		try {
			client = new Client(host, port, this);
			sem1 = new Semaphore(0);
			// result= new ArrayList<>();
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
	 * returns the instance if it exists, creates one and returns it if it doesn't
	 * exist. marked as {@code synchronized} to ensure thread safety.
	 * 
	 * @param host - the IP address of the server.
	 * @param port - the port which the server dwells on.
	 * @return the instance of LendController
	 */
	public synchronized static LendController getInstance(String host, int port) {
		if (lendSingleton == null)
			lendSingleton = new LendController(host, port);
		return lendSingleton;
	}

	public MsgParser checkuser(String username) throws InterruptedException {

		MsgParser<User> msg = new MsgParser<>();
		User user = new User(username);
		msg.addToCommPipe(user);
		msg.setTask(ConstantsAndGlobalVars.checkUserTask);

		client.sendMessageToServer(msg);
		sem1.acquire();

		return Msg1;

	}

	public BookCopies checkcopy(String barcode) throws InterruptedException {

		MsgParser<BookCopies> msg = new MsgParser<>();
		BookCopies copy = new BookCopies(barcode);
		msg.addToCommPipe(copy);
		msg.setTask(ConstantsAndGlobalVars.checkCopyTask);

		client.sendMessageToServer(msg);
		sem1.acquire();
		// System.out.println(this.copy.getBarcode());
		// System.out.println(this.copy.getCatalogNumber());
		// System.out.println(this.copy.getStatus());
		return this.copy;

	}

	public MsgParser checkbooktype(String catalognum, String barcode) throws InterruptedException {

		MsgParser<BookCopies> msg = new MsgParser<>();
		BookCopies copy = new BookCopies(barcode, catalognum);
		msg.addToCommPipe(copy);
		msg.setTask(ConstantsAndGlobalVars.checkBookTypeTask);

		client.sendMessageToServer(msg);
		sem1.acquire();

		return Msg2;

	}

	public int checknumofcopies(String barcode) throws InterruptedException {
		MsgParser<BookCopies> msg = new MsgParser<>();
		BookCopies copy = new BookCopies(barcode);
		msg.addToCommPipe(copy);
		msg.setTask(ConstantsAndGlobalVars.checkNumOfCopyTask);

		client.sendMessageToServer(msg);
		sem1.acquire();
		// System.out.println(this.copy.getBarcode());
		// System.out.println(this.copy.getCatalogNumber());
		// System.out.println(this.copy.getStatus());
		return this.numOfBorrowCopy;

	}

	public enums.Result updateborrow(String userName, String copyBarcod, String librarianID, Date currentDate,
			Date lendDate, enums.BorrowStatus status) throws InterruptedException {

		MsgParser<Borrows> msg = new MsgParser<>();
		Borrows borrow = new Borrows(userName, copyBarcod, librarianID, currentDate, lendDate, null, status);

		msg.addToCommPipe(borrow);
		msg.setTask(ConstantsAndGlobalVars.updateBorrowTask);

		client.sendMessageToServer(msg);
		sem1.acquire();

		return borrowresult;

	}

	public enums.UpdateCopyResult updatebookcopy(String barcode, String catalogNumber, Date purchaseDate,
			enums.BookCopyStatus status) throws InterruptedException {

		MsgParser<BookCopies> msg = new MsgParser<>();
		BookCopies bookcopy = new BookCopies(barcode, catalogNumber, purchaseDate, status);

		msg.addToCommPipe(bookcopy);
		msg.setTask(ConstantsAndGlobalVars.updateBookCopyTask);

		client.sendMessageToServer(msg);
		sem1.acquire();
		
		return updatecopyresult;
	
		}
public int checkreserve(String userID,String barcode) throws InterruptedException {
	
	MsgParser<Reservations> msg = new MsgParser<>();
	Reservations reserve = new Reservations(userID,barcode);
	
	msg.addToCommPipe(reserve);
	msg.setTask(ConstantsAndGlobalVars.checkIfReserveExistTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return reserveExist;

	}
public enums.Result updateReserve(String userID,String barcode) throws InterruptedException {
	
	MsgParser<Reservations> msg = new MsgParser<>();
	Reservations reserve = new Reservations(userID,barcode);
	
	msg.addToCommPipe(reserve);
	msg.setTask(ConstantsAndGlobalVars.updateReservestatusToDoneTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return updateReserveResult;

	}
	
	@Override
	public void recieveMessageFromServer(MsgParser msg) {
		if (msg.getTask().equals(ConstantsAndGlobalVars.checkUserTask)) {
			// if the task is log in, retrieve data from server.
			this.Msg1 = msg;

			user = (User) Msg1.getCommPipe().get(0);

		}
		if (msg.getTask().equals(ConstantsAndGlobalVars.checkCopyTask)) {
			// if the task is log in, retrieve data from server.

			copy = (BookCopies) msg.getCommPipe().get(0);
			// System.out.println(copy.getCatalogNumber());

		}

		if (msg.getTask().equals(ConstantsAndGlobalVars.checkBookTypeTask)) {
			// if the task is log in, retrieve data from server.

			type = msg.getType();
			this.Msg2 = msg;

		}
		if (msg.getTask().equals(ConstantsAndGlobalVars.updateBorrowTask)) {
			// if the task is log in, retrieve data from server.

			borrowresult = msg.getBorrowresult();

		}
		if (msg.getTask().equals(ConstantsAndGlobalVars.checkNumOfCopyTask)) {
			// if the task is log in, retrieve data from server.
			this.numOfBorrowCopy = msg.getNumOfBorrowCopies();

		}
		if (msg.getTask().equals(ConstantsAndGlobalVars.updateBookCopyTask)) {
			// if the task is log in, retrieve data from server.
			updatecopyresult = msg.getUpdatecopyresult();

		}

		if(msg.getTask().equals(ConstantsAndGlobalVars.checkIfReserveExistTask)) {
			//if the task is log in, retrieve data from server.
		 reserveExist=(int)msg.getCommPipe().get(0);
			
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.updateReservestatusToDoneTask)) {
			//if the task is log in, retrieve data from server.
		 updateReserveResult=msg.getUpdateReservationsResult();
			
		}
		sem1.release();	
	}
}
