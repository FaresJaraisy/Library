package boundary;
//////////////////////////////////////
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import control.LendController;
import control.DelayController;
import entity.BookCopies;
import entity.ConstantsAndGlobalVars;
import entity.MsgParser;
import entity.User;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
/**
 * this class controls the Manual Delay Borrow GUI. It receives input from the librarian and sends a request to the server to delay some borrowed book,
 * receives a response from the server and displays feedback (success, failure).
 */
public class ManualDelayBorrowController {
	/**
	 * instance variables:
	 * currentUser - the current librarian logged in.
	 * manualDelayController - an instance of ManualDelayController to use when requesting the delay from the server.
	 */
	private User currentUser;
	private DelayController delayController;
	private String catalognum;
	private enums.ExistStatus delayresult;
	private Date returnDate;
	private enums.Result updateresult;
	private enums.Result insertresult;
	private Date date;
	
	
	
    @FXML
    private DatePicker newDatePicker;

    @FXML
    private TextField fillBarcodeTF;

    @FXML
    private TextField IDBorrowerTF;

    @FXML
    private Button applyButton;

    @FXML
    private Button cancelButton;
    @FXML
    private Label resultlabel;
    
    /**
     * called from the previous window, it loads the librarian currently logged in into an instance variables.
     * @param u - the current librarian logged in.
     */
    public void loadUser(User u) {
		this.currentUser = u;
    }
	/**
	 * called upon loading the FXML file, initialises some GUI elements and instance variables.
	 */
    @FXML
    void initialize(){

    	delayController=DelayController.getInstance(ConstantsAndGlobalVars.ipAddress,
				ConstantsAndGlobalVars.DEFAULT_PORT);
		//closeBtn.getScene().getWindow().setOnCloseRequest(e->closeBorrowWindow());
		
	}

