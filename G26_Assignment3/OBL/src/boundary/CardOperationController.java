package boundary;

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;

import com.sun.net.httpserver.Authenticator.Failure;

import control.FaultsHistoryController;
import control.LibrarianController;
import control.ManagerController;
import entity.ConstantsAndGlobalVars;
import entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * <h1><b>CardOperationController</b></h1>
 * This is the GUI Controller responsible for all reader card operations the manager can do.
 */
public class CardOperationController {
	/**
	 * instance variables:
	 * librarianController - an instance of LibrarianController, used to do all the operations (search, change status)
	 */
	private ManagerController managerController;
	private LibrarianController librarianController;
	private FaultsHistoryController faultsHistoryController;
	
	private User member;
	
    @FXML
    private ImageView oblImg;

    @FXML
    private TextField userIDTF;

    @FXML
    private Button openBtn;

    @FXML
    private Label currentStatusLabel;

    @FXML
    private ComboBox newStatusCB;
    private ObservableList<String> statusOBS;

    @FXML
    private Button applyBtn;

    @FXML
    private Button cancelBtn;
    
    @FXML
    private AnchorPane resultWindow;

    /**
     * This method saves the changes done to the reader card.  it displays an error alert should a problem occur,
     *  a success alert upon successful operation. 
     */
    @FXML
    void applyHandler(ActionEvent event) {
    	boolean retVal = false;
    	String newStatus = (String)newStatusCB.getSelectionModel().getSelectedItem();
    	if(newStatus != null) {
			try {
				retVal = managerController.changeMemberStatus(userIDTF.getText(), newStatus);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Alert alert = new Alert(AlertType.WARNING);
    			alert.initOwner(openBtn.getScene().getWindow());
    			alert.initModality(Modality.APPLICATION_MODAL);
    			alert.setTitle("Can't change user status");
    			alert.setHeaderText(null);
    			alert.setContentText("Can't change user status. try again (DB problem) !!!");
    			alert.showAndWait();
			}
    	}
    	if(retVal) {
    		try {
				retVal = faultsHistoryController.addFault(enums.FaultDesc.valueOf(newStatus), userIDTF.getText());
				if(retVal) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.initOwner(openBtn.getScene().getWindow());
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.setTitle("User status");
					alert.setHeaderText(null);
					alert.setContentText("User status changed succssefully.");
					alert.showAndWait();
					((Stage)applyBtn.getScene().getWindow()).close();
				}else{
	    			Alert alert = new Alert(AlertType.WARNING);
	    			alert.initOwner(openBtn.getScene().getWindow());
	    			alert.initModality(Modality.APPLICATION_MODAL);
	    			alert.setTitle("Change user status");
	    			alert.setHeaderText(null);
	    			alert.setContentText("Can't add faults to the sql, but the status changed !!!");
	    			alert.showAndWait();
	    		}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Alert alert = new Alert(AlertType.WARNING);
    			alert.initOwner(openBtn.getScene().getWindow());
    			alert.initModality(Modality.APPLICATION_MODAL);
    			alert.setTitle("Change user status");
    			alert.setHeaderText(null);
    			alert.setContentText("Can't add faults to the sql, try again (DB problem) !!!");
    			alert.showAndWait();
			}
    		
    	}else {
    		Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(openBtn.getScene().getWindow());
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Can't change user status");
			alert.setHeaderText(null);
			alert.setContentText("Can't change user status. try again !!!");
			alert.showAndWait();
    	}
    	
    }

    /**
     * this method closes the current window
     */
    @FXML
    void cancelHandler(ActionEvent event) {
    	Stage window = (Stage) cancelBtn.getScene().getWindow();
		window.close();
    }

    /**
     * this method opens the details of a reader card should it exist in the database.
     */
    @FXML
    void openHandler(ActionEvent event) {
    	boolean isOk = checkInputValidation(userIDTF);
    	User member = null;
    	if(isOk) {
    		try {
				member = librarianController.searchForMember(userIDTF.getText());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if(member != null) {
    			resultWindow.setVisible(true);
    			currentStatusLabel.setText(member.getStatus().toString());
    			ArrayList<String> arr = new ArrayList<>();
    			for(enums.UserStatus us : enums.UserStatus.values()) 
    				if(!(us.equals(member.getStatus()))) arr.add(us.toString());
    			statusOBS = FXCollections.observableArrayList(arr);
    			newStatusCB.setItems(statusOBS);
    		}else {
    			Alert alert = new Alert(AlertType.WARNING);
    			alert.initOwner(openBtn.getScene().getWindow());
    			alert.initModality(Modality.APPLICATION_MODAL);
    			alert.setTitle("User doesn't exists");
    			alert.setHeaderText(null);
    			alert.setContentText("User doesn't exists. try again !!!");
    			alert.showAndWait();
    		}
    	}
    }

	/**
	 * This method is called when the FXML file is loaded, it initialises the variables and some GUI elements.
	 */
    @FXML
    void initialize() {
    	managerController = ManagerController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	librarianController = LibrarianController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	faultsHistoryController = FaultsHistoryController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	resultWindow.setVisible(false);
    	Image img = new Image("/pictures/oblLogo.gif");
    	oblImg.setImage(img);
		userIDTF.textProperty().addListener((observable, oldValue, newValue) -> userIDHandler());
    }

    public void loadUser(User u) {
    	this.member = u;
    }
    
    
    /*
     * ***********************************************************
     * private methods
     * 
     * **********************************************************/
    /**
     * checks if the input is valid in the passed textField
     * @param TF - the textField for which to check the input
     * @return boolean - true if the input is valid, false otherwise
     */
    private boolean checkInputValidation(TextField TF) {
		boolean retVal = true;
		if(TF.getText().isEmpty()) {
			drawField(TF,"red","red");
			retVal = false;
		}
		
		if(TF == userIDTF && retVal) {
			if(TF.getText().length() != 9) {
				drawField(TF,"red" , "red");
				retVal = false;
			}
		}
		
		return retVal;
	}
	/**
	 * changes the style of the passed textField, the text fill is assigned the colour passed as an argument
	 * and so is the border.  
	 * @param TF - the textFeild to change the style.
	 * @param fillColor - the new text fill colour
	 * @param borderColor - the new border colour.
	 */
	private void drawField(TextField TF, String fillColor, String borderColor) {
		TF.setStyle("-fx-text-fill: "+fillColor+";"+"-fx-border-color: "+borderColor+";");
		TF.setPromptText("Required field");
	}
	/**
	 * handles the input of userID TextField. called each time a character is typed to the textField.
	 */
	private void userIDHandler() {
		int length = userIDTF.getText().length();
		if (length > 0) {
			drawField(userIDTF, "black","transparent");
			userIDTF.setPromptText("Fill in user ID");
			char c = userIDTF.getText().charAt(length - 1);
			if (c < '0' || c > '9' || length > 9) {
				userIDTF.deleteNextChar();
				length = userIDTF.getText().length();
			}
		}
	}
}
