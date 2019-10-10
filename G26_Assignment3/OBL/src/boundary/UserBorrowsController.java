package boundary;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import control.BorrowsController;
import control.DelayController;
import control.UserController;
import entity.Book;
import entity.BookCopies;
import entity.Borrows;
import entity.BorrowsWithButton;
import entity.ConstantsAndGlobalVars;
import entity.Message;
import entity.MsgParser;
import entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * This class shows the users' active borrows in a tableView.
 */
public class UserBorrowsController {
	/**
	 * instance variables:
	 * borrowsController - an instance of BorrowsController to use when fetching the borrows from the DB.
	 * res - an arrayList containing the active borrows.
	 * currentUser - the currently logged in user.
	 */
	private BorrowsController borrowsController;
	private ArrayList<Borrows> res;
	private ArrayList<BorrowsWithButton> resWithBtn;
	private ObservableList<Borrows> data;
	private User currentUser;
	private DelayController delayController;
	
	@FXML
    private ImageView oblImg;

    @FXML
    private TableView table;

    @FXML
    private TableColumn userIDTC;

    @FXML
    private TableColumn barcodeTC;

    @FXML
    private TableColumn librarianIDTC;

    @FXML
    private TableColumn borrowDateTC;

    @FXML
    private TableColumn returnDateTC;

    @FXML
    private TableColumn statusTC;
    
    @FXML
    private TableColumn delayTC;

    @FXML
    private Button closeBtn;

