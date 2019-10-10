package boundary;

import java.io.IOException;
import java.util.ArrayList;

import control.FaultsHistoryController;
import control.LibrarianController;
import control.ManagerController;
import entity.ConstantsAndGlobalVars;
import entity.Permissions;
import entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * This class controls the GUI responsible for showing the details of a reader card and editing them.
 */
public class ReaderCardController {
	/**
	 * instance variables:
	 * member - the member being edited
	 * currentUser - the currently logged user (librarian)
	 * librarianController - an instance of LibrarianController to use when applying the changes done.
	 * permissions - to view the permissions for the member.
	 */
	private User member;
	private User currentUser;
	private LibrarianController librarianController;
	private ManagerController managerController;
	private Permissions permissions;
	private FaultsHistoryController faultsHistoryController;

	@FXML
    private ImageView oblImg;

    @FXML
    private Button CancelBtn;

    @FXML
    private Button confirmBtn;

    @FXML
    private Button editBtn;
    
    @FXML
    private TextField userIDTF;

    @FXML
    private TextField firstNameTF;

    @FXML
    private TextField lastNameTF;

    @FXML
    private TextField phoneNumberTF;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField passwordTF;

    @FXML
    private TextField membershipNumberTF;

    @FXML
    private ComboBox statusCB;
    private ObservableList<String> statusOBS;
    
    @FXML
    private ComboBox canBorrowCB;
    private ObservableList<String> canBorrowOBS;

    @FXML
    private ComboBox canReserveCB;
    private ObservableList<String> canReserveOBS;

    @FXML
    private TextField strikesTF;
    
    @FXML
    private Button falutsBtn;

    @FXML
    private Button borrowsBtn;

    @FXML
    private Button reservationsBtn;

    @FXML
    private Button historyBtn;
	/**
	 * called upon loading the FXML file, initialises some GUI elements and instance variables.
	 */
    @FXML
    void initialize() {
    	librarianController = LibrarianController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	managerController = ManagerController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	faultsHistoryController = FaultsHistoryController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);

