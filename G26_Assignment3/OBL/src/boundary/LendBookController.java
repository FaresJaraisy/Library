package boundary;
/////////////////////////////////////////////////////////
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import control.BorrowsController;
import control.LendController;
import entity.BookCopies;
import entity.ConstantsAndGlobalVars;
import entity.MsgParser;
import entity.TwoDaysMessage;
import entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
/**
 * This class is responsible for enabling the librarian to lend a book for a student.
 */
public class LendBookController {
	/**
	 * instance variables:
	 * lendController - an instance of LendController to use when lending the book
	 * currentUser - the librarian doing the lend operation.
	 */
	private LendController lendController;
	private User currentUser;
	private int lendday;
	private int nowday;
	private int nowmounth;
	private LocalDate wantedDate;
	private LocalDate regularDate;
	private int lendmounth;
	private Date borrowDate;
	private Date returnDate;
	private String catalognum;
	private enums.Result borrowresult;
	private enums.UpdateCopyResult updatecopyresult;
	private enums.Result updateReserveresult;
 
    @FXML
    private DatePicker date;

    @FXML
    private TextField barcode;

    @FXML
    private TextField borrowerID;

    @FXML
    private Button lendbtn;

    @FXML
    private Label resultlabel;

    @FXML
    private Button cancelbtn;
    /**
     * loads the current librarian into an instance variable.
     * @param u - the current librarian
     */
    public void loadUser(User u) {
		this.currentUser = u;
    }
    /**
     * called when the FXML file loads, initialises the instance variable lendController.
     */
    @FXML
    void initialize(){

    	lendController=LendController.getInstance(ConstantsAndGlobalVars.ipAddress,
				ConstantsAndGlobalVars.DEFAULT_PORT);
		//closeBtn.getScene().getWindow().setOnCloseRequest(e->closeBorrowWindow());
		
	}
    /**
     * handles the event where the librarian presses "Lend" button, checks if all input is valid (i.e. not empty)
     * checks if the user exists, if the user has permission to borrow, the users' reader card status.
     * if all checks pass, it calls the {@link control.LendController#updateborrow(String, String, String, Date, Date, enums.BorrowStatus)}
     * to update the DB. All the checks are done using instance methods of {@link control.LendController}.
     * @param event auto-generated by SceneBuilder
     */
    @FXML
    void lendHandler(ActionEvent event) {
    	
    	resultlabel.setText("");
    	resultlabel.setVisible(false);
    	String userName = borrowerID.getText();
    	String copyBarcode = barcode.getText();
    	
    	LocalDate lendDate =date.getValue();

    	 
    	if (userName.isEmpty() ) {
    		resultlabel.setText("Lend failed you should enter borrowerID");
    		resultlabel.setTextFill(Color.RED);
    		resultlabel.setVisible(true);
			return;
		}
    	if ( copyBarcode.isEmpty()) {
    		resultlabel.setText("Lend failed you should enter copy barcode");
    		resultlabel.setTextFill(Color.RED);
    		resultlabel.setVisible(true);
			return;
		}
    	if (lendDate==null) {
    		resultlabel.setText("Lend failed you should enter due date");
    		resultlabel.setTextFill(Color.RED);
    		resultlabel.setVisible(true);
			return;
		}
    	
    	BookCopies copy;
    	MsgParser msg;
    	MsgParser msg2;
    	enums.BookType type;
    	enums.ExistStatus isexist;
    	enums.UserStatus status;
    	enums.UserPermissions per;
    	int  numofborrowcopies;
    	int reservExist;
    	TwoDaysMessage td;
    	
		try {
			
			msg = lendController.checkuser(userName);
			
			isexist=msg.getIsExist();
			
			if(isexist==enums.ExistStatus.NotExist)
	    	{
	    		resultlabel.setText("Lend fail.User not exist");
	    		resultlabel.setTextFill(Color.RED);
			    resultlabel.setVisible(true);
			    return;
	    	}
			per=msg.getPer();
			if(per!=enums.UserPermissions.CanBorrow) {
				resultlabel.setText("Lend fail.User cann't borrow");
				resultlabel.setTextFill(Color.RED);
			    resultlabel.setVisible(true);
			    return;
			}
			status=((User) msg.getCommPipe().get(0)).getStatus();
			if(status!=enums.UserStatus.Active) {
				resultlabel.setText("Lend fail.User status isn't ACTIVE");
				resultlabel.setTextFill(Color.RED);
			    resultlabel.setVisible(true);
			    return;
			}
			
			copy=lendController.checkcopy(copyBarcode);
		//	System.out.println(copy.getBarcode());
		//	catalognum=copy.getCatalogNumber();
			if(copy==null) {
				resultlabel.setText("Lend fail.Wrong barcode");
				resultlabel.setTextFill(Color.RED);
			    resultlabel.setVisible(true);
			    return;
			}
			
			catalognum=copy.getCatalogNumber();
			
			if(copy.getStatus()==enums.BookCopyStatus.borrowed)
			{
				resultlabel.setText("Lend fail.Copy of book isn't available");
				resultlabel.setTextFill(Color.RED);
			    resultlabel.setVisible(true);
			    return;
			} 
			
			//if(copy.getStatus()==enums.BookCopyStatus.reserved) {
				reservExist=lendController.checkreserve(userName,copyBarcode);
				System.out.println(reservExist);
				if(reservExist==-1) {
					resultlabel.setText("Lend fail.Copy of book has already been saved  to someone else");
					resultlabel.setTextFill(Color.RED);
				    resultlabel.setVisible(true);
				    return;
				}
			//}
			   LocalDate now = LocalDate.now();
			   
			   wantedDate=now.plusDays(3);
			   regularDate=now.plusWeeks(2);
			   nowday=now.getDayOfMonth();
			
			   nowmounth=now.getMonthValue();
			   lendday=lendDate.getDayOfMonth();
			   
			   
			    returnDate = (Date) Date.from(lendDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			    borrowDate = (Date) Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
			    
			  msg2=lendController.checkbooktype(catalognum,copyBarcode);
//			  numofborrowcopies=lendController.checknumofcopies(copyBarcode);
//			
//			  if(numofborrowcopies>=msg2.getNumOfCopies()) {
//				  resultlabel.setText("There is no enough copies of this book");
//				  resultlabel.setTextFill(Color.RED);
//				    resultlabel.setVisible(true);
//				    return;
//			  }
			  if(now.isAfter(lendDate))
			  {
				  resultlabel.setText("Lend fail.Wrong DATE");
				   resultlabel.setTextFill(Color.RED);
				    resultlabel.setVisible(true);
				    return;
			  }
			  
			  
			  if(msg2.getType()==enums.BookType.Wanted) {
			   if(wantedDate.isBefore(lendDate))
			   {
				   resultlabel.setText("Lend fail.This book is marked as WANTED you cann't lend it for more than three days");
				   resultlabel.setTextFill(Color.RED);
				   resultlabel.setVisible(true);
				   return;
			   }
			 
			  else {
				 
				  updatecopyresult=lendController.updatebookcopy(copyBarcode, catalognum, copy.getPurchaseDate(),enums.BookCopyStatus.borrowed);
				  if(updatecopyresult==enums.UpdateCopyResult.Fail) {
					  resultlabel.setText("Lend FAIL");
					  resultlabel.setTextFill(Color.RED);
					    resultlabel.setVisible(true);
					    return;
				  }
				  borrowresult=lendController.updateborrow(userName,copyBarcode,currentUser.getUserID(),borrowDate,returnDate,enums.BorrowStatus.Active);
				
				  if(borrowresult==enums.Result.Fail) {
					  resultlabel.setText("Lend FAIL");
					  resultlabel.setTextFill(Color.RED);
					    resultlabel.setVisible(true);
					    return;
				  }
				  if(reservExist==1) {
					  System.out.println("200");
				  updateReserveresult=lendController.updateReserve(userName,copyBarcode);
				  if(updateReserveresult==enums.Result.Fail) {
					  resultlabel.setText("Lend FAIL");
					  resultlabel.setTextFill(Color.RED);
					    resultlabel.setVisible(true);
					    return;
				  }
				  }
				 
					 
					  resultlabel.setText("Lend success");
					  resultlabel.setTextFill(Color.GREEN);
					    resultlabel.setVisible(true);
					    return;
				  
				  
				  
			  }
			  }
			  else {
			  
				  if(regularDate.isBefore(lendDate)) {
					  resultlabel.setText("Lend fail. You cann't lend it for more than two weeks");
					  resultlabel.setTextFill(Color.RED);
					    resultlabel.setVisible(true);
					    return;
				  }
				  else {
					  updatecopyresult=lendController.updatebookcopy(copyBarcode, catalognum, copy.getPurchaseDate(),enums.BookCopyStatus.borrowed);
					  if(updatecopyresult==enums.UpdateCopyResult.Fail) {
						  resultlabel.setText("Lend FAIL");
						  resultlabel.setTextFill(Color.RED);
						    resultlabel.setVisible(true);
						    return;
					  }
					  borrowresult=lendController.updateborrow(userName,copyBarcode,currentUser.getUserID(),borrowDate,returnDate,enums.BorrowStatus.Active);
					 
					  if(borrowresult==enums.Result.Fail) {
						  resultlabel.setText("Lend FAIL");
						  resultlabel.setTextFill(Color.RED);
						    resultlabel.setVisible(true);
						    return;
					  }
					  if(reservExist==1) {
						  System.out.println("300");
					  updateReserveresult=lendController.updateReserve(userName,copyBarcode);
					  if(updateReserveresult==enums.Result.Fail) {
						  resultlabel.setText("Lend FAIL");
						  resultlabel.setTextFill(Color.RED);
						    resultlabel.setVisible(true);
						    return;
					  }
					  }
					  
						  
						  resultlabel.setText("Lend success");
						  resultlabel.setTextFill(Color.GREEN);
						    resultlabel.setVisible(true);
						    return;
					  
					 
					 
				  }
			 
			  
			  }
			  }catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    	
    }

    /**
     * closes the current window.
     * @param event - auto-generated by SceneBuilder
     */
    @FXML
    void onClose(ActionEvent event) {
    	closeLendBookWindow();
    }
    public void closeLendBookWindow()
    {
    	
    	Stage window = (Stage) cancelbtn.getScene().getWindow();
    	window.close();
    }

}