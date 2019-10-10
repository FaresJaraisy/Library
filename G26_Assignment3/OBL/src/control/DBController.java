package control;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;
import java.util.Timer;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import entity.ActivityReport;
import entity.Book;
import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.FaultsHistory;
import entity.History;
import entity.Librarian;
import entity.Message;
import entity.ManualDelays;
import entity.MsgParser;
import entity.MyFile;
import entity.Permissions;
import entity.Report;
import entity.History;
import entity.Reservations;
import entity.StatData;
import entity.StatDataForLateReturnedReport;
import entity.TableQueries;
import entity.TwoDaysMessage;
import entity.User;
import enums.BookCopyStatus;
import enums.BorrowStatus;
import enums.LogInStatus;
import enums.MessageType;
import enums.ReserveStatus;
import enums.UpdateCopyResult;
import enums.UserStatus;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import sun.nio.ch.DatagramSocketAdaptor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import entity.BookCopies;
import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.History;
import entity.MsgParser;
import entity.History;
import entity.Reservations;
import entity.User;
import enums.Result;
import enums.ExistStatus;
import enums.FaultDesc;
import enums.LogInStatus;
import enums.UserPermissions;
import control.EmailController;
/**
 * The class responsible for connection and executing queries and updates to the schema.
 * all methods in this class are self documented.
 */