    	Image img = new Image("/pictures/oblLogo.gif");
    	oblImg.setImage(img);
    }

    /**
     * closes the current window
     * @param event - auto-generated by SceneBuilder
     */
    @FXML
    void cancelHandler(ActionEvent event) {
		Stage window = (Stage) CancelBtn.getScene().getWindow();
		window.close();
    }

    /**
     * saves the changes made to the reader card, sends request to the server to save them and receives a response from the 
     * server, displays the response (failure, success).
     * @param event - auto-generated by SceneBuilder
     */
    @FXML
    void confirmHandler(ActionEvent event) {
    	boolean changeStatus = false;
    	boolean changeBorrowPermission = false;
    	boolean changeReservePermission = false;
    	boolean isCatch = false;
    	String popUpMsg = "";
		String newStatus = (String) statusCB.getSelectionModel().getSelectedItem();
		String newBorrowPermission = (String) canBorrowCB.getSelectionModel().getSelectedItem();
		String newReservePermission = (String) canReserveCB.getSelectionModel().getSelectedItem();
		
		if(newStatus != null) {
			try {
				changeStatus = managerController.changeMemberStatus(member.getUserID(), newStatus);
				if(!changeStatus) popUpMsg += "Can't update member status\n";
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				popUpMsg += "Can't update member status\n";
				isCatch = true;
			}
		}
		if(!isCatch && changeStatus) {
			popUpMsg += "Member status succssefully updated\n"; 
			try {
				isCatch = faultsHistoryController.addFault(enums.FaultDesc.valueOf(newStatus), userIDTF.getText());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		isCatch = false;
		if(newBorrowPermission != null) {
			try {
				changeBorrowPermission = managerController.changeBorrowPermission(member.getUserID(), newBorrowPermission);
				if(!changeBorrowPermission) popUpMsg += "Can't update user borrow permission\n";
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isCatch = true;
				popUpMsg += "Can't update user borrow permission\n";
			}
		}
		if(!isCatch && changeBorrowPermission) popUpMsg += "Member borrow permission succssefully updated\n";
		
		isCatch = false;
		if(newReservePermission != null) {
			try {
				changeReservePermission = managerController.changeReservePermission(member.getUserID(), newReservePermission);
				if(!changeReservePermission) popUpMsg += "Can't update user reserve permission\n";
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isCatch = true;
				popUpMsg += "Can't update user reserve permission\n";
			}
		}
		if(!isCatch && changeReservePermission) popUpMsg += "Member reserve permission succssefully updated\n";
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(CancelBtn.getScene().getWindow());
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle("Updating status");
		alert.setHeaderText(null);
		if(popUpMsg.equals("")) popUpMsg = "Nothing changed";
		alert.setContentText(popUpMsg);
		alert.showAndWait();
		
		((Stage)confirmBtn.getScene().getWindow()).close();
    }

    /**
     * changes the GUI elements to editable.
     * @param event - auto-generated by SceneBuilder.
     */
    @FXML
    void editHandler(ActionEvent event) {
		statusCB.setMouseTransparent(false);
		canBorrowCB.setMouseTransparent(false);
		canReserveCB.setMouseTransparent(false);
		confirmBtn.setVisible(true);
    }
    /**
     * opens a new window containing a tableView that shows all active borrows for the user.
     * @param event - auto-generated by SceneBuilder
     * @throws IOException - thrown should loading the FXML file encounter a problem.
     */
    @FXML
    void activeBorrowsHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/UserBorrowsGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Active Borrows");
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		UserBorrowsController ubc = loader.getController(); //send user to reader card controller
		ubc.loadUser(member);
		primaryStage.show();
    }
    /**
     * opens a new window containing a tableView that shows all active reservation for the user.
     * @param event - auto-generated by SceneBuilder
     * @throws IOException - thrown should loading the FXML file encounter a problem.
     */
    @FXML
    void activeReservationsHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/ReservationsGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Active Reservations");
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		UserReservationsController urc = loader.getController(); //send user to reader card controller
		urc.loadUser(member);
		primaryStage.show();
    }
    /**
     * opens a new window containing a tableView that shows all faults for the user.
     * @param event - auto-generated by SceneBuilder
     * @throws IOException - thrown should loading the FXML file encounter a problem.
     */
    @FXML
    void faultsHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/UserFaultsHistoryGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Faults History");
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		UserFaultsHistoryController ufc = loader.getController(); //send user to reader card controller
		ufc.loadUser(member);
		primaryStage.show();
    }
    /**
     * opens a new window containing a tableView that shows all the history for the user.
     * @param event - auto-generated by SceneBuilder
     * @throws IOException - thrown should loading the FXML file encounter a problem.
     */
    @FXML
    void historyHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/HistoryGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Borrows & Reservations History");
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		UserHistoryController uhc = loader.getController(); //send user to reader card controller
		uhc.loadUser(member);
		primaryStage.show();

    }
    
    /*
     * *****************************************************
     * 
     * 
     **************************************************** */
    /**
     * loads the member to edit, the currently logged in user (the librarian) into instance variable.
     * loads the member details into GUI elements.
     * @param member - the member to edit
     * @param user - the currently logged in member.
     */
    public void loadUser(User member, User user) {
    	this.member = member;
    	this.currentUser = user;
    	userIDTF.setText(member.getUserID());
    	firstNameTF.setText(member.getFirstName());
    	lastNameTF.setText(member.getLastName());
    	phoneNumberTF.setText(member.getPhoneNumber());
    	emailTF.setText(member.getEmail());
    	passwordTF.setText(member.getPassword());
    	strikesTF.setText(Integer.toString(member.getStrikes()));
    	membershipNumberTF.setText(member.getMembershipNumber());
    	statusCB.setPromptText(member.getStatus().toString());
		statusCB.setMouseTransparent(true);
		confirmBtn.setVisible(false);
		
		try {
			permissions = librarianController.getMemberPermissions(member.getUserID());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(CancelBtn.getScene().getWindow());
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Can't load permissions");
			alert.setHeaderText(null);
			alert.setContentText("Can't get the member permissions !!!");
			alert.showAndWait();
		}
		
		if(permissions.isCanBorrow())
			canBorrowCB.setPromptText("YES");
		else canBorrowCB.setPromptText("NO");
		if(permissions.isCanReserve())
			canReserveCB.setPromptText("YES");
		else canReserveCB.setPromptText("NO");
		
		canBorrowCB.setMouseTransparent(true);
		canReserveCB.setMouseTransparent(true);
		
    	boolean isManager = false;
		try {
			isManager = librarianController.checkEmployeeType(currentUser);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(CancelBtn.getScene().getWindow());
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Can't edit details");
			alert.setHeaderText(null);
			alert.setContentText("Can't get user identity !!!");
			alert.showAndWait();
		}
		if(!isManager) {
			editBtn.setVisible(false);
		}else {
	    	ArrayList<String> arr = new ArrayList<>();
	    	for(enums.UserStatus us : enums.UserStatus.values()) {
	    		if(!(us.name().equals(statusCB.getPromptText())))
	    			arr.add(us.name());
	    	}
			statusOBS = FXCollections.observableArrayList(arr);
			statusCB.setItems(statusOBS);
			
			arr.clear();
			if(canBorrowCB.getPromptText().equals("YES"))
				arr.add("NO");
			else arr.add("YES");
			canBorrowOBS = FXCollections.observableArrayList(arr);
			canBorrowCB.setItems(canBorrowOBS);
			
			arr.clear();
			if(canReserveCB.getPromptText().equals("YES"))
				arr.add("NO");
			else arr.add("YES");
			canReserveOBS = FXCollections.observableArrayList(arr);
			canReserveCB.setItems(canReserveOBS);
			
		}
    }
}
