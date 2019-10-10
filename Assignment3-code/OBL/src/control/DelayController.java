package control;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.Semaphore;

import entity.BookCopies;
import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.IClient;
import entity.ManualDelays;
import entity.Message;
import entity.MsgParser;
import entity.Reservations;
import entity.User;

public class DelayController implements IClient{
	private Client client;
	private static DelayController DelaySingleton;
	private User user;
	private Semaphore sem1;
	private MsgParser Msg1;
	private MsgParser Msg2;
	private MsgParser Msg3;
	private BookCopies copy;
	private enums.ExistStatus borrowresult;
	private enums.Result UpdateBorrowResult;
	private enums.Result UpdateDelayResult;
	private enums.Result UpdateMessageResult;
	int copyreserved;

	
	private  DelayController(String host, int port) {
		try {
			client = new Client(host,port,this);
			sem1 = new Semaphore(0);
			//result= new ArrayList<>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void disconnectClient() {
		 this.client.quit();
	}
	public synchronized static DelayController getInstance(String host, int port) {
		if(DelaySingleton == null)
			DelaySingleton = new DelayController(host,port);
		return DelaySingleton;
	}
	
public MsgParser checkuser(String username ) throws InterruptedException {
		
		MsgParser<User> msg = new MsgParser<>();
		User user = new User(username);
		msg.addToCommPipe(user);
		msg.setTask(ConstantsAndGlobalVars.checkUserTask);
		
		client.sendMessageToServer(msg);
		sem1.acquire();
		
		return Msg1;
	
		}

public MsgParser checkborrow(String userName,String copyBarcod,enums.BorrowStatus status) throws InterruptedException {
	
	MsgParser<Borrows> msg = new MsgParser<>();
	Borrows borrow = new Borrows(userName,copyBarcod,status);
	
	msg.addToCommPipe(borrow);
	msg.setTask(ConstantsAndGlobalVars.checkBorrowTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return Msg3;

	}
public BookCopies checkcopy(String barcode ) throws InterruptedException {
	
	MsgParser<BookCopies> msg = new MsgParser<>();
	BookCopies copy = new BookCopies(barcode);
	msg.addToCommPipe(copy);
	msg.setTask(ConstantsAndGlobalVars.checkCopyTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return this.copy;
}
public enums.Result UpdateBorrowTableAfterDelaying(String userName,String copyBarcode,String librarianID,Date returnDate,enums.BorrowStatus status ) throws InterruptedException {
	System.out.println("33");
	MsgParser<Borrows> msg = new MsgParser<>();
	Borrows borrow = new Borrows(userName,copyBarcode,librarianID,returnDate,status);
	msg.addToCommPipe(borrow);
	msg.setTask(ConstantsAndGlobalVars.UpdateBorrowTableAfterDelayingTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return this.UpdateBorrowResult;
}

public MsgParser checkbooktype(String catalognum ,String barcode) throws InterruptedException {
	
	MsgParser<BookCopies> msg = new MsgParser<>();
	BookCopies copy = new BookCopies(barcode,catalognum);
	msg.addToCommPipe(copy);
	msg.setTask(ConstantsAndGlobalVars.checkBookTypeTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return Msg2;

	}
public enums.Result UpdateDelayTable(String librarianID,Date Date ,String userID,String barcode,Timestamp borrowDate ) throws InterruptedException {
	System.out.println("33");
	MsgParser<ManualDelays> msg = new MsgParser<>();
	ManualDelays delay = new ManualDelays(librarianID,Date,userID,barcode,borrowDate);
	msg.addToCommPipe(delay);
	msg.setTask(ConstantsAndGlobalVars.UpdateDelayTableTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return this.UpdateDelayResult;
}
public enums.Result SendMessageTable(Message message ) throws InterruptedException {
	System.out.println("33");
	MsgParser<Message> msg = new MsgParser<>();
	
	msg.addToCommPipe(message);
	msg.setTask(ConstantsAndGlobalVars.sendMessageForDelayTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return this.UpdateMessageResult;
}
public int checkIfReserve(String barcode,String catalognum ) throws InterruptedException {
	System.out.println("33");
	MsgParser<BookCopies> msg = new MsgParser<>();
	BookCopies copy=new BookCopies(barcode,catalognum);
	msg.addToCommPipe(copy);
	msg.setTask(ConstantsAndGlobalVars.checkIfReservedTask);
	
	client.sendMessageToServer(msg);
	sem1.acquire();
	
	return this.copyreserved;
}

	public void recieveMessageFromServer(MsgParser msg) {
		if(msg.getTask().equals(ConstantsAndGlobalVars.checkUserTask)) {
			this.Msg1=msg;
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.checkCopyTask)) {
			copy = (BookCopies)msg.getCommPipe().get(0);
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.checkBookTypeTask)) {
			this.Msg2=msg;
			
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.checkBorrowTask)) {
			this.Msg3=msg;
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.UpdateBorrowTableAfterDelayingTask)) {
			this.UpdateBorrowResult=msg.getBorrowresult();
		}
		if(msg.getTask().equals(ConstantsAndGlobalVars.UpdateDelayTableTask)) {
			UpdateDelayResult=msg.getDelayResult();
		}	
		if(msg.getTask().equals(ConstantsAndGlobalVars.sendMessageForDelayTask)) {
			UpdateMessageResult=msg.getDelayResult();
		}	
		if(msg.getTask().equals(ConstantsAndGlobalVars.checkIfReservedTask)) {
			copyreserved=(int)msg.getCommPipe().get(0);
		}	
		sem1.release();
	}
}