	/**
	 * called upon loading the FXML file, initializes some GUI elements and instance variables.
	 */
    @FXML
	void initialize() {
		borrowsController = BorrowsController.getInstance(ConstantsAndGlobalVars.ipAddress,
				ConstantsAndGlobalVars.DEFAULT_PORT);
		System.out.println("1333333");
		delayController=DelayController.getInstance(ConstantsAndGlobalVars.ipAddress,
				ConstantsAndGlobalVars.DEFAULT_PORT);
		System.out.println("13344444");
		Image img = new Image("/pictures/oblLogo.gif");
		oblImg.setImage(img);
		//closeBtn.getScene().getWindow().setOnCloseRequest(e->closeBorrowWindow());
		
	}
    
    
    /**
     * loads the currently logged in user into an instance variable, fetches the users' borrow list from the DB
     * and displays them in a tableView.
     * @param u - the currently logged in user.
     */
    public void loadUser(User u) {
		this.currentUser = u;
		System.out.println(u.getFirstName());
		try {
			res = borrowsController.viewborrows(currentUser);
			System.out.println("dfferferfref");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.resWithBtn = new ArrayList<>();
		for(Borrows b : res) {
			 String userID=b.getUserID();
			 String barcode=b.getBarcode();
			 String librarianID=b.getLibrarianID();
			 Date borrowDate=b.getBorrowDate();
			 Date returnDate=b.getReturnDate();
			 Date actualReturnDate=b.getActualReturnDate();
			 enums.BorrowStatus status=b.getStatus();
			 BorrowsWithButton borrow=new BorrowsWithButton(userID,barcode,librarianID,borrowDate,returnDate,actualReturnDate,status);
			 borrow.getDelayBorrowBtn().setOnAction(e -> {
					try {
						DelayBorrowHandler(borrow);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
			 this.resWithBtn.add(borrow);
		}
		data = FXCollections.observableArrayList(resWithBtn);
		table.setItems(data);
		userIDTC.setCellValueFactory(new PropertyValueFactory("userID"));
		barcodeTC.setCellValueFactory(new PropertyValueFactory("barcode"));
		librarianIDTC.setCellValueFactory(new PropertyValueFactory("librarianID"));
		borrowDateTC.setCellValueFactory(new PropertyValueFactory("borrowDate"));
		returnDateTC.setCellValueFactory(new PropertyValueFactory("returnDate"));
		statusTC.setCellValueFactory(new PropertyValueFactory("status"));
		delayTC.setCellValueFactory(new PropertyValueFactory("DelayBorrowBtn"));
	}

    @FXML
    void onClose(ActionEvent event) {
    	closeBorrowWindow();
    }
    /**
     * closes the current window, clears the list of borrows.
     */
    public void closeBorrowWindow()
    {
    	if(res != null)
    		res.clear();
    	Stage window = (Stage) closeBtn.getScene().getWindow();
    	window.close();
    }
    private void DelayBorrowHandler(BorrowsWithButton borrow) throws IOException {
    	BookCopies copy;
    	String catalognum;
    	MsgParser msg2;
    	MsgParser msg3;
    	MsgParser msg;
    	Date previousReturnDate;
    	Date currentDate;
    	enums.Result updateBoorowResult,updateMessageResult;
    	Message message;
    	int copyreserved;
    	enums.UserStatus status;

    	
   	    try {
   	    	msg = delayController.checkuser(borrow.getUserID());
   	    	status=((User) msg.getCommPipe().get(0)).getStatus();
			if(status!=enums.UserStatus.Active) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(borrow.getDelayBorrowBtn().getScene().getWindow());
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setTitle("Delay borrow");
				alert.setHeaderText(null);
				alert.setContentText("Delay fail.\nDelay fail.User status isn't ACTIVE ");
				alert.showAndWait();
				return;
			}
			copy=delayController.checkcopy(borrow.getBarcode());
			catalognum=copy.getCatalogNumber();
			msg2=delayController.checkbooktype(catalognum,borrow.getBarcode());
			if(msg2.getType()==enums.BookType.Wanted) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(borrow.getDelayBorrowBtn().getScene().getWindow());
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setTitle("Delay borrow");
				alert.setHeaderText(null);
				alert.setContentText("Delay fail.\nThis book is marked as WANTED you cann't delay it ");
				alert.showAndWait();
				return;
			}
			msg3=delayController.checkborrow(borrow.getUserID(),borrow.getBarcode(),enums.BorrowStatus.Active);
			previousReturnDate=msg3.getReturnDate();
			
			copyreserved=delayController.checkIfReserve(borrow.getBarcode(),catalognum);
			if(copyreserved==-1) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(borrow.getDelayBorrowBtn().getScene().getWindow());
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setTitle("Delay borrow");
				alert.setHeaderText(null);
				alert.setContentText("Delay fail.\nThis copy has already reserved ");
				alert.showAndWait();
				return;
		    }
			System.out.println(previousReturnDate);
			Calendar calendar = Calendar.getInstance();
		    calendar.setTime(previousReturnDate);            
		    calendar.add(Calendar.WEEK_OF_YEAR, -1);
		    Date validDate = calendar.getTime();
		    LocalDate now = LocalDate.now();
		    currentDate=(Date) Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
			if(currentDate.before(validDate)) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(borrow.getDelayBorrowBtn().getScene().getWindow());
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setTitle("Delay borrow");
				alert.setHeaderText(null);
				alert.setContentText("Delay failed.\nReturn date is not in less than a week.");
				alert.showAndWait();
				return;
			}
		    calendar.setTime(previousReturnDate);            
		    calendar.add(Calendar.WEEK_OF_YEAR, 1);
		    Date returnDate=calendar.getTime();
			updateBoorowResult=delayController.UpdateBorrowTableAfterDelaying(borrow.getUserID(), borrow.getBarcode(), borrow.getLibrarianID(),returnDate,enums.BorrowStatus.Active );
			if(updateBoorowResult==enums.Result.Fail) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(borrow.getDelayBorrowBtn().getScene().getWindow());
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setTitle("Delay borrow");
				alert.setHeaderText(null);
				alert.setContentText("Delay failed.");
				alert.showAndWait();
				return;
		    }
			message=new Message(enums.MessageType.view,"Borrow delayed","user "+borrow.getUserID()+" delayed borrow for "+borrow.getBarcode()+" to "+returnDate,borrow.getLibrarianID(),currentDate,borrow.getUserID() );
	        updateMessageResult=delayController.SendMessageTable(message);
	        if(updateMessageResult==enums.Result.Fail) {
	        	Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(borrow.getDelayBorrowBtn().getScene().getWindow());
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setTitle("Delay borrow");
				alert.setHeaderText(null);
				alert.setContentText("Delay failed.");
				alert.showAndWait();
				return;
	        }
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(borrow.getDelayBorrowBtn().getScene().getWindow());
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Delay borrow");
			alert.setHeaderText(null);
			alert.setContentText("Delay success");
			alert.showAndWait();
			return;
			
			
			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    
    }

}