public class DBController {
	/**
	 * a Connection object to prepare and create statements.
	 */
	private static Connection conn;
	/**
	 * an arrayList that simulates the reservation queue.
	 */
	static ArrayList<TwoDaysMessage> timerList;
	/**
	 * connects to the SQL server.
	 * @see control.Server#openServerConnection(String, String, String, String).
	 * @return true if connection succeeded, false otherwise
	 */
	public boolean connectToDB(String username, String password, String host, String dbName) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			timerList = new ArrayList<>();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/*
		 * create new schema and create all the tables and populate it with some data..
		 */
		String url = "jdbc:mysql://" + host + "/" + dbName;
		// this block of code creates a new schema (database) and all the tables
		// and insert a new manager.
		// String url = "jdbc:mysql://" + host;
		Statement s;
		try {
			conn = DriverManager.getConnection(url, username, password);
			/*
			 * s = conn.createStatement(); if(s.executeUpdate("CREATE DATABASE "+dbName)==0)
			 * return false; s.close(); url = "jdbc:mysql://" + host + "/" + dbName; conn =
			 * DriverManager.getConnection(url, username, password); s =
			 * conn.createStatement(); for(int i=0;i<TableQueries.orderOfBuild.length;i++) {
			 * s.executeUpdate(TableQueries.orderOfBuild[i]); } String insertLibrarianQuery
			 * =
			 * "INSERT INTO users VALUES('208487289','Stacey','Abrams','0503487839','7289','123456',0,'Active','stacey.abrams@gmail.com',0)"
			 * ; if(s.executeUpdate(insertLibrarianQuery)==0) return false;
			 * insertLibrarianQuery =
			 * "INSERT INTO librarians VALUES('208487289','9827','Manager','Library')";
			 * if(s.executeUpdate(insertLibrarianQuery)==0) return false;
			 * insertLibrarianQuery = "INSERT INTO managers VALUES('208487289')";
			 * if(s.executeUpdate(insertLibrarianQuery)==0) return false;
			 */
			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			return false;
		}
		return true;
	}

	// gets the user if he/she is not already logged in.
	/**
	 * gets the user from the DB and checks the credentials sent, changes isLoggedIn status if all is valid.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser userLogin(MsgParser msg) {
		PreparedStatement stmt;
		User tmpUser = null;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		String password = ((User) msg.getCommPipe().get(0)).getPassword();
		String table = msg.getTableName();
		try {

			ResultSet rs;
			if (table.equals("users")) {
				String loginQuery = "SELECT * FROM users U WHERE U.userID = ?";
				stmt = conn.prepareStatement(loginQuery);
				stmt.setString(1, username);
				rs = stmt.executeQuery();
			} else if (table.equals("librarians")) {
				String loginQuery = "SELECT U.* " + "FROM users U,librarians L "
						+ "WHERE  L.librarianID = U.userID AND L.librarianID = ?";
				stmt = conn.prepareStatement(loginQuery);
				stmt.setString(1, username);
				rs = stmt.executeQuery();
			} else { // if(table.equals("managers"))
				String loginQuery = "SELECT U.* " + "FROM users U,managers M "
						+ "WHERE U.userID=M.managerID AND M.managerID = ?";
				stmt = conn.prepareStatement(loginQuery);
				stmt.setString(1, username);
				rs = stmt.executeQuery();
				// get the matching tuple, if there's any
			}
			msg.setReturnResult(LogInStatus.Success);
			if (rs.next()) {
				int bit = Integer.parseInt(rs.getString(10));
				boolean isLoggedIn = (bit == 1) ? true : false;
				// check if already logged in
				if (isLoggedIn) {
					msg.setReturnResult(LogInStatus.isLoggedIn);
					return msg;
				}
				// check if password matches
				if (!rs.getString(6).equals(password)) {
					msg.setReturnResult(LogInStatus.WrongPassword);
					return msg;
				}
				tmpUser = new User();
				tmpUser.setUserID(rs.getString(1));
				tmpUser.setFirstName(rs.getString(2));
				tmpUser.setLastName(rs.getString(3));
				tmpUser.setPhoneNumber(rs.getString(4));
				tmpUser.setMembershipNumber(rs.getString(5));
				tmpUser.setPassword(rs.getString(6));
				tmpUser.setStrikes(rs.getInt(7));
				tmpUser.setStatus(enums.UserStatus.valueOf(rs.getString(8)));
				tmpUser.setEmail(rs.getString(9));
				// System.out.println(tmpUser.getUserID());
				if (tmpUser.getStatus() != enums.UserStatus.Locked) {
					changeIsLoggedIn(username);
				}
				msg.clearCommPipe();
				msg.addToCommPipe(tmpUser);
			} else {
				msg.setReturnResult(LogInStatus.UserNotExist);
				return msg;
			}

		} catch (SQLException e) {
//			msg.setReturnResult(LogInStatus.UserNotExist);
//			return msg;
		}

		return msg;
	}

	// if the all conditions to a log in are met, change isLoggedIn flag to 1.
	/**
	 * changes isLoggedIn status for the user specified.
	 * @param username - the user ID
	 */
	private void changeIsLoggedIn(String username) {
		PreparedStatement stmt;
		try {
			String isLoggedinQuery = "UPDATE users SET isLoggedIn = 1 WHERE userID = ?";
			stmt = conn.prepareStatement(isLoggedinQuery);
			stmt.setString(1, username);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * gets a list of books matching a search pattern and a search keyword.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser searchForBook(MsgParser msg) {
		PreparedStatement stmt, stmt1;
		ResultSet rs;
		String type = (String) msg.getCommPipe().get(0);
		String keyword = (String) msg.getCommPipe().get(1);
		msg.clearCommPipe();
		if (type.equals(ConstantsAndGlobalVars.searchType[0])) {// search by Book Title
			String bookTitleSearchQuery = "SELECT B.catalogNumber " + "FROM books B " + "WHERE (B.Title LIKE ?)";
			try {
				stmt = conn.prepareStatement(bookTitleSearchQuery);
				stmt.setString(1, "%" + keyword + "%");
				rs = stmt.executeQuery();
				while (rs.next()) {
					String CatalogNumber = rs.getString(1);
					MsgParser resultBook = new MsgParser<>();
					resultBook.addToCommPipe(CatalogNumber);
					resultBook = this.getBook(resultBook);
					Book b = (Book) resultBook.getCommPipe().get(0);
					msg.addToCommPipe(b);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (type.equals(ConstantsAndGlobalVars.searchType[1])) {// search by Category
			String bookTitleSearchQuery = "SELECT B.catalogNumber " + "FROM books B, categories C "
					+ "WHERE (B.catalognumber = C.catalognumber AND C.categoryname LIKE ?)";
			try {
				stmt = conn.prepareStatement(bookTitleSearchQuery);
				stmt.setString(1, "%" + keyword + "%");
				rs = stmt.executeQuery();
				while (rs.next()) {
					String CatalogNumber = rs.getString(1);
					MsgParser resultBook = new MsgParser<>();
					resultBook.addToCommPipe(CatalogNumber);
					resultBook = this.getBook(resultBook);
					Book b = (Book) resultBook.getCommPipe().get(0);
					msg.addToCommPipe(b);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (type.equals(ConstantsAndGlobalVars.searchType[2])) {// search by Author Name
			String bookTitleSearchQuery = "SELECT B.catalogNumber " + "FROM books B " + "WHERE (B.AuthorName LIKE ?)";
			try {
				stmt = conn.prepareStatement(bookTitleSearchQuery);
				stmt.setString(1, "%" + keyword + "%");
				rs = stmt.executeQuery();
				while (rs.next()) {
					String CatalogNumber = rs.getString(1);
					MsgParser resultBook = new MsgParser<>();
					resultBook.addToCommPipe(CatalogNumber);
					resultBook = this.getBook(resultBook);
					Book b = (Book) resultBook.getCommPipe().get(0);
					msg.addToCommPipe(b);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return msg;

	}

	/**
	 * gets the messages for a librarian or a manager.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getMessages(MsgParser msg) {
		PreparedStatement stmt;
		String username = ((Message) msg.getCommPipe().get(0)).getBelong();
		ResultSet rs = null;
		String getMessagesQuery = "SELECT * FROM messages M WHERE M.belong = ?";
		msg.clearCommPipe();
		try {
			stmt = conn.prepareStatement(getMessagesQuery);
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Message msgToAdd = new Message(enums.MessageType.valueOf(rs.getString(1)), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getDate(5), rs.getString(6));
				msg.addToCommPipe(msgToAdd);

			}

		} catch (SQLException e) {
		}
		return msg;
	}

	/**
	 * updates the settings for the user specified
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser userSettingUpdate(MsgParser msg) {
		PreparedStatement stmt;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		String newPhoneNumber = ((User) msg.getCommPipe().get(0)).getPhoneNumber();
		String newMail = ((User) msg.getCommPipe().get(0)).getEmail();
		String updateSettingsQuery = "UPDATE users SET email =?, phoneNumber= ? WHERE userID = ?";
		msg.clearCommPipe();
		try {
			stmt = conn.prepareStatement(updateSettingsQuery);
			stmt.setString(1, newMail);
			stmt.setString(2, newPhoneNumber);
			stmt.setString(3, username);
			stmt.executeUpdate();
			msg.addToCommPipe("Success");
		} catch (SQLException e) {
			msg.addToCommPipe("Failed");
		}
		return msg;

	}

	/**
	 * updates the related tables upon returning a book.
	 * @param msg the parameters
	 * @return the return message
	 * @throws SQLException thrown if executing the update encounters a problem.
	 * @throws ParseException thrown if parsing the date encounters a problem
	 */
	public MsgParser<String> returnBookUpdate(MsgParser msg) throws SQLException, ParseException {
		MsgParser<String> msg1 = msg;
		PreparedStatement stmt, stmt2, stmt3, stmt5;
		// unused variables??????
		ArrayList<String> bookBarcodes = new ArrayList<>();
		ArrayList<Reservations> bookReservations = new ArrayList<>();
		Statement stmt4, stmt6;
		int strikes, lateReturnsBook = 0;
		BorrowStatus bS;
		ResultSet rs = null, rs1 = null;
		LocalDate returnDate, borrowDate;
		String userID, catalogNumber;
		String barcode = ((String) msg1.getCommPipe().get(0));
		LocalDate local = LocalDate.now();
		msg1.clearCommPipe();
		// ------check if copy exist
		try {
			PreparedStatement firstStmt;
			ResultSet firstRs;
			String checkCopyExistance = "SELECT * FROM bookcopies B WHERE B.barcode = ? ";
			firstStmt = conn.prepareStatement(checkCopyExistance);
			firstStmt.setString(1, barcode);
			// get the ID of the borrower if no one has borrowed the book return not
			// borrowed
			firstRs = firstStmt.executeQuery();
			if (!firstRs.next()) {
				msg1.addToCommPipe("Barcode not exist");
				return msg1;
			}
		} catch (SQLException e) {
			msg1.addToCommPipe("Barcode not exist");
			return msg1;
		}

		// -----get user id (the one who had borrowed the book).
		try {
			String getActiveBorrrowsQuery = "SELECT * " + "FROM borrows B "
					+ "WHERE (B.barcode = ? AND (B.status= 'Active' OR status= 'LateNotReturned'))";
			stmt = conn.prepareStatement(getActiveBorrrowsQuery);
			stmt.setString(1, barcode);
			// get the ID of the borrower if no one has borrowed the book return not
			// borrowed
			rs = stmt.executeQuery();

		} catch (SQLException e) {
			msg1.addToCommPipe("No one has borrowed the book");
			return msg1;
		}

		if (rs.next()) {
			userID = rs.getString(1);
			borrowDate = rs.getDate(4).toLocalDate();
			returnDate = rs.getDate(5).toLocalDate();
		} else {
			msg1.addToCommPipe("No one has borrowed the book");
			return msg1;
		}
		// now check the the status if LateReturned or on time.
		if (local.isAfter(returnDate)) {
			bS = BorrowStatus.LateReturned; // if the book not returned on time
		} else {
			bS = BorrowStatus.Returned;// if the book returned on time
		}
		try {
			// update the status on borrows and actual return date
			String returnCopyQuery = "UPDATE borrows SET actualReturnDate = NOW(), status= ? WHERE barcode = ? AND( status = ? OR status= 'LateNotReturned')";
			stmt2 = conn.prepareStatement(returnCopyQuery);
			// stmt2.setString(1, local.toString());
			stmt2.setString(1, bS.toString());
			stmt2.setString(2, barcode);
			stmt2.setString(3, UserStatus.Active.toString());
			stmt2.executeUpdate();
			// update the status on copy of book
			returnCopyQuery = "UPDATE bookcopies SET status= ? WHERE barcode = ?";
			stmt3 = conn.prepareStatement(returnCopyQuery);
			stmt3.setString(1, enums.BookCopyStatus.available.name());
			stmt3.setString(2, barcode);
			stmt3.executeUpdate();
		} catch (SQLException e) {
			msg1.addToCommPipe("return copy has not updated successfully!");
			return msg1;

		}

		// if the condition is true thats mean that the user already frozen
		// we need to check if he has returned all his books that the due date is
		// passed.

		if (bS == BorrowStatus.LateReturned) {
			// get number of copies that the user hasn't return
			stmt4 = conn.createStatement();
			try {
				// get number of books that not returned
				String numberOfUnreturnedBooksQuery = "SELECT COUNT(*) FROM borrows B WHERE B.userID = ? AND B.status = ?";
				PreparedStatement preparedStmt = conn.prepareStatement(numberOfUnreturnedBooksQuery);
				preparedStmt.setString(1, userID);
				preparedStmt.setString(2, "LateNotReturned");

				rs1 = preparedStmt.executeQuery();
				if (rs1.next()) {
					lateReturnsBook = rs1.getInt(1);
				} else {
					msg1.addToCommPipe("User has no late returns!");
					return msg1;
				}

			} catch (SQLException e) {
				msg1.addToCommPipe("User has no late returns!");
				return msg1;
			}
			if (lateReturnsBook == 0)// unfreeze his account
			{
				String unfreezeQuery = "UPDATE users SET status= ? WHERE userID = ?";
				stmt5 = conn.prepareStatement(unfreezeQuery);
				stmt5.setString(1, UserStatus.Active.name());
				stmt5.setString(2, userID);
				if (stmt5.executeUpdate() > 0)
					msg1.addToCommPipe("unFreeze success");
			}

		}

		// 1-check reservation table if anyone has reserve and send to him message for 2
		// days
		// 2-check reserve table if there is reserve set status to reserve in copy of
		// book

		// get barcode number
		ResultSet barcodeN;
		PreparedStatement stmt7;
		String getcatalognumber = "SELECT catalognumber FROM bookcopies Where barcode = ? ";
		stmt7 = conn.prepareStatement(getcatalognumber);
		stmt7.setString(1, barcode);
		barcodeN = stmt7.executeQuery();
		if (barcodeN.next()) {
			catalogNumber = barcodeN.getString(1);
		} else {
			msg1.addToCommPipe("Catalog not existed");
			return msg1;
		}

		PreparedStatement stmt8;
		String getAllBarcodes = "SELECT B.barcode FROM bookcopies B where B.catalogNumber= ? ";
		stmt8 = conn.prepareStatement(getAllBarcodes);
		stmt8.setString(1, catalogNumber);
		barcodeN = stmt8.executeQuery();
		while (barcodeN.next()) {
			bookBarcodes.add(barcodeN.getString(1));
		}

		PreparedStatement stmt9;
		ResultSet reserveN;
		String getAllBookReservations = "SELECT R.* FROM reservations R WHERE R.reserveStatus= 'Pending' AND R.barcode = ?";//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		stmt9 = conn.prepareStatement(getAllBookReservations);

		int flag = 0;
		// get all reservations reserved that copy..
		for (String barcodeI : bookBarcodes) {
			stmt9.setString(1, barcodeI);
			reserveN = stmt9.executeQuery();
			if (reserveN.next()) {
				bookReservations.add(new Reservations(reserveN.getString(1), reserveN.getString(2),
						reserveN.getTimestamp(3), ReserveStatus.valueOf(reserveN.getString(4))));
				flag++;
			}
		}

		if (flag > 0) // if there is reservations on that book get the earlier date.
		{
			Reservations earlierDateReserve = bookReservations.get(0);
			for (Reservations reservations : bookReservations) {
				LocalDateTime min = earlierDateReserve.gettS().toLocalDateTime();
				LocalDateTime next = reservations.gettS().toLocalDateTime();
				if (min.isAfter(next)) {
					earlierDateReserve = reservations;
				}

			}

			PreparedStatement stmt123;
			String TwoDayMsg = "UPDATE reservations SET reserveStatus= ? WHERE userID = ? AND barcode = ? AND reserveDate=? ";
			stmt123 = conn.prepareStatement(TwoDayMsg);
			stmt123.setString(1, ReserveStatus.twoDaysPending.name());
			stmt123.setString(2, earlierDateReserve.getUserID());
			stmt123.setString(3, earlierDateReserve.getBarcode());
			String s = earlierDateReserve.gettS().toString().split("\\.")[0];
			stmt123.setString(4, s);
			if (stmt123.executeUpdate() > 0) {
				System.out.println("update success");
			} else {
				System.out.println("update fails");
			}

			// update copybook table status to reserved 'reserved'
//			PreparedStatement stmt10;
//			String bookCopyReserved = "UPDATE bookcopies SET status= ? WHERE barcode = ? AND catalogNumber = ? ";
//			stmt10 = conn.prepareStatement(bookCopyReserved);
//			stmt10.setString(1, BookCopyStatus.reserved.name());
//			stmt10.setString(2, barcode);
//			stmt10.setString(3, catalogNumber);
//			if(stmt10.executeUpdate()>0)
//			{

			System.out.println("your book has arrived you have 2 days to borrow it ID: "
					+ earlierDateReserve.getUserID() + " barcode: " + barcode);
			// send mail or add alert---------------not
			// implemented-----<><><><><><><><><><>---------

			PreparedStatement stmtEmail;
			ResultSet mailRs = null;
			String getEmailQuery = "SELECT U.email FROM users U WHERE U.userID = ?";
			try {
				stmtEmail = conn.prepareStatement(getEmailQuery);
				stmtEmail.setString(1, earlierDateReserve.getUserID());
				mailRs = stmtEmail.executeQuery();
				if (mailRs.next()) {
					String toSendMail = mailRs.getString(1);
					EmailController emailController = EmailController.getInstance();
					emailController.sendVerficationMail(toSendMail, "book arrived",
							"your book has arrived you have 2 days to borrow it ID: " + earlierDateReserve.getUserID()
									+ " barcode: " + barcode);
				}

			} catch (SQLException e) {
			}
			TwoDaysMessage TDM = new TwoDaysMessage();
			Timer timer = new Timer();
			Date currentDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			c.add(Calendar.HOUR, 48);
			// c.add(Calendar.SECOND, 30);
			// convert calendar to date
			Date currentDatePlus48H = c.getTime();
			timer.schedule(new TwoDaysMessagecontroller(this, earlierDateReserve, catalogNumber, barcode),
					currentDatePlus48H);
			TDM.setTimer(timer);
			TDM.setReservation(earlierDateReserve);
			TDM.setRealBarcode(barcode);
			timerList.add(TDM);

			// }
		}
		flag = 0;

		msg1.addToCommPipe("book returned successfully!");
		return msg1;
	}

	// function that delete reservation and send to the next one in the list i there
	// any
	/**
	 * updates the status of a reservation to 'Canceled'.
	 * @param R the reservation to delete
	 */
	public void deleteReservation(Reservations R) {
		PreparedStatement stmt;
		String bookCopyReserved = "UPDATE reservations SET reserveStatus= ? WHERE userID = ? AND barcode = ? AND reserveDate = ?";

		try {
			stmt = conn.prepareStatement(bookCopyReserved);
			stmt.setString(1, ReserveStatus.Canceled.name());
			stmt.setString(2, R.getUserID());
			stmt.setString(3, R.getBarcode());
			String s = R.gettS().toString().split("\\.")[0];
			stmt.setString(4, s);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		// now send the copy to the next person

	}

	// check if there is another one want to reserve else change status of copy to
	// available
	public void sendMessageToNextOne(String catalogNumber, String barcode, String ID) {// --------------i'm working
																						// here----------------------------------------

		ArrayList<String> bookBarcodes = new ArrayList<>();
		ArrayList<Reservations> bookReservations = new ArrayList<>();
		// ----------------------------
		try {
			ResultSet barcodeN;
			PreparedStatement stmt11;
			String getAllBarcodes = "SELECT B.barcode FROM bookcopies B where B.catalogNumber= ? ";
			stmt11 = conn.prepareStatement(getAllBarcodes);
			stmt11.setString(1, catalogNumber);
			barcodeN = stmt11.executeQuery();
			while (barcodeN.next()) {
				bookBarcodes.add(barcodeN.getString(1));
			}
			if (bookBarcodes.isEmpty())
				return;

			PreparedStatement stmt12;
			ResultSet reserveN;
			String getAllBookReservations = "SELECT R.* FROM reservations R WHERE R.reserveStatus= 'Pending' AND R.barcode = ?";
			stmt12 = conn.prepareStatement(getAllBookReservations);

			int flag = 0;
			// get all reservations reserved that copy..
			for (String barcodeI : bookBarcodes) {
				stmt12.setString(1, barcodeI);
				reserveN = stmt12.executeQuery();
				if (reserveN.next()) {
					bookReservations.add(new Reservations(reserveN.getString(1), reserveN.getString(2),
							reserveN.getDate(3), ReserveStatus.valueOf(reserveN.getString(4))));
					flag++;
				}
			}

			if (flag > 0) // if there is reservations on that book get the earlier date.
			{
				Reservations earlierDateReserve = bookReservations.get(0);
				for (Reservations reservations : bookReservations) {
					LocalDate min = earlierDateReserve.getMysqlDate().toLocalDate();
					LocalDate next = reservations.getMysqlDate().toLocalDate();
					if (min.isAfter(next)) {
						earlierDateReserve = reservations;
					}

					PreparedStatement stmt123;
					String TwoDayMsg = "UPDATE reservations SET reserveStatus= ? WHERE userID = ? AND barcode = ? AND reserveDate=? ";
					stmt123 = conn.prepareStatement(TwoDayMsg);
					stmt123.setString(1, ReserveStatus.twoDaysPending.name());
					stmt123.setString(2, earlierDateReserve.getUserID());
					stmt123.setString(3, earlierDateReserve.getBarcode());
					stmt123.setDate(4, earlierDateReserve.getMysqlDate());
					stmt123.executeUpdate();

					System.out.println("your book has arrived you have 2 days to borrow it ID: "
							+ earlierDateReserve.getUserID() + " barcode: " + barcode);
					// send mail or add alert---------------not
					// implemented-----<><><><><><><><><><>---------
					PreparedStatement stmtEmail;
					ResultSet mailRs = null;
					String getEmailQuery = "SELECT U.email FROM users U WHERE U.userID = ?";
					try {
						stmtEmail = conn.prepareStatement(getEmailQuery);
						stmtEmail.setString(1, earlierDateReserve.getUserID());
						mailRs = stmtEmail.executeQuery();
						if (mailRs.next()) {
							String toSendMail = mailRs.getString(1);
							EmailController emailController = EmailController.getInstance();
							emailController.sendVerficationMail(toSendMail, "book arrived",
									"your book has arrived you have 2 days to borrow it ID: "
											+ earlierDateReserve.getUserID() + " barcode: " + barcode);
						}

					} catch (SQLException e) {
					}

					TwoDaysMessage TDM = new TwoDaysMessage();
					Timer timer = new Timer();
					Date currentDate = new Date();
					Calendar c = Calendar.getInstance();
					c.setTime(currentDate);
					c.add(Calendar.HOUR, 48);
					// c.add(Calendar.SECOND, 30);
					// convert calendar to date
					Date currentDatePlus48H = c.getTime();
					timer.schedule(new TwoDaysMessagecontroller(this, earlierDateReserve, catalogNumber, barcode),
							currentDatePlus48H);
					TDM.setTimer(timer);
					TDM.setReservation(earlierDateReserve);
					timerList.add(TDM);
					for (TwoDaysMessage twoDaysMessage : timerList) {
						if (twoDaysMessage.getReservation().getUserID().equals(ID)
								&& twoDaysMessage.getRealBarcode().equals(barcode)) {
							timerList.remove(twoDaysMessage);
						}
					}
				}
			} else {
				// delete the reservation from ArrayList<TwoDaysMessage>
				for (TwoDaysMessage twoDaysMessage : timerList) {
					if (twoDaysMessage.getReservation().getUserID().equals(ID)
							&& twoDaysMessage.getRealBarcode().equals(barcode)) {
						timerList.remove(twoDaysMessage);
					}
				}

			}
			flag = 0;

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * sends a return notice to all users who borrowed a book.
	 */
	public void checkDayBeforeReturnAndSendMessage() {
		PreparedStatement stmt;
		ResultSet rs;
		String getActiveBorrows = "select B.* from borrows B where B.status= 'Active' ";
		LocalDate currentDate1 = LocalDate.now();
		LocalDate borrowDate, borrowDateMinusOneDay;
		String ID, barcode;

		try {

			stmt = conn.prepareStatement(getActiveBorrows);
			rs = stmt.executeQuery();
			while (rs.next()) {
				borrowDate = rs.getDate(5).toLocalDate();
				borrowDateMinusOneDay = borrowDate.minusDays(1);
				if (borrowDateMinusOneDay.isEqual(currentDate1)) {
					ID = rs.getString(1);
					barcode = rs.getString(2);
					System.out
							.println("user id: " + ID + " you have one day to return book barcode number: " + barcode);
					PreparedStatement stmtEmail;
					ResultSet mailRs = null;
					String getEmailQuery = "SELECT U.email FROM users U WHERE U.userID = ?";
					try {
						stmtEmail = conn.prepareStatement(getEmailQuery);
						stmtEmail.setString(1, ID);
						mailRs = stmtEmail.executeQuery();
						if (mailRs.next()) {
							String toSendMail = mailRs.getString(1);
							EmailController emailController = EmailController.getInstance();
							emailController.sendVerficationMail(toSendMail, "Warning message!",
									"user id: " + ID + " you have one day to return book barcode number: " + barcode);
						}

					} catch (SQLException e) {
					}

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * checks if any user has late returns and freezes the account
	 * @throws ParseException thrown if parsing the date encounters a problem
	 */
	public void checkLateReturnAndFreeze() throws ParseException {
		Statement stmt;
		PreparedStatement stmt4;
		int numOfStrikes = 0;
		ArrayList<Borrows> lateBorrows = new ArrayList<>();
		ArrayList<String> managersID = new ArrayList<>();
		ResultSet rs = null, managersRs = null;
		String IDOfLateReturns = "select B.* from borrows B where B.status= 'Active' AND B.returnDate < NOW()";
		String getManagers = "select M.managerID from managers M";
		try {
			stmt4 = conn.prepareStatement(getManagers);
			managersRs = stmt4.executeQuery();
			// get all the managers to send them message in the end
			while (managersRs.next()) {
				managersID.add(managersRs.getString(1));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(IDOfLateReturns); // get the borrowers that are late
			while (rs.next()) {
				Timestamp tS = rs.getTimestamp(4);
				lateBorrows.add(new Borrows(rs.getString(1), rs.getString(2), rs.getString(3), tS,
						enums.BorrowStatus.valueOf(rs.getString(7))));

			}

			PreparedStatement stmt1;
			PreparedStatement stmt2;
			PreparedStatement stmt3;
			PreparedStatement stmt5;
			PreparedStatement stmt6;
			ResultSet rsStrikes = null;
			String freezeID = "UPDATE users SET status='Frozen' WHERE userID = ?";
			String getStrikes = "SELECT U.strikes FROM users U WHERE U.userID = ?";
			String addTupleQuery = "INSERT INTO messages VALUES (?,?,?,?,NOW(),?)";
			String setStrikes = "UPDATE users SET strikes=? WHERE userID = ?";
			String setLateReturns = "UPDATE borrows SET status= 'LateNotReturned' WHERE userID = ? AND barcode = ? AND borrowDate = ?";
			stmt1 = conn.prepareStatement(freezeID);
			stmt2 = conn.prepareStatement(getStrikes);
			stmt3 = conn.prepareStatement(addTupleQuery);
			stmt5 = conn.prepareStatement(setStrikes);
			stmt6 = conn.prepareStatement(setLateReturns);
			for (Borrows B : lateBorrows) {
				stmt1.setString(1, B.getUserID());
				stmt2.setString(1, B.getUserID());
				stmt1.executeUpdate(); // update user status to frozen
				stmt6.setString(1, B.getUserID());
				stmt6.setString(2, B.getBarcode());

				String s = B.gettS().toString().split("\\.")[0]; // remove nanoseconds
				stmt6.setString(3, s);

				if (stmt6.executeUpdate() > 0)// set status to lateNotReturn
				{
					System.out.println("update success");
					MsgParser<FaultsHistory> msg1 = new MsgParser();
					FaultsHistory fH = new FaultsHistory();
					fH.setUserID(B.getUserID());
					fH.setFaultDesc(FaultDesc.Frozen);
					msg1.addToCommPipe(fH);
					addFault(msg1);
				} else {

					System.out.println("update fails");
				}

				rsStrikes = stmt2.executeQuery();
				if (rsStrikes.next()) {
					numOfStrikes = rsStrikes.getInt(1);
					numOfStrikes++;
					stmt5.setInt(1, numOfStrikes);
					stmt5.setString(2, B.getUserID());
					stmt5.executeUpdate();
				}
				if (numOfStrikes == 3) {
					for (String managerID : managersID) {
						stmt3.setString(1, MessageType.lock.name());
						stmt3.setString(2, "Confirm reader card locked");
						stmt3.setString(3, "User " + B.getUserID() + " has 3 late returns, change status to locked?");
						stmt3.setString(4, managerID);
						stmt3.setString(5, B.getUserID());
						stmt3.executeUpdate();
					}

				}
			}
			lateBorrows.clear();
			managersID.clear();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * gets number of messages for a specific librarian/ manager.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumberMsg(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs = null;
		String username = (String) msg.getCommPipe().get(0);
		String numberOfMsg = "SELECT COUNT(*) FROM messages M WHERE M.belong = ?";
		msg.clearCommPipe();
		try {
			stmt = conn.prepareStatement(numberOfMsg);
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			if (rs.next())
				msg.addToCommPipe(Integer.toString(rs.getInt(1)));
			else
				msg.addToCommPipe("0");
		} catch (SQLException e) {
			msg.addToCommPipe("0");
		}
		return msg;
	}

	/**
	 * changes the isLoggedIn status of a user
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser userLogout(MsgParser msg) {
		PreparedStatement stmt;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		String logoutQuery = "UPDATE users SET isLoggedIn = 0 WHERE userID = ?";
		msg.clearCommPipe();
		try {
			stmt = conn.prepareStatement(logoutQuery);
			stmt.setString(1, username);
			stmt.executeUpdate();
			msg.addToCommPipe("Success");
		} catch (SQLException e) {
			msg.addToCommPipe("Failed");
		}
		return msg;
	}

	/**
	 * gets all active borrows for a user
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser userBorrow(MsgParser msg) {
		PreparedStatement stmt;
		Borrows tmpBorrows;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		String getActiveBorrowsQuery = "SELECT * " + "FROM borrows B "
				+ "WHERE B.userID = ? AND (B.status = 'Active' OR B.status = 'LateNotReturned')";
		try {
			stmt = conn.prepareStatement(getActiveBorrowsQuery);
			stmt.setString(1, username);
			System.out.println("alsaf alawal");
			ResultSet rs = stmt.executeQuery();
			System.out.println("alsaf alawal b");
			// get the matching tuple, if there's any
			msg.clearCommPipe();
			while (rs.next()) {
				System.out.println("alsaf altani");
				tmpBorrows = new Borrows();
				System.out.println(rs.getString(1));
				tmpBorrows.setUserID(rs.getString(1));
				System.out.println(rs.getString(2));
				tmpBorrows.setBarcode(rs.getString(2));
				System.out.println(rs.getString(3));
				tmpBorrows.setLibrarianID(rs.getString(3));
				System.out.println(rs.getDate(4));
				tmpBorrows.setBorrowDate(rs.getDate(4));
				System.out.println(rs.getDate(5));
				tmpBorrows.setReturnDate(rs.getDate(5));
				System.out.println(rs.getDate(6));
				tmpBorrows.setActualReturnDate(rs.getDate(6));
				System.out.println(rs.getString(7));
				tmpBorrows.setStatus(enums.BorrowStatus.valueOf(rs.getString(7)));
				msg.addToCommPipe(tmpBorrows);
			}

		} catch (SQLException e) {
			System.out.println("aklat 5ara");
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * gets all pending reservations for a user.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser userReservations(MsgParser msg) {
		PreparedStatement stmt;
		Reservations tmpReservations;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		String getPendingReservationsQuery = "SELECT * " + "FROM reservations R "
				+ "WHERE (R.userID = ? AND (R.reserveStatus = 'Pending' OR reserveStatus= 'twoDaysPending'))";
		try {
			stmt = conn.prepareStatement(getPendingReservationsQuery);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			// get the matching tuple, if there's any
			msg.clearCommPipe();
			while (rs.next()) {
				tmpReservations = new Reservations();
				tmpReservations.setUserID(rs.getString(1));
				tmpReservations.setBarcode(rs.getString(2));
				tmpReservations.setReserveDate(rs.getDate(3));
				tmpReservations.setStatus(enums.ReserveStatus.valueOf(rs.getString(4)));
				msg.addToCommPipe(tmpReservations);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * gets all inactive borrows and reservations for a user
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser userHistory(MsgParser msg) {
		PreparedStatement stmt;
		History tmpHistory;
		Reservations tmpReservations;
		Borrows tmpBorrows;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		try {
			String getHistoryQuery = "SELECT * " + "FROM borrows B "
					+ "WHERE B.userID = ? AND B.status != 'Active' AND B.status !='LateNotReturned'";
			stmt = conn.prepareStatement(getHistoryQuery);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			// get the matching tuples, if there's any
			msg.clearCommPipe();
			while (rs.next()) {
				tmpHistory = new History();
				tmpHistory.setUserID(rs.getString(1));
				tmpHistory.setBarcode(rs.getString(2));
				tmpHistory.setType(enums.EventType.Borrow);
				tmpHistory.setEventDate(rs.getDate(4));
				msg.addToCommPipe(tmpHistory);
			}
			getHistoryQuery = "SELECT * " + "FROM reservations R "
					+ "WHERE R.userID = ? AND R.reserveStatus != 'Pending' AND R.reserveStatus != 'twoDaysPending'";
			stmt = conn.prepareStatement(getHistoryQuery);
			stmt.setString(1, username);
			ResultSet rs1 = stmt.executeQuery();
			while (rs1.next()) {
				tmpHistory = new History();
				tmpHistory.setUserID(rs1.getString(1));
				tmpHistory.setBarcode(rs1.getString(2));
				tmpHistory.setType(enums.EventType.Reserve);
				tmpHistory.setEventDate(rs1.getDate(3));
				msg.addToCommPipe(tmpHistory);
			}

		} catch (SQLException e) {

		}
		return msg;
	}

	/**
	 * gets total number of books in the inventory.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser numberOfBooks(MsgParser mp) {
		Statement stmt;
		ResultSet rs;
		int totalNumberOfBooks = 0;
		mp.clearCommPipe();
		try {
			// no need for PreparedStatement, query isn't parameterised
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) FROM books B");
			if (rs.next())
				totalNumberOfBooks = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mp.addToCommPipe(totalNumberOfBooks);
		return mp;
	}

	/**
	 * adds a new user tuple to the users table
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser addNewUser(MsgParser mp) {
		PreparedStatement stmt;
		User newUser = (User) mp.getCommPipe().get(0);
		mp.clearCommPipe();
		ResultSet rs;
		try {
			String getUserQuery = "SELECT U.userID " + "FROM users U " + "WHERE U.userID = ?";
			stmt = conn.prepareStatement(getUserQuery);
			stmt.setString(1, newUser.getUserID());
			rs = stmt.executeQuery();
			if (rs.next()) {
				mp.addToCommPipe(false);
			} else {
				String addTupleQuery = "INSERT INTO users VALUES (?,?,?,?,?,?,0,'Active',?,0)";
				stmt = conn.prepareStatement(addTupleQuery);
				stmt.setString(1, newUser.getUserID());
				stmt.setString(2, newUser.getFirstName());
				stmt.setString(3, newUser.getLastName());
				stmt.setString(4, newUser.getPhoneNumber());
				stmt.setString(5, newUser.getMembershipNumber());
				stmt.setString(6, newUser.getPassword());
				stmt.setString(7, newUser.getEmail());
				if (stmt.executeUpdate() == 0) {
					mp.addToCommPipe(false);
					return mp;
				}
				String addPermissionQuery = "INSERT INTO permissions VALUES (?,'CanReserve')";
				stmt = conn.prepareStatement(addPermissionQuery);
				stmt.setString(1, newUser.getUserID());
				if (stmt.executeUpdate() == 0) {
					mp.addToCommPipe(false);
					return mp;
				}
				addPermissionQuery = "INSERT INTO permissions VALUES (?,'CanBorrow')";
				stmt = conn.prepareStatement(addPermissionQuery);
				stmt.setString(1, newUser.getUserID());
				if (stmt.executeUpdate() == 0) {
					mp.addToCommPipe(false);
					return mp;
				}
				mp.addToCommPipe(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			mp.addToCommPipe(false);
		}

		return mp;
	}

	/**
	 * adds a new book tuple to the books table.
	 * @param msg the parameters
	 * @return the return message
	 * @throws ParseException thrown if parsing the date encounters a problem
	 */
	public MsgParser addBook(MsgParser mp) throws ParseException {
		Book b = (Book) mp.getCommPipe().get(0);
		String insertBookQuery = "INSERT INTO books values (?,?,?,?,?,?,?,?,?,?)";
		mp.clearCommPipe();
		/*
		 * Convert Java Date to SQL Date
		 */
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date input = b.getPurchaseDate();
		String pd = df.format(input);
		Date temp = new SimpleDateFormat("yyyy-MM-dd").parse(pd);
		// java.sql.Date d = new java.sql.Date(temp.getDate());
		java.sql.Date d = new java.sql.Date(temp.getTime());
		// create an inputStream from the byteArray..
		InputStream is = new ByteArrayInputStream((b.getTableOfContents()).getMybytearray());
		try {
			PreparedStatement stmt = conn.prepareStatement(insertBookQuery);
			stmt.setString(1, b.getCatalogNumber());
			stmt.setString(2, b.getTitle());
			stmt.setString(3, b.getAuthorName());
			stmt.setString(4, b.getPublication());
			stmt.setInt(5, b.getNumberOfCopies());
			stmt.setDate(6, d);// convert to SQL Date
			stmt.setString(7, b.getLocationOnShelf());
			stmt.setBlob(8, is);
			stmt.setString(9, b.getDescription());
			stmt.setString(10, b.getType().name());
			if (stmt.executeUpdate() > 0) {
				stmt.close();
				String addCategoryQuery = "INSERT INTO categories VALUES(?,?)";
				ArrayList<String> categoriesArr = b.getCategories();
				for (String string : categoriesArr) {
					stmt = conn.prepareStatement(addCategoryQuery);
					stmt.setString(1, string);
					stmt.setString(2, b.getCatalogNumber());
					int res = stmt.executeUpdate();
					stmt.close();
					if (res == 0) {
						// catalogNumber is a PFK in categories set to ON-DELETE:CASCADE..
						String abortQuery = "DELETE FROM books WHERE (catalogNumber = ?)";
						stmt = conn.prepareStatement(abortQuery);
						stmt.setString(1, b.getCatalogNumber());
						stmt.executeUpdate();
						mp.addToCommPipe(false);
						return mp;
					}
				}
				mp.addToCommPipe(true);
			} else
				mp.addToCommPipe(false);
		} catch (SQLException e) {
			e.printStackTrace();
			mp.addToCommPipe(false);
		}
		return mp;
	}

	/**
	 * removes a book tuple from the books table.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser removeBook(MsgParser mp) {
		PreparedStatement stmt;
		String catalogNumber = (String) mp.getCommPipe().get(0);
		mp.clearCommPipe();
		try {
			String removeBookQuery = "DELETE FROM books WHERE (CatalogNumber = ?)";
			stmt = conn.prepareStatement(removeBookQuery);
			stmt.setString(1, catalogNumber);
			if (stmt.executeUpdate() > 0)
				mp.addToCommPipe(true);
			else
				mp.addToCommPipe(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mp.addToCommPipe(false);
		}
		return mp;
	}

	/**
	 * searched for a user and returns an object containing the user details.
	 * @param msg the parameters
	 * @return the return message
	 * @throws SQLException thrown should executing the query encounter a problem
	 */
	public MsgParser searchForUser(MsgParser mp) throws SQLException {
		String getUserQuery = "SELECT * FROM users U WHERE U.userID = ?";
		PreparedStatement prStmt = conn.prepareStatement(getUserQuery);
		prStmt.setString(1, ((User) mp.getCommPipe().get(0)).getUserID());

		ResultSet rs = prStmt.executeQuery();
		mp.clearCommPipe();
		User tmpUser = new User();

		if (rs.next()) {
			tmpUser.setUserID(rs.getString(1));
			tmpUser.setFirstName(rs.getString(2));
			tmpUser.setLastName(rs.getString(3));
			tmpUser.setPhoneNumber(rs.getString(4));
			tmpUser.setMembershipNumber(rs.getString(5));
			tmpUser.setPassword(rs.getString(6));
			tmpUser.setStrikes(rs.getInt(7));
			tmpUser.setStatus(enums.UserStatus.valueOf(rs.getString(8)));
			tmpUser.setEmail(rs.getString(9));
		} else {
			tmpUser = null;
		}
		mp.addToCommPipe(tmpUser);
		return mp;
	}

	
	public MsgParser checkNumOfCopy(MsgParser msg) {
		PreparedStatement stmt;
		int numBorrowCopy;
		String barcode = ((BookCopies) msg.getCommPipe().get(0)).getBarcode();
		try {
			String checkNumOfCopyQuery = "SELECT COUNT(*) " + "FROM borrows B " + "WHERE B.barcode = ?";
			stmt = conn.prepareStatement(checkNumOfCopyQuery);
			stmt.setString(1, barcode);
			ResultSet rs = stmt.executeQuery();
			// get the matching tuple, if there's any
			msg.clearCommPipe();
			if (rs.next()) {
				numBorrowCopy = rs.getInt(1);
				msg.setNumOfBorrowCopies(numBorrowCopy);

			} else {
				return msg;
			}

		} catch (SQLException e) {
//			msg.setReturnResult(LogInStatus.UserNotExist);
//			return msg;
		}
		return msg;

	}

	/**
	 * gets a user with all the permissions.<br></br>
	 * @see control.DBController#searchForUser(MsgParser)
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser checkUser(MsgParser msg) {
		PreparedStatement stmt;
		User tmpUser = null;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		try {
			String getUserQuery = "SELECT * " + "FROM users U " + "WHERE U.userID = ?";
			stmt = conn.prepareStatement(getUserQuery);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			// get the matching tuple, if there's any
			msg.clearCommPipe();
			if (rs.next()) {
				tmpUser = new User();
				tmpUser.setUserID(rs.getString(1));
				tmpUser.setFirstName(rs.getString(2));
				tmpUser.setLastName(rs.getString(3));
				tmpUser.setPhoneNumber(rs.getString(4));
				tmpUser.setMembershipNumber(rs.getString(5));
				tmpUser.setPassword(rs.getString(6));
				tmpUser.setStrikes(rs.getInt(7));
				tmpUser.setStatus(enums.UserStatus.valueOf(rs.getString(8)));
				tmpUser.setEmail(rs.getString(9));
				msg.addToCommPipe(tmpUser);
			} else {
				msg.addToCommPipe(tmpUser);
				msg.setIsExist(ExistStatus.NotExist);
				return msg;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			msg.addToCommPipe(tmpUser);
			return msg;
		}
		msg.setIsExist(ExistStatus.Exist);
		try {
			String canBorrowQuery = "SELECT * " + "FROM permissions P "
					+ "WHERE P.userID = ? AND P.permission = 'CanBorrow'";
			stmt = conn.prepareStatement(canBorrowQuery);
			stmt.setString(1, username);
			ResultSet rs1 = stmt.executeQuery();
			if (rs1.next()) {
				msg.setPer(enums.UserPermissions.valueOf(rs1.getString(2)));
			} else {

				return msg;
			}

		} catch (SQLException e) {
			e.printStackTrace();

		}

		return msg;

	}

	public MsgParser checkCopy(MsgParser msg) {
		PreparedStatement stmt;
		BookCopies tmpCopy = null;
		Book tmpBook = null;
		String barcode = ((BookCopies) msg.getCommPipe().get(0)).getBarcode();
		try {
			String checkCopyQuery = "SELECT * " + "FROM bookcopies C " + "WHERE C.barcode = ?";
			stmt = conn.prepareStatement(checkCopyQuery);
			stmt.setString(1, barcode);
			ResultSet rs = stmt.executeQuery();
			// get the matching tuple, if there's any
			msg.clearCommPipe();
			if (rs.next()) {
				tmpCopy = new BookCopies();
				tmpCopy.setBarcode(rs.getString(1));
				tmpCopy.setCatalogNumber(rs.getString(2));
				tmpCopy.setPurchaseDate(rs.getDate(3));
				tmpCopy.setStatus(enums.BookCopyStatus.valueOf(rs.getString(4)));
				msg.addToCommPipe(tmpCopy);

			} else {
				msg.addToCommPipe(tmpCopy);
				return msg;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			msg.addToCommPipe(tmpCopy);

		}
		return msg;

	}

	/**
	 * gets the book type and number of copies for a specific book.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser checkBookType(MsgParser msg) {
		String catalog = ((BookCopies) msg.getCommPipe().get(0)).getCatalogNumber();
		PreparedStatement stmt;
		BookCopies tmpCopy = null;
		Book tmpBook = null;
		enums.BookType type;
		int numOfCopies;
		try {
			String checkDateQuery = "SELECT B.numberOfCopies,B.type " + "FROM books B " + "WHERE B.CatalogNumber = ?";
			stmt = conn.prepareStatement(checkDateQuery);
			stmt.setString(1, catalog);
			ResultSet rs = stmt.executeQuery();
			msg.clearCommPipe();
			if (rs.next()) {
				numOfCopies = rs.getInt(1);
				type = enums.BookType.valueOf(rs.getString(2));
				msg.setNumOfCopies(numOfCopies);
				msg.setType(type);
			} else {
				System.out.println("noooo");
			}

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return msg;

	}

	/**
	 * inserts a new borrow tuple into the borrows table.
	 * @param msg the parameters
	 * @return the return message
	 * @throws ParseException thrown should parsing the date encounter problems.
	 */
	public MsgParser updateBorrowTable(MsgParser msg) throws ParseException {
		PreparedStatement stmt;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date borrowDate = ((Borrows) msg.getCommPipe().get(0)).getBorrowDate();
		Date returnDate = ((Borrows) msg.getCommPipe().get(0)).getReturnDate();
		Date actualReturnDate = ((Borrows) msg.getCommPipe().get(0)).getActualReturnDate();
		String retDate = df.format(returnDate);
		String currentDate = df.format(borrowDate);
		Date utilDate1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDate);
		Date utilDate2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(retDate);
		java.sql.Date sqlDate1 = new java.sql.Date(utilDate1.getTime());
		java.sql.Date sqlDate2 = new java.sql.Date(utilDate2.getTime());
		try {
			stmt = conn.prepareStatement("INSERT INTO borrows VALUES(?,?,?,NOW(),?,?,?)");
			stmt.setString(1, ((Borrows) msg.getCommPipe().get(0)).getUserID());
			stmt.setString(2, ((Borrows) msg.getCommPipe().get(0)).getBarcode());
			stmt.setString(3, ((Borrows) msg.getCommPipe().get(0)).getLibrarianID());
			// stmt.setDate(4, sqlDate1);
			stmt.setDate(4, sqlDate2);
			stmt.setDate(5, null);
			stmt.setString(6, enums.BorrowStatus.Active.name());
			stmt.executeUpdate();
			msg.clearCommPipe();
			msg.setBorrowresult(Result.Success);

		} catch (SQLException e) {
			msg.setBorrowresult(Result.Fail);
		}
		return msg;

	}

	/**
	 * updates an existing book tuple.
	 * @param msg the parameters
	 * @return the return message
	 * @throws ParseException thrown should parsing the date encounter problems.
	 */
	public MsgParser updateBook(MsgParser mp) throws ParseException {
		Book b = (Book) mp.getCommPipe().get(0);
		String updateBookQuery = "UPDATE books B SET B.Title = ?, B.AuthorName = ?, B.Publication = ?, B.numberOfCopies = ?, B.purchaseDate = ?, "
				+ "B.locationOnShelf = ?, B.tableOfContents = ?, B.Description = ?, B.type = ? WHERE (B.catalogNumber = ?)";
		mp.clearCommPipe();
		/*
		 * Convert Java Date to SQL Date
		 */
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date input = b.getPurchaseDate();
		String pd = df.format(input);
		Date temp = new SimpleDateFormat("yyyy-MM-dd").parse(pd);
		// java.sql.Date d = new java.sql.Date(temp.getDate());
		java.sql.Date purchaseDate = new java.sql.Date(temp.getTime());
		// just for checking if all is valid..
		int fileSize = (b.getTableOfContents()).getSize();
		System.out.printf("File of size %d received", fileSize);
		InputStream is = new ByteArrayInputStream((b.getTableOfContents()).getMybytearray());
		try {
			PreparedStatement stmt = conn.prepareStatement(updateBookQuery);
			stmt.setString(1, b.getTitle());
			stmt.setString(2, b.getAuthorName());
			stmt.setString(3, b.getPublication());
			stmt.setInt(4, b.getNumberOfCopies());
			stmt.setDate(5, purchaseDate);// convert to SQL Date
			stmt.setString(6, b.getLocationOnShelf());
			stmt.setBlob(7, is);
			stmt.setString(8, b.getDescription());
			stmt.setString(9, b.getType().name());
			stmt.setString(10, b.getCatalogNumber());
			stmt.executeUpdate();
			if (stmt.executeUpdate() > 0)
				mp.addToCommPipe(true);
			else
				mp.addToCommPipe(false);
		} catch (SQLException e) {
			e.printStackTrace();
			mp.addToCommPipe(false);
		}
		return mp;
	}

	/**
	 * searches for a book and returns it.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getBook(MsgParser mp) {
		PreparedStatement stmt;
		ResultSet rs;
		String catalogNumber = (String) mp.getCommPipe().get(0);
		System.out.println("bseder32323");
		String getBookQuery = "SELECT B.* FROM books B WHERE B.catalogNumber = ? ";
		String getCategoriesQuery = "SELECT C.categoryName FROM categories C WHERE C.catalogNumber = ? ";
		mp.clearCommPipe();
		Book b = null;
		try {
			stmt = conn.prepareStatement(getBookQuery);
			stmt.setString(1, catalogNumber);
			System.out.println(b);
			rs = stmt.executeQuery();
			System.out.println("rani");
			if (rs.next()) {
				b = new Book();
				System.out.println("la bseder");
				b.setCatalogNumber(rs.getString(1));
				b.setTitle(rs.getString(2));
				b.setAuthorName(rs.getString(3));
				b.setPublication(rs.getString(4));
				b.setNumberOfCopies(rs.getInt(5));
				java.sql.Date date = rs.getDate(6);
				Date purchaseDate = null;
				if (date != null)
					purchaseDate = new Date(date.getTime());
				b.setPurchaseDate(purchaseDate);
				b.setLocationOnShelf(rs.getString(7));
				/*
				 * get blob and convert to byteArray
				 */
				MyFile tableOfContents = new MyFile(b.getTitle() + " Table of contents.pdf");
				Blob blob = rs.getBlob(8);
				int blobLength = (int) blob.length();
				tableOfContents.initArray(blobLength);
				byte[] arr = blob.getBytes(1, blobLength);
				tableOfContents.setMybytearray(arr);
				tableOfContents.setSize(blobLength);
				blob.free();
				b.setTableOfContents(tableOfContents);
				b.setDescription(rs.getString(9));
				b.setType(enums.BookType.valueOf(rs.getString(10)));

				stmt = conn.prepareStatement(getCategoriesQuery);
				System.out.println("miao");
				stmt.setString(1, catalogNumber);
				rs = stmt.executeQuery();
				System.out.println("timo");
				ArrayList<String> categories = new ArrayList<>();
				while (rs.next()) {
					categories.add(rs.getString(1));
				}
				b.setCategories(categories);
				System.out.println(b);
				mp.addToCommPipe(b);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mp;
	}

	/**
	 * adds a new book copy tuple into bookcopies table.
	 * @param msg the parameters
	 * @return the return message
	 * @throws ParseException thrown should parsing the date encounter problems.
	 */
	public MsgParser addCopy(MsgParser mp) throws ParseException {
		PreparedStatement stmt;
		// get the argument from the MsgParser..
		BookCopies bc = (BookCopies) mp.getCommPipe().get(0);
		mp.clearCommPipe();
		// count total number of copies to numberOfcopies field
		try {
			// get total number copies of book
			String getTotalNumberOfCopies = "SELECT COUNT(*) " + "FROM bookcopies BC " + "WHERE BC.catalognumber = ?";
			stmt = conn.prepareStatement(getTotalNumberOfCopies);
			stmt.setString(1, bc.getCatalogNumber());
			ResultSet rs = stmt.executeQuery();
			int totalNumberOfCopies = 0;
			if (rs.next())
				totalNumberOfCopies = rs.getInt(1);
			// --------------------------------------------------------
			// get actual number of copies
			String getActualNumberOfCopies = "SELECT B.numberOfCopies " + "FROM books B " + "WHERE B.catalognumber = ?";
			stmt = conn.prepareStatement(getActualNumberOfCopies);
			stmt.setString(1, bc.getCatalogNumber());
			rs = stmt.executeQuery();
			int numberOfCopies = 0;
			if (rs.next())
				numberOfCopies = rs.getInt(1);
			// compare the the two numbers
			if (totalNumberOfCopies == numberOfCopies)// if they're equal don't add the copy.
				return mp;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return mp;
		}
		String addCopyQuery = "INSERT INTO bookcopies VALUES(?,?,?,?)";
		String catalogNumber = bc.getCatalogNumber();
		String barcode = bc.getBarcode();
		// get the date and convert it to SQL Date.
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date input = bc.getPurchaseDate();
		String pd = df.format(input);
		Date temp = new SimpleDateFormat("yyyy-MM-dd").parse(pd);
		// java.sql.Date d = new java.sql.Date(temp.getDate());
		java.sql.Date purchaseDate = new java.sql.Date(temp.getTime());
		enums.BookCopyStatus status = bc.getStatus();
		try {
			stmt = conn.prepareStatement(addCopyQuery);
			stmt.setString(1, barcode);
			stmt.setString(2, catalogNumber);
			stmt.setDate(3, purchaseDate);
			stmt.setString(4, status.name());
			if (stmt.executeUpdate() > 0)
				mp.addToCommPipe(true);
			else
				mp.addToCommPipe(false);
		} catch (SQLException e) {
			e.printStackTrace();
			mp.addToCommPipe(false);
		}
		return mp;
	}

	/**
	 * gets all faults for a specific user
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser userFaultsHistory(MsgParser msg) {
		PreparedStatement stmt;
		FaultsHistory tmpFaultsHistory = null;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		try {
			String getFaultsHistoryQuery = "SELECT * FROM faultshistory F WHERE F.userID = ? ";
			stmt = conn.prepareStatement(getFaultsHistoryQuery);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			// get the matching tuples, if there's any
			msg.clearCommPipe();
			while (rs.next()) {
				tmpFaultsHistory = new FaultsHistory();
				tmpFaultsHistory.setUserID(rs.getString(1));
				tmpFaultsHistory.setFaultDesc(enums.FaultDesc.valueOf(rs.getString(2)));
				tmpFaultsHistory.setFaultDate(rs.getDate(3));
				msg.addToCommPipe(tmpFaultsHistory);
			}

		} catch (SQLException e) {
			msg.addToCommPipe(tmpFaultsHistory);
		}
		return msg;
	}

	/**
	 * gets the employee role (librarian/manager).
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser checkEmployeeType(MsgParser msg) {
		// this function return true if the user is manager
		// or false if the user is librarian
		PreparedStatement stmt;
		String username = ((User) msg.getCommPipe().get(0)).getUserID();
		try {
			String checkUserTypeQuery = "SELECT M.managerID FROM managers M WHERE M.managerID = ? ";
			stmt = conn.prepareStatement(checkUserTypeQuery);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			// get the matching tuples, if there's any
			msg.clearCommPipe();
			if (rs.next())
				msg.addToCommPipe(true);
			else
				msg.addToCommPipe(false);
		} catch (SQLException e) {
			msg.addToCommPipe(false);
		}
		return msg;
	}

	/**
	 * sets the status of the book copy to 'borrowed'
	 * @param msg the parameters
	 * @return the return message
	 * @throws ParseException thrown should parsing the date encounter problems.
	 */
	public MsgParser updatebookcopytable(MsgParser msg) throws ParseException {
		PreparedStatement stmt;

		try {
			stmt = conn.prepareStatement("UPDATE bookcopies SET status = 'borrowed' WHERE barcode = ?");
			stmt.setString(1, ((BookCopies) msg.getCommPipe().get(0)).getBarcode());
			stmt.executeUpdate();
			msg.clearCommPipe();
			msg.setUpdatecopyresult(UpdateCopyResult.Success);

		} catch (SQLException e) {

			msg.setUpdatecopyresult(UpdateCopyResult.Fail);
		}
		return msg;

	}

	/**
	 * checks if the specified borrow is active.
	 * @param msg the parameters
	 * @return the return message
	 * @throws ParseException thrown should parsing the date encounter problems.
	 */
	public MsgParser checkborrow(MsgParser msg) throws ParseException {
		PreparedStatement stmt;
		try {
			String getActiveBorrrowsQuery = "SELECT * " + "FROM borrows B "
					+ "WHERE (B.userID = ? AND B.barcode = ? AND (B.status= 'Active' OR B.status = 'LateNotReturned'))";
			stmt = conn.prepareStatement(getActiveBorrrowsQuery);
			stmt.setString(1, ((Borrows) msg.getCommPipe().get(0)).getUserID());
			stmt.setString(2, ((Borrows) msg.getCommPipe().get(0)).getBarcode());
			// stmt.setString(3, ((Borrows) msg.getCommPipe().get(0)).getLibrarianID());
			ResultSet rs = stmt.executeQuery();
			msg.clearCommPipe();
			if (rs.next()) {

				msg.setBorrowDate(rs.getTimestamp(4));
				msg.setReturnDate(rs.getDate(5));
				msg.setIsExist(enums.ExistStatus.Exist);
			}

			else {
				msg.setIsExist(enums.ExistStatus.NotExist);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return msg;

	}

	/**
	 * updates the return date of a borrow
	 * @param msg the parameters
	 * @return the return message
	 * @throws ParseException thrown should parsing the date encounter problems.
	 */
	public MsgParser UpdateBorrowTableAfterDelaying(MsgParser msg) throws ParseException {
		PreparedStatement stmt;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date returnDate = ((Borrows) msg.getCommPipe().get(0)).getReturnDate();
		String retDate = df.format(returnDate);
		Date utilDate2 = new SimpleDateFormat("yyyy-MM-dd").parse(retDate);
		java.sql.Date sqlDate2 = new java.sql.Date(utilDate2.getTime());
		try {

			stmt = conn.prepareStatement(
					"UPDATE borrows SET returnDate = ? WHERE (userID = ? AND barcode = ? AND librarianID = ? AND status= 'Active')");
			stmt.setDate(1, sqlDate2);
			stmt.setString(2, ((Borrows) msg.getCommPipe().get(0)).getUserID());
			stmt.setString(3, ((Borrows) msg.getCommPipe().get(0)).getBarcode());
			stmt.setString(4, ((Borrows) msg.getCommPipe().get(0)).getLibrarianID());
			stmt.executeUpdate();
			msg.clearCommPipe();
			msg.setBorrowresult(Result.Success);

		} catch (SQLException e) {

			msg.setBorrowresult(Result.Fail);
		}
		return msg;

	}

	/**
	 * inserts a new manual delays tuple.
	 * @param msg the parameters
	 * @return the return message
	 * @throws ParseException thrown should parsing the date encounter problems.
	 */
	public MsgParser UpdateDelayTableTask(MsgParser msg) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		PreparedStatement stmt, stmt2;
		String getSpecificDelayQuery = "SELECT * " + "FROM manualdelays M "
				+ "WHERE (M.LibrarianID = ? AND M.date = ? AND M.userID = ? AND M.barcode= ? AND M.borrowdate=?)";

		Date Date = ((ManualDelays) msg.getCommPipe().get(0)).getDate();
		Timestamp borrowDate = ((ManualDelays) msg.getCommPipe().get(0)).getBorrowDate();
		String retDate = df.format(Date);
		String borrDate = df.format(borrowDate);
		Date utilDate1 = new SimpleDateFormat("yyyy-MM-dd").parse(borrDate);
		Date utilDate2 = new SimpleDateFormat("yyyy-MM-dd").parse(retDate);
		java.sql.Date sqlDate2 = new java.sql.Date(utilDate2.getTime());
		java.sql.Date sqlDate1 = new java.sql.Date(utilDate1.getTime());
		String s = borrowDate.toString().split("\\.")[0];
		try {

			System.out.println(s);
			System.out.println(((ManualDelays) msg.getCommPipe().get(0)).getBarcode());
			System.out.println(((ManualDelays) msg.getCommPipe().get(0)).getUserID());
			stmt2 = conn.prepareStatement(getSpecificDelayQuery);
			stmt2.setString(1, ((ManualDelays) msg.getCommPipe().get(0)).getLibraraianID());
			stmt2.setDate(2, sqlDate2);
			stmt2.setString(3, ((ManualDelays) msg.getCommPipe().get(0)).getUserID());
			stmt2.setString(4, ((ManualDelays) msg.getCommPipe().get(0)).getBarcode());
			stmt2.setString(5, s);
			ResultSet rs = stmt2.executeQuery();
			if (rs.next()) {
				msg.setDelayResult(Result.Occured);
				return msg;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {

			stmt = conn.prepareStatement("INSERT INTO manualdelays VALUES(?,?,?,?,?)");

			stmt.setString(1, ((ManualDelays) msg.getCommPipe().get(0)).getLibraraianID());
			stmt.setDate(2, sqlDate2);
			stmt.setString(3, ((ManualDelays) msg.getCommPipe().get(0)).getUserID());
			stmt.setString(4, ((ManualDelays) msg.getCommPipe().get(0)).getBarcode());
			stmt.setString(5, s);
			stmt.executeUpdate();
			msg.clearCommPipe();
			msg.setDelayResult(Result.Success);

		} catch (SQLException e) {
			e.printStackTrace();
			msg.setDelayResult(Result.Fail);
		}
		return msg;
	}

	/**
	 * gets the permissions of a member.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getMemberPermissions(MsgParser msg) {
		PreparedStatement stmt;
		String userID = ((Permissions) msg.getCommPipe().get(0)).getUserID();
		try {
			String getMemberPermissionsQuery = "SELECT * FROM permissions P WHERE P.userID = ? ";
			stmt = conn.prepareStatement(getMemberPermissionsQuery);
			stmt.setString(1, userID);
			ResultSet rs = stmt.executeQuery();
			// get the matching tuples, if there's any
			// msg.clearCommPipe();
			while (rs.next()) {
				if (enums.UserPermissions.valueOf(rs.getString(2)) == enums.UserPermissions.CanBorrow)
					((Permissions) msg.getCommPipe().get(0)).setCanBorrow(true);
				if (enums.UserPermissions.valueOf(rs.getString(2)) == enums.UserPermissions.CanReserve)
					((Permissions) msg.getCommPipe().get(0)).setCanReserve(true);
			}
		} catch (SQLException e) {
			msg.addToCommPipe(false);
		}
		return msg;
	}

	/**
	 * checks the status of a member
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser changeMemberStatus(MsgParser msg) {
		PreparedStatement stmt;
		String userID = ((User) msg.getCommPipe().get(0)).getUserID();
		String newStatus = ((User) msg.getCommPipe().get(0)).getStatus().toString();
		try {
			String changeMemberStatusQuery = "UPDATE users SET status= ? WHERE userID = ?";
			stmt = conn.prepareStatement(changeMemberStatusQuery);
			stmt.setString(2, userID);
			stmt.setString(1, newStatus);
			int rs = stmt.executeUpdate();
			// get the matching tuples, if there's any
			msg.clearCommPipe();
			if (rs > 0)
				msg.addToCommPipe(true);
			else
				msg.addToCommPipe(false);
		} catch (SQLException e) {
			msg.addToCommPipe(false);
		}
		return msg;
	}

	/**
	 * changes the status of the borrow permission (true to false and vice versa).
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser changeBorrowPermission(MsgParser msg) {
		PreparedStatement stmt;
		String userID = ((Permissions) msg.getCommPipe().get(0)).getUserID();
		boolean canBorrow = ((Permissions) msg.getCommPipe().get(0)).isCanBorrow();
		msg.clearCommPipe();
		try {
			if (canBorrow) {
				String addQuery = "INSERT INTO permissions VALUES (?,'CanBorrow')";
				stmt = conn.prepareStatement(addQuery);
				stmt.setString(1, userID);
				int rs = stmt.executeUpdate();
				if (rs == 0)
					msg.addToCommPipe(false);
				else
					msg.addToCommPipe(true);
			} else {
				String deleteQuery = "DELETE FROM permissions WHERE userID = ? and permission = 'CanBorrow'";
				stmt = conn.prepareStatement(deleteQuery);
				stmt.setString(1, userID);
				int rs = stmt.executeUpdate();
				if (rs == 0)
					msg.addToCommPipe(false);
				else
					msg.addToCommPipe(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			msg.addToCommPipe(false);
		}
		return msg;
	}

	/**
	 * gets all existing categories in the DB.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getAllCategories(MsgParser msg) {
		Statement stmt;
		ResultSet rs;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT DISTINCT C.categoryName FROM categories C");
			msg.clearCommPipe();
			while (rs.next()) {
				msg.addToCommPipe(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * changes the status of the reserve permission (true to false and vice versa).
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser changeReservePermission(MsgParser msg) {
		PreparedStatement stmt;
		String userID = ((Permissions) msg.getCommPipe().get(0)).getUserID();
		boolean canReserve = ((Permissions) msg.getCommPipe().get(0)).isCanReserve();
		msg.clearCommPipe();
		try {
			if (canReserve) {
				String addQuery = "INSERT INTO permissions VALUES (?,'CanReserve')";
				stmt = conn.prepareStatement(addQuery);
				stmt.setString(1, userID);
				int rs = stmt.executeUpdate();
				if (rs == 0)
					msg.addToCommPipe(false);
				else
					msg.addToCommPipe(true);
			} else {
				String deleteQuery = "DELETE FROM permissions WHERE userID = ? and permission = 'CanReserve'";
				stmt = conn.prepareStatement(deleteQuery);
				stmt.setString(1, userID);
				int rs = stmt.executeUpdate();
				if (rs == 0)
					msg.addToCommPipe(false);
				else
					msg.addToCommPipe(true);
			}
		} catch (SQLException e) {
			msg.addToCommPipe(false);
		}
		return msg;
	}

	/**
	 * gets number of available book copies.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumberOfAvailableCopies(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		String catalogNumber = (String) msg.getCommPipe().get(0);
		msg.clearCommPipe();
		int availableCopies = 0;
		String availableCopiesQuery = "SELECT COUNT(*) " + "FROM bookcopies BC "
				+ "WHERE BC.catalognumber = ? AND BC.status = 'available'";
		try {
			stmt = conn.prepareStatement(availableCopiesQuery);
			stmt.setString(1, catalogNumber);
			rs = stmt.executeQuery();
			if (rs.next()) {
				availableCopies = rs.getInt(1);
				msg.addToCommPipe(availableCopies);
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return msg;
	}

	/**
	 * gets number of pending reservations for a book.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumberOfReserves(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		String catalogNumber = (String) msg.getCommPipe().get(0);
		msg.clearCommPipe();
		int reserves = 0;
		String getReservesQuery = "SELECT COUNT(*) " + "FROM reservations R, bookcopies BC "
				+ "WHERE BC.catalognumber = ? AND BC.barcode = R.barcode AND (R.reservestatus = 'Pending' OR R.reservestatus = 'twoDaysPending')";
		try {
			stmt = conn.prepareStatement(getReservesQuery);
			stmt.setString(1, catalogNumber);
			rs = stmt.executeQuery();
			if (rs.next()) {
				reserves = rs.getInt(1);
				msg.addToCommPipe(reserves);
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return msg;
	}

	/**
	 * adds a reservation tuple to the reservations table.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser addReserve(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		String userID = (String) msg.getCommPipe().get(0);
		String catalogNumber = (String) msg.getCommPipe().get(1);
		String barcode = "";
		msg.clearCommPipe();
		String getBarcodeQuery = "(SELECT BC.barcode " + "FROM bookcopies BC, borrows B "
				+ "WHERE BC.catalognumber = ? AND BC.barcode = B.barcode AND ( B.status = 'Active' OR B.status = 'LateNotReturned') "
				+ "AND BC.barcode NOT IN (SELECT R.barcode " + "FROM reservations R,bookcopies BC1 "
				+ "WHERE R.reservestatus = 'Pending' AND BC1.catalogNumber = BC.catalogNumber\n"
				+ "AND R.barcode = BC1.barcode)) ORDER BY B.ReturnDate ASC";
		try {
			stmt = conn.prepareStatement(getBarcodeQuery);
			stmt.setString(1, catalogNumber);
			rs = stmt.executeQuery();
			// there will always be a barCode to reserve, the situation in which the user
			// cannot reserve has already been handled
			if (rs.next())
				barcode = rs.getString(1);
			stmt.close();
			String checkIfCanReserve = "SELECT R.userID,R.barcode " + "FROM reservations R, bookcopies BC "
					+ "WHERE (R.userID = ? AND (BC.catalogNumber = ? AND R.barcode = BC.barcode)) AND ((R.userID,R.barcode) IN (SELECT R1.userID,R1.barcode "
					+ "FROM reservations R1 "
					+ "WHERE (R1.reservestatus = 'Pending' OR R1.reservestatus = 'twoDaysPending')))";

			stmt = conn.prepareStatement(checkIfCanReserve);
			stmt.setString(1, userID);
			stmt.setString(2, catalogNumber);
			rs = stmt.executeQuery();
			// stmt.close();
			if (rs.next()) {
				msg.addToCommPipe(1);// user already reserved the book
			} else {
				MsgParser<Permissions> mp = new MsgParser<>();
				Permissions p = new Permissions(userID, false, false);
				mp.addToCommPipe(p);
				mp = this.getMemberPermissions(mp);
				// check if user has CanReserve permission..
				if (((Permissions) mp.getCommPipe().get(0)).isCanReserve()) {
					String addReserveQuery = "INSERT INTO reservations VALUES(?,?,NOW(),'Pending')";
					stmt = conn.prepareStatement(addReserveQuery);
					stmt.setString(1, userID);
					stmt.setString(2, barcode);
					if (stmt.executeUpdate() > 0) {
						msg.addToCommPipe(0);
					} else {
						msg.addToCommPipe(2);// couldn't insert the tuple into the table
					}
				} else {
					msg.addToCommPipe(3);// user doesn't have reserve permission
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			msg.addToCommPipe(-1);
		}

		return msg;
	}

	/**
	 * checks if a reservation exists.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser checkReserveExistence(MsgParser msg) {
		// PreparedStatement stmt;
		System.out.println("100");
		String userID = ((Reservations) msg.getCommPipe().get(0)).getUserID();
		String barcode = ((Reservations) msg.getCommPipe().get(0)).getBarcode();
		msg.clearCommPipe();
		for (TwoDaysMessage td : timerList) {
			System.out.println("400");
			if (barcode.equals(td.getRealBarcode())) {
				System.out.println("500");
				if (userID.equals(td.getReservation().getUserID())) {
					System.out.println("101");
					msg.addToCommPipe(1);
					return msg;
				} else {
					msg.addToCommPipe(-1);
					return msg;
				}
			}
		}
		msg.addToCommPipe(0);
//		try {
//			
//				String checkReserve = "SELECT * " + "FROM reservations R "
//						+" WHERE R.barcode = ? AND R.reserveStatus = 'Pending'"+ "ORDER BY reserveDate";
//				stmt = conn.prepareStatement(checkReserve);
//				stmt.setString(1, barcode);
//				ResultSet rs = stmt.executeQuery();
//			if(rs.next()) {
//				if(userID.equals(rs.getString(1))) {
//					msg.addToCommPipe(true);
//					return msg;
//				}
//				else {
//					msg.addToCommPipe(false);
//					return msg;
//				}
//			}
//			
//		} catch (SQLException e) {
//			msg.addToCommPipe(false);
//		}
//		msg.addToCommPipe(false);
		return msg;
	}

	/**
	 * updates the reservation status to 'Closed'.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser updateReservestatusToDone(MsgParser msg) {
		System.out.println("201");
		PreparedStatement stmt;
		String userID = ((Reservations) msg.getCommPipe().get(0)).getUserID();
		String realbarcode = ((Reservations) msg.getCommPipe().get(0)).getBarcode();
		String barcode;
		int ind;
		for (TwoDaysMessage td : timerList) {
			if (realbarcode.equals(td.getRealBarcode())) {
				if (userID.equals(td.getReservation().getUserID())) {
					System.out.println("202");
					barcode = td.getReservation().getBarcode();
					try {
						String changeReserveStatusQuery = "UPDATE reservations SET reserveStatus= 'Closed' WHERE userID = ? AND barcode= ? AND (reserveStatus= 'Pending' OR reserveStatus= 'twoDaysPending')";
						stmt = conn.prepareStatement(changeReserveStatusQuery);
						stmt.setString(1, userID);
						stmt.setString(2, barcode);
						stmt.executeUpdate();
						msg.clearCommPipe();
						td.getTimer().cancel();
						timerList.remove(td);
						msg.setUpdateReservationsResult(Result.Success);

					} catch (SQLException e) {
						msg.setUpdateReservationsResult(Result.Fail);
					}
					return msg;
				}
			}
		}
		return msg;
	}

	/**
	 * gets all student users from the DB.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getAllMembers(MsgParser msg) {
		Statement stmt;
		ResultSet rs;
		User tmpUser = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"SELECT U.* FROM users U WHERE NOT EXISTS ( SELECT L.librarianID FROM librarians L WHERE L.librarianID = U.userID )");
			while (rs.next()) {
				tmpUser = new User();
				tmpUser.setUserID(rs.getString(1));
				tmpUser.setFirstName(rs.getString(2));
				tmpUser.setLastName(rs.getString(3));
				tmpUser.setPhoneNumber(rs.getString(4));
				tmpUser.setMembershipNumber(rs.getString(5));
				tmpUser.setPassword(rs.getString(6));
				tmpUser.setStrikes(rs.getInt(7));
				tmpUser.setStatus(enums.UserStatus.valueOf(rs.getString(8)));
				tmpUser.setEmail(rs.getString(9));
				msg.addToCommPipe(tmpUser);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * sends a message to the librarian notifying about a delay.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser sendMessageForDelay(MsgParser msg) {
		PreparedStatement stmt;
		Message message = (Message) msg.getCommPipe().get(0);
		try {
			String sendMessage = "INSERT INTO messages VALUES (?,?,?,?,NOW(),?)";
			stmt = conn.prepareStatement(sendMessage);
			stmt.setString(1, message.getMessageType().name());
			stmt.setString(2, message.getTitle());
			stmt.setString(3, message.getMsg());
			stmt.setString(4, message.getBelong());
			stmt.setString(5, message.getUser());
			stmt.executeUpdate();
			msg.clearCommPipe();
			msg.setDelayResult(Result.Success);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.setDelayResult(Result.Fail);
		}
		return msg;
	}

	/**
	 * gets all employees
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getAllEmployees(MsgParser msg) {
		Statement stmt;
		ResultSet rs;
		Librarian tmpLibrarian = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM users U, librarians L WHERE U.userID = L.librarianID");
			while (rs.next()) {
				tmpLibrarian = new Librarian();
				tmpLibrarian.setUserID(rs.getString(1));
				tmpLibrarian.setFirstName(rs.getString(2));
				tmpLibrarian.setLastName(rs.getString(3));
				tmpLibrarian.setPhoneNumber(rs.getString(4));
				tmpLibrarian.setEmployeeNumber(rs.getString(12));
				tmpLibrarian.setPassword(rs.getString(6));
				tmpLibrarian.setDepartmentName(rs.getString(14));
				tmpLibrarian.setRole(rs.getString(13));
				tmpLibrarian.setEmail(rs.getString(9));
				msg.addToCommPipe(tmpLibrarian);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * gets data for the the activity report
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumOfAllMembers(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		ActivityReport ar = (ActivityReport) msg.getCommPipe().get(0);
		try {
			String str = "SELECT COUNT(DISTINCT F.userID) FROM faultshistory F WHERE (F.Date >= ? AND F.Date < ?)";
			stmt = conn.prepareStatement(str);
			stmt.setDate(1, ar.getFromDate());
			stmt.setDate(2, ar.getToDate());
			rs = stmt.executeQuery();
			if (rs.next())
				msg.setIntResult(rs.getInt(1));
			else
				msg.setIntResult(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * gets data for the the activity report
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumOfActiveMembers(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		ActivityReport ar = (ActivityReport) msg.getCommPipe().get(0);
		try {
			String str = "SELECT COUNT(DISTINCT F.userID) FROM faultshistory F WHERE (F.faultDesc= 'Active' AND F.Date >= ? AND F.Date < ?)";
			stmt = conn.prepareStatement(str);
			stmt.setDate(1, ar.getFromDate());
			stmt.setDate(2, ar.getToDate());
			rs = stmt.executeQuery();
			if (rs.next())
				msg.setIntResult(rs.getInt(1));
			else
				msg.setIntResult(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * gets data for the the activity report
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumOfFrozenMembers(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		ActivityReport ar = (ActivityReport) msg.getCommPipe().get(0);
		try {
			String str = "SELECT COUNT(DISTINCT F.userID) FROM faultshistory F WHERE (F.faultDesc= 'Frozen' AND F.Date >= ? AND F.Date < ?)";
			stmt = conn.prepareStatement(str);
			stmt.setDate(1, ar.getFromDate());
			stmt.setDate(2, ar.getToDate());
			rs = stmt.executeQuery();
			if (rs.next())
				msg.setIntResult(rs.getInt(1));
			else
				msg.setIntResult(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * gets data for the the activity report
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumOfLockedMembers(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		ActivityReport ar = (ActivityReport) msg.getCommPipe().get(0);
		try {
			String str = "SELECT COUNT(DISTINCT F.userID) FROM faultshistory F WHERE (F.faultDesc= 'Locked' AND F.Date >= ? AND F.Date < ?)";
			stmt = conn.prepareStatement(str);
			stmt.setDate(1, ar.getFromDate());
			stmt.setDate(2, ar.getToDate());
			rs = stmt.executeQuery();
			if (rs.next())
				msg.setIntResult(rs.getInt(1));
			else
				msg.setIntResult(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * gets data for the the activity report
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumOfBorrowedBooks(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		ActivityReport ar = (ActivityReport) msg.getCommPipe().get(0);
		try {
			String str = "SELECT COUNT(*) FROM borrows B WHERE ((B.status='Active' OR B.status='LateNotReturned') AND B.borrowDate >= ? AND B.borrowDate < ?)";
			stmt = conn.prepareStatement(str);
			stmt.setDate(1, ar.getFromDate());
			stmt.setDate(2, ar.getToDate());
			rs = stmt.executeQuery();
			if (rs.next())
				msg.setIntResult(rs.getInt(1));
			else
				msg.setIntResult(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
	/**
	 * gets data for the the activity report
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getNumOfLateReturnMembers(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		ActivityReport ar = (ActivityReport) msg.getCommPipe().get(0);
		try {
			String str = "SELECT COUNT(*) FROM borrows B WHERE ((B.status='LateNotReturned' OR B.status = 'LateReturned') AND B.returnDate >= ? AND B.returnDate < ?)";
			stmt = conn.prepareStatement(str);
			stmt.setDate(1, ar.getFromDate());
			stmt.setDate(2, ar.getToDate());
			rs = stmt.executeQuery();
			if (rs.next())
				msg.setIntResult(rs.getInt(1));
			else
				msg.setIntResult(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
	/**
	 * check if the user already reserved the book.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser checkIfReserved(MsgParser msg) {
		PreparedStatement stmt;
		ResultSet rs;
		String catalognum = ((BookCopies) msg.getCommPipe().get(0)).getCatalogNumber();
		msg.clearCommPipe();
		try {
			String checkIfReserve = "SELECT R.barcode " + "FROM reservations R, bookcopies BC "
					+ "WHERE ((BC.catalogNumber = ? AND R.barcode = BC.barcode)) AND ((R.barcode) IN (SELECT R1.barcode "
					+ "FROM reservations R1 " + "WHERE R1.reservestatus = 'Pending'))";
			stmt = conn.prepareStatement(checkIfReserve);
			stmt.setString(1, catalognum);
			rs = stmt.executeQuery();
			if (rs.next()) {
				msg.addToCommPipe(-1);
				return msg;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.addToCommPipe(0);
			return msg;
		}
		msg.addToCommPipe(1);
		return msg;
	}

	/**
	 * deletes a message from the messages table.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser deleteMessageTuple(MsgParser msg) {
		PreparedStatement stmt;
		Message toDel = ((Message) msg.getCommPipe().get(0));
		msg.clearCommPipe();
		try {
			String deleteQuery = "DELETE FROM messages WHERE messsageType = ? AND belong = ? AND user= ?";
			stmt = conn.prepareStatement(deleteQuery);
			stmt.setString(1, toDel.getMessageType().name());
			stmt.setString(2, toDel.getBelong());
			stmt.setString(3, toDel.getUser());
			int rs = stmt.executeUpdate();
			if (rs == 0)
				msg.addToCommPipe(false);
			else
				msg.addToCommPipe(true);
		} catch (SQLException e) {
			e.printStackTrace();
			msg.addToCommPipe(false);
		}
		return msg;
	}

	/**
	 * adds a fault to a user.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser addFault(MsgParser msg) {
		PreparedStatement stmt;
		FaultsHistory faultHistory = (FaultsHistory) msg.getCommPipe().get(0);
		try {
			String query = "INSERT INTO faultshistory VALUES (?,?,NOW())";
			stmt = conn.prepareStatement(query);
			stmt.setString(1, faultHistory.getUserID());
			stmt.setString(2, faultHistory.getFault().name());
			stmt.executeUpdate();
			msg.clearCommPipe();
			msg.addToCommPipe(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.addToCommPipe(false);
		}
		return msg;
	}

	/**
	 * check if an activity exists and fetches it.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser checkReportExistence(MsgParser msg) {
		PreparedStatement stmt;
		String reportName = ((ActivityReport) msg.getCommPipe().get(0)).getName();
		msg.clearCommPipe();
		try {
			String query = "SELECT AR.reportFile FROM activityreports AR WHERE AR.reportName = ?";
			stmt = conn.prepareStatement(query);
			stmt.setString(1, reportName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				MyFile reportFile = new MyFile(reportName + ".pdf");
				Blob blob = rs.getBlob(1);
				if (blob == null)
					reportFile = null;
				else {
					int blobLength = (int) blob.length();
					reportFile.initArray(blobLength);
					byte[] arr = blob.getBytes(1, blobLength);
					reportFile.setMybytearray(arr);
					reportFile.setSize(blobLength);
					blob.free();
					msg.addToCommPipe(reportFile);
				}
			} else
				msg.addToCommPipe(null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.addToCommPipe(null);
		}
		return msg;
	}

	/**
	 * adds an activity report to the DB
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser addReportToDB(MsgParser msg) {
		PreparedStatement stmt;
		ActivityReport ar = (ActivityReport) msg.getCommPipe().get(0);
		InputStream is = new ByteArrayInputStream((ar.getFile()).getMybytearray());
		try {
			String query = "INSERT INTO activityreports VALUES (?,?,?,?)";
			stmt = conn.prepareStatement(query);
			stmt.setString(1, ar.getName());
			stmt.setDate(2, ar.getFromDate());
			stmt.setDate(3, ar.getToDate());
			stmt.setBlob(4, is);
			stmt.executeUpdate();
			msg.clearCommPipe();
			msg.addToCommPipe(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.addToCommPipe(false);
		}
		return msg;
	}

	/**
	 * check if the specific user has an active borrow
	 * @param ID the user ID
	 * @return true if exists, false otherwise.
	 */
	public boolean checkBorrowsExistance(String ID) {
		PreparedStatement stmt;
		String getActiveBorrowsQuery = "SELECT * " + "FROM borrows B "
				+ "WHERE B.userID = ? AND (B.status = 'Active' OR B.status = 'LateNotReturned')";
		try {
			stmt = conn.prepareStatement(getActiveBorrowsQuery);
			stmt.setString(1, ID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * get data for the borrow report.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getDataForBorrowedReport(MsgParser msg) {
		Statement stmt;
		ResultSet rs;
		int numOfBooks = 0;
		int median = 0;
		double avg = 0;
		int maxRange = 0;
		int sum = 0;
		StatData sd = new StatData();
		ArrayList<Integer> arr = new ArrayList();
		try {
			stmt = conn.createStatement();
			String select = "SELECT DATEDIFF(BR.actualReturnDate, BR.borrowDate) ";
			String select1 = "SELECT COUNT(DATEDIFF(BR.actualReturnDate, BR.borrowDate)) , MAX(DATEDIFF(BR.actualReturnDate, BR.borrowDate)) , SUM(DATEDIFF(BR.actualReturnDate, BR.borrowDate)), AVG(DATEDIFF(BR.actualReturnDate, BR.borrowDate)) ";
			String from = "FROM borrows BR, bookcopies BC, books B ";
			String where = "WHERE BR.barcode = BC.barcode AND (BR.status = 'Returned' OR BR.status = 'LateReturned') AND BC.catalogNumber = B.catalogNumber AND B.type = 'Wanted'";
			String where1 = "WHERE BR.barcode = BC.barcode AND (BR.status = 'Returned' OR BR.status = 'LateReturned') AND BC.catalogNumber = B.catalogNumber AND B.type = 'Regular'";
			rs = stmt.executeQuery(select + from + where);
			while (rs.next())
				arr.add(rs.getInt(1));
			if (!arr.isEmpty()) {
				Arrays.sort(arr.toArray());
				median = arr.get(arr.size() / 2);
				sd.setWantedArr(arr);
			} else
				sd.setWantedArr(null);
			rs = stmt.executeQuery(select1 + from + where);
			if (rs.next()) {
				numOfBooks = rs.getInt(1);
				maxRange = rs.getInt(2);
				sum = rs.getInt(3);
				avg = rs.getDouble(4);
			}

			sd.setNumOfWantedBooks(numOfBooks);
			sd.setMaxRangeForWantedBooks(maxRange);
			sd.setSumOfDaysForWantedBooks(sum);
			sd.setAvgOfWantedBooks(avg);
			sd.setMedianOfWantedBooks(median);
			stmt.close();
			arr.clear();
			// get statistical data for regular books
			stmt = conn.createStatement();
			rs = stmt.executeQuery(select + from + where1);
			while (rs.next())
				arr.add(rs.getInt(1));
			if (!arr.isEmpty()) {
				Arrays.sort(arr.toArray());
				median = arr.get(arr.size() / 2);
				sd.setRegularArr(arr);
			} else
				sd.setRegularArr(null);

			rs = stmt.executeQuery(select1 + from + where1);
			if (rs.next()) {
				numOfBooks = rs.getInt(1);
				maxRange = rs.getInt(2);
				sum = rs.getInt(3);
				avg = rs.getDouble(4);
			}

			sd.setNumOfRegularBooks(numOfBooks);
			sd.setMaxRangeForRegularBooks(maxRange);
			sd.setSumOfDaysForRegularBooks(sum);
			sd.setAvgOfRegularBooks(avg);
			sd.setMedianOfRegularBooks(median);
			stmt.close();

			msg.addToCommPipe(sd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.addToCommPipe(null);
		}
		return msg;
	}

	/**
	 * get data for the late returned report.
	 * @param msg the parameters
	 * @return the return message
	 */
	public MsgParser getDataForLateReturnedReport(MsgParser msg) {
		Statement stmt;
		ResultSet rs,rs1;
		int i2;
		String select = "SELECT BC.catalognumber,B.title,DATEDIFF(BR.actualReturnDate,BR.returnDate) ";
		String from = "FROM borrows BR, bookcopies BC,books B ";
		String where = "WHERE BC.barcode = BR.barcode AND BR.status = 'LateReturned' AND BC.catalognumber = B.catalogNumber ";
		String groupby = "GROUP BY BC.catalognumber,BC.barcode ORDER BY BC.catalogNumber ASC";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(select + from + where + groupby);
			ArrayList<StatDataForLateReturnedReport> arr = new ArrayList<>();
			
				
				
				
			while (rs.next()) {
				StatDataForLateReturnedReport s = new StatDataForLateReturnedReport();
				ArrayList<Integer> durations = new ArrayList<>();
				String bookTitle = rs.getString(2);
				String previous = rs.getString(1);
				String catalogNumber = previous;
				String next;
				rs1 = rs;
				do {
					next = rs1.getString(1);

					if (previous.equals(next)) {
						durations.add(rs.getInt(3));
						if(rs1.next()) {
							
						}
					}
				} while (previous.equals(next));
				if(rs1 != null)
					rs = rs1;
				
				s.setDurations(durations);
				s.setBookName(bookTitle);
				s.setCatalogNumber(catalogNumber);
				arr.add(s);
				s = null;
				durations = null;
				if(rs1 == null || rs1.next() == false) break;
			}
			stmt.close();
			select = "SELECT BC.catalogNumber,B.title,COUNT(DATEDIFF(BR.actualReturnDate,BR.returnDate)),SUM(DATEDIFF(BR.actualReturnDate,BR.returnDate)),MAX(DATEDIFF(BR.actualReturnDate,BR.returnDate)),AVG(DATEDIFF(BR.actualReturnDate,BR.returnDate)) ";
			groupby = "GROUP BY BC.catalognumber ORDER BY BC.catalogNumber ASC";
			int i = 0;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(select + from + where + groupby);
			while (rs.next()) {
				int totalNumberOfDurations = rs.getInt(3);
				int sumOfDurations = rs.getInt(4);
				int maxDuration = rs.getInt(5);
				double avgDuration = rs.getDouble(6);
				arr.get(i).setTotalNumberOfDurations(totalNumberOfDurations);
				arr.get(i).setSumOfDuration(sumOfDurations);
				arr.get(i).setMaxDuration(maxDuration);
				arr.get(i).setAvgDuration(avgDuration);
				i++;
				if (i >= arr.size())
					break;
			}
			System.out.println(arr);
			if (!arr.isEmpty())
				for (StatDataForLateReturnedReport s : arr) {
					System.out.println("1");
					msg.addToCommPipe(s);
				}
			else
				msg.addToCommPipe(null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.addToCommPipe(null);
		}

		return msg;
	}

}
