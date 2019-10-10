package control;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Timer;

import entity.BookCopies;
import entity.ConstantsAndGlobalVars;
import entity.MsgParser;
import entity.User;
import ocsf.server.*;

public class Server extends AbstractServer {
	// Class variables *************************************************
	/**
	 * instance variable:
	 * sv - a static instance of Server to facilitate disconnection
	 * dbController - a static instance of DBController to call methods from {@link control.DBController} class.
	 */
	public static Server sv;
	public static DBController dbController;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the server and the DBController.
	 * @param port The port number to connect on.
	 */
	public Server(int port) {
		super(port);
		dbController = new DBController();
		
		// TODO Auto-generated constructor stub
	}

	// Instance methods ************************************************
	/**
	 * This method handles any messages received from the client.
	 * The message is of type {@link entity.MsgParser} and the task can be found via {@link entity.MsgParser#getTask()} method,
	 * and a respective method of {@link control.DBController} is called.
	 * @param msg - The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {

		MsgParser clientMsg = ((MsgParser) msg);
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.loginTask)) {
			clientMsg = dbController.userLogin(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.searchBookTask)) {
			clientMsg = dbController.searchForBook(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getMessagesTask)) {
			clientMsg = dbController.getMessages(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.logoutTask)) {
			clientMsg = dbController.userLogout(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.updateSettingTask)) {

			clientMsg = dbController.userSettingUpdate(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.returnBookTask)) {

			try {
				clientMsg = dbController.returnBookUpdate(clientMsg);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
			}
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getBorrowsTask)) {
			// System.out.println(clientMsg.getCommPipe().get(0));
			clientMsg = dbController.userBorrow(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getReservationsTask)) {
			// System.out.println(clientMsg.getCommPipe().get(0));
			clientMsg = dbController.userReservations(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getHistoryTask)) {
			// System.out.println(clientMsg.getCommPipe().get(0));
			clientMsg = dbController.userHistory(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkUserTask)) {
			clientMsg = dbController.checkUser(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumberOfBooksTask)) {
			clientMsg = dbController.numberOfBooks(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkCopyTask)) {
			clientMsg = dbController.checkCopy(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.addNewUserTask)) {
			clientMsg = dbController.addNewUser(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkBookTypeTask)) {
			clientMsg = dbController.checkBookType(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.addBookTask)) {
			try {
				clientMsg = dbController.addBook(clientMsg);
				client.sendToClient(clientMsg);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.updateBorrowTask)) {
			try {
				clientMsg = dbController.updateBorrowTable(clientMsg);
				client.sendToClient(clientMsg);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.removeBookTask)) {
			clientMsg = dbController.removeBook(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getBookTask)) {
			clientMsg = dbController.getBook(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.updateBookTask)) {
			try {
				clientMsg = dbController.updateBook(clientMsg);
				client.sendToClient(clientMsg);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkNumOfCopyTask)) {
			clientMsg = dbController.checkNumOfCopy(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getUserTask)) {
			try {
				clientMsg = dbController.searchForUser(clientMsg);
				client.sendToClient(clientMsg);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.addCopyTask)) {
			try {
				clientMsg = dbController.addCopy(clientMsg);
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getFaultsHistoryTask)) {
			clientMsg = dbController.userFaultsHistory(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.updateBookCopyTask)) {
			try {
				clientMsg = dbController.updatebookcopytable(clientMsg);
				client.sendToClient(clientMsg);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkBorrowTask)) {
			try {
				clientMsg = dbController.checkborrow(clientMsg);
				client.sendToClient(clientMsg);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkEmployeeTypeTask)) {
			clientMsg = dbController.checkEmployeeType(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.UpdateBorrowTableAfterDelayingTask)) {
			try {
				clientMsg = dbController.UpdateBorrowTableAfterDelaying(clientMsg);
				client.sendToClient(clientMsg);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.UpdateDelayTableTask)) {
			try {
				clientMsg = dbController.UpdateDelayTableTask(clientMsg);
				client.sendToClient(clientMsg);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumberOfMessagesTask)) {
			clientMsg = dbController.getNumberMsg(clientMsg);
      try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getAllCategoriesTask)) {
			clientMsg = dbController.getAllCategories(clientMsg);
      try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(clientMsg.getTask().equals(ConstantsAndGlobalVars.getMemberPermissionsTask)) {
			try {
				clientMsg = dbController.getMemberPermissions(clientMsg);
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		if(clientMsg.getTask().equals(ConstantsAndGlobalVars.changeMemberStatusTask)) {
			clientMsg = dbController.changeMemberStatus(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(clientMsg.getTask().equals(ConstantsAndGlobalVars.changeBorrowPermissionTask)) {
			clientMsg = dbController.changeBorrowPermission(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(clientMsg.getTask().equals(ConstantsAndGlobalVars.changeReservePermissionTask)) {
			clientMsg = dbController.changeReservePermission(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumberOfAvailableCopies)) {
			clientMsg = dbController.getNumberOfAvailableCopies(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumberOfReservesTask)) {
			clientMsg = dbController.getNumberOfReserves(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(clientMsg.getTask().equals(ConstantsAndGlobalVars.addReserveTask)) {
			clientMsg = dbController.addReserve(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		///////////////////////////////////////////////////////////////////////////
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkIfReserveExistTask)) {
			clientMsg = dbController.checkReserveExistence(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		if(clientMsg.getTask().equals(ConstantsAndGlobalVars.getAllMembersTask)) {
			clientMsg = dbController.getAllMembers(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//////////////////////////////////////////////////////////////////////
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.updateReservestatusToDoneTask)) {
			clientMsg = dbController.updateReservestatusToDone(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.sendMessageForDelayTask)) {
			clientMsg = dbController.sendMessageForDelay(clientMsg);
      			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getAllEmployeesTask)) {
			clientMsg = dbController.getAllEmployees(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumOfAllMembersTask)) {
			clientMsg = dbController.getNumOfAllMembers(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumOfActiveMembersTask)) {
			clientMsg = dbController.getNumOfActiveMembers(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumOfFrozenMembersTask)) {
			clientMsg = dbController.getNumOfFrozenMembers(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumOfLockedMembersTask)) {
			clientMsg = dbController.getNumOfLockedMembers(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumOfBorrowedBooksTask)) {
			clientMsg = dbController.getNumOfBorrowedBooks(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getNumOfLateReturnMembersTask)) {
			clientMsg = dbController.getNumOfLateReturnMembers(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkIfReservedTask)) {
			clientMsg = dbController.checkIfReserved(clientMsg);
      try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.deleteMessageTask)) {
			clientMsg = dbController.deleteMessageTuple(clientMsg);
      try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.addFaultTask)) {
			clientMsg = dbController.addFault(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.checkReportExistenceTask)) {
			clientMsg = dbController.checkReportExistence(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.addReportToDBTask)) {
			clientMsg = dbController.addReportToDB(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getDataForBorrowedReportTask)) {
			clientMsg = dbController.getDataForBorrowedReport(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (clientMsg.getTask().equals(ConstantsAndGlobalVars.getDataForLateReturnedReportTask)) {
			clientMsg = dbController.getDataForLateReturnedReport(clientMsg);
			try {
				client.sendToClient(clientMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}
	/**
	 * starts the server (starts listening to clients) and connects to the SQL server.
	 * @param username - the SQL server username.
	 * @param password - the SQL server password.
	 * @param host - the SQL server host.
	 * @param dbName - the schema name
	 * @return true if connection succeeded, false otherwise.
	 */
	public static boolean openServerConnection(String username, String password, String host, String dbName) {
		// dbController = new DBController();
		int port = ConstantsAndGlobalVars.DEFAULT_PORT; // Set port to 5555
		sv = new Server(port);

		if (!dbController.connectToDB(username, password, host, dbName))
			return false;
		else {
			//--- run thread to check database
			Timer timer = new Timer();
			 timer.schedule(new checkDB(dbController), 0, 60000*60*24);//1 minutes.
		}
		try {
			sv.listen(); // Start listening for connections
			return true;
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
		return false;
	}

}