    /**
     * handles the event where the librarian clicks "Apply" button.
     * It validates the input (i.e. not empty), sends the request to the server, receives feedback from the server and displays it.
     * @param event - auto-generated by SceneBuilder.
     */
    @FXML
    void applyDelayHandler(ActionEvent event) {
    	resultlabel.setText("");
    	resultlabel.setVisible(false);
    	String userName = IDBorrowerTF.getText();
    	String copyBarcode = fillBarcodeTF.getText();
    	
    	LocalDate lendDate =newDatePicker.getValue();

    	 
    	if (userName.isEmpty() ) {
    		resultlabel.setText("Delay failed you should enter borrowerID");
    		resultlabel.setTextFill(Color.RED);
    		resultlabel.setVisible(true);
			return;
		}
    	if ( copyBarcode.isEmpty()) {
    		resultlabel.setText("Delay failed you should enter copy barcode");
    		resultlabel.setTextFill(Color.RED);
    		resultlabel.setVisible(true);
			return;
		}
    	if (lendDate==null) {
    		resultlabel.setText("Delay failed you should enter due date");
    		resultlabel.setTextFill(Color.RED);
    		resultlabel.setVisible(true);
			return;
		}
    	
    	MsgParser msg;
    	enums.ExistStatus isexist;
    	BookCopies copy;
    	MsgParser msg2;
    	MsgParser msg3;
    	Timestamp borrowDate;
    	Date previousReturnDate;
    	int copyreserved;
    	enums.UserStatus status;

    
    	
    	
    	try {
    	msg = delayController.checkuser(userName);
		isexist=msg.getIsExist();
		if(isexist==enums.ExistStatus.NotExist)
    	{
    		resultlabel.setText("Delay fail.User not exist");
    		resultlabel.setTextFill(Color.RED);
		    resultlabel.setVisible(true);
		    return;
    	}
		status=((User) msg.getCommPipe().get(0)).getStatus();
		if(status!=enums.UserStatus.Active) {
			resultlabel.setText("Delay fail.User status isn't ACTIVE");
			resultlabel.setTextFill(Color.RED);
		    resultlabel.setVisible(true);
		    return;
		}
		copy=delayController.checkcopy(copyBarcode);
		if(copy==null) {
			resultlabel.setText("Delay fail.Wrong barcode");
			resultlabel.setTextFill(Color.RED);
		    resultlabel.setVisible(true);
		    return;
		}
		catalognum=copy.getCatalogNumber();
		msg2=delayController.checkbooktype(catalognum,copyBarcode);
		returnDate = (Date) Date.from(lendDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		 
		if(msg2.getType()==enums.BookType.Wanted) {
			 resultlabel.setText("Delay fail.This book is marked as WANTED you cann't delay it");
			 resultlabel.setTextFill(Color.RED);
			 resultlabel.setVisible(true);
			 return;
		}
		else {
			msg3=delayController.checkborrow(userName,copyBarcode,enums.BorrowStatus.Active);
			delayresult=msg3.getIsExist();
			borrowDate=msg3.getBorrowDate();
			previousReturnDate=msg3.getReturnDate();
		    if(delayresult==enums.ExistStatus.NotExist) {
		    	resultlabel.setText("Delay fail.This copy isn't borrowed by this user");
				 resultlabel.setTextFill(Color.RED);
				 resultlabel.setVisible(true);
				 return;
		    }
		    copyreserved=delayController.checkIfReserve(copyBarcode,catalognum);
		    if(copyreserved==-1) {
		    	resultlabel.setText("Delay fail.This copy has already reserved");
				 resultlabel.setTextFill(Color.RED);
				 resultlabel.setVisible(true);
				 return;
		    }
		    int noOfDays = 14; //i.e two weeks
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(previousReturnDate);            
		    calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
		    Date validDate = calendar.getTime();
		    
		   
		    if(returnDate.before(previousReturnDate)) {
		    	resultlabel.setText("Delay fail.This date is befor the previous returnDate");
				 resultlabel.setTextFill(Color.RED);
				 resultlabel.setVisible(true);
				 return;
		    }
		    
		    if(validDate.before(returnDate)) {
		    	resultlabel.setText("Delay fail.You cann't delay for more than two weeks.");
				 resultlabel.setTextFill(Color.RED);
				 resultlabel.setVisible(true);
				 return;
		    }
		    updateresult=delayController.UpdateBorrowTableAfterDelaying(userName, copyBarcode, currentUser.getUserID(),returnDate,enums.BorrowStatus.Active );
		    if(updateresult==enums.Result.Fail) {
		    	resultlabel.setText("Delay fail");
				 resultlabel.setTextFill(Color.RED);
				 resultlabel.setVisible(true);
				 return;
		    }
		    LocalDate now = LocalDate.now();
		    date=(Date) Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
		    insertresult=delayController.UpdateDelayTable(currentUser.getUserID(), date, userName,copyBarcode,borrowDate );
		
		    if(insertresult==enums.Result.Fail) {
		    	resultlabel.setText("Delay fail");
				 resultlabel.setTextFill(Color.RED);
				 resultlabel.setVisible(true);
				 return;
		    }
		    if(insertresult==enums.Result.Occured) {
		    	resultlabel.setText("Delay already occured");
				 resultlabel.setTextFill(Color.RED);
				 resultlabel.setVisible(true);
				 return;
		    }
		
		    else {
		    	resultlabel.setText("Delay success");
				 resultlabel.setTextFill(Color.GREEN);
				 resultlabel.setVisible(true);
				 return;
		    }
		}
		
    	}
    	catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
  
    }

    /**
     * closes the current window.
     * @param event - auto-generated by SceneBuilder
     */
    @FXML
    void cancelHandler(ActionEvent event) {
    	closeManualDelayWindow();
    }
    public void closeManualDelayWindow()
    {
    	
    	Stage window = (Stage) cancelButton.getScene().getWindow();
    	window.close();
    }

}