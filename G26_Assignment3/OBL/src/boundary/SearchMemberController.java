package boundary;

import java.io.IOException;
import java.util.ArrayList;

import control.FaultsHistoryController;
import control.LibrarianController;
import entity.ConstantsAndGlobalVars;
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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 * This class facilitates searching for members for librarians.
 */
public class SearchMemberController {
	/**
	 * instance variables:
	 * currentUser - the currently logged in user.
	 * librarianController - an instance variable of LibrarianController to use when fetching the searched member from the DB
	 * (should it exist).
	 */
	private User currentUser;
	private LibrarianController librarianController;
	
    @FXML
    private ImageView oblImg;

    @FXML
    private TextField searchTF;

    @FXML
    private Button searchBtn;
	/**
	 * called upon loading the FXML file, initialises some GUI elements and instance variables.
	 */
    @FXML
    void initialize() {
    	librarianController = LibrarianController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	
    	Image imgObl = new Image("/pictures/oblLogo.gif");
		oblImg.setImage(imgObl);
		String img = "/pictures/search.png";
		searchBtn.setStyle("-fx-background-image: url('" + img + "'); " + "-fx-min-height: 10px; "
				+ "-fx-min-width: 10px;" + "-fx-background-size: 100% 100%;" + "-fx-background-repeat: no-repeat;"
				+ "-fx-background-position: center;");
		
		searchTF.textProperty().addListener((observable, oldValue, newValue) -> searchTFHandler());
    }
    
   
    /**
     * this method checks input validation of the search TextField, calls the {@link control.LibrarianController#searchForMember(String)} method and displays the result:
     * either an alert to indicate an error has occurred, or a window containing the reader card details of that member (@see boundary.ReaderCardController).
     * @param event - auto-generated.
     * @throws IOException - thrown should loading the FXML file encounter a problem.
     */
    @FXML
    void searchHandler(ActionEvent event) throws IOException {
    	boolean isOk = true;
        isOk = checkInputValidation(searchTF);
        User member ;
        
        if(isOk) {
        	try {
        		member = librarianController.searchForMember(searchTF.getText());
				if(member != null) {
					Stage primaryStage = new Stage();
					//((Stage) searchBtn.getScene().getWindow()).close();
					FXMLLoader loader = new FXMLLoader();
					Parent root = loader.load(getClass().getResource("/boundary/ReaderCardGUI.fxml").openStream());
					primaryStage.setScene(new Scene(root));
					primaryStage.setTitle("Reader Card");
					primaryStage.setAlwaysOnTop(true);
					primaryStage.initModality(Modality.APPLICATION_MODAL);
					ReaderCardController rc = loader.getController(); //send user to reader card controller
					rc.loadUser(member, currentUser);
					primaryStage.show();
				}else {
					//((Stage) searchBtn.getScene().getWindow()).close();
					Alert alert = new Alert(AlertType.WARNING);
					alert.initOwner(searchBtn.getScene().getWindow());
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.setTitle("Search For Member");
					alert.setHeaderText(null);
					alert.setContentText("User doesn't exists !!!");
					alert.showAndWait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
    
    
    
    /*
     * *******************************************************
     * Private methods
     * 
     * ********************************************************/
    /**
     * checks input validation of the passed TextField.
     * @param TF - the textField to check input validation to.
     * @return boolean: true if the input is valid, false otherwise.
     */
    private boolean checkInputValidation(TextField TF) {
		boolean retVal = true;
		if(TF.getText().isEmpty()) {
			drawField(TF,"red","red");
			retVal = false;
		}
		
		
		if (retVal && TF.getText().length() != 9) {
			drawField(TF, "red", "red");
			retVal = false;
		}
	
		return retVal;
	}
    /**
     * checks each character input into the search TextFeild.
     * called each time a new character is input.
     */
    private void searchTFHandler() {
		int length = searchTF.getText().length();
		if (length > 0) {
			drawField(searchTF, "black","transparent");
			searchTF.setPromptText("Enter user ID");
			char c = searchTF.getText().charAt(length - 1);
			if (c < '0' || c > '9' || length > 9) {
				searchTF.deleteNextChar();
				length = searchTF.getText().length();
			}
		}
	}
    /**
     * changes the style of the passed textField, changes the text fill color and the border color as specified.
     * @param TF - the textField to change the style of.
     * @param fillColor
     * @param borderColor
     */
    private void drawField(TextField TF, String fillColor, String borderColor) {
		TF.setStyle("-fx-text-fill: "+fillColor+";"+"-fx-border-color: "+borderColor+";");
		TF.setPromptText("Required field");
	}
    /**
     * loads the currently logged in user from the previous windows into an instance variable.
     * @param u - the currently logged in user.
     */
    public void loadUser(User u) {
    	this.currentUser = u;
    }
    
}
