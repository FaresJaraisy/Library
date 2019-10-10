package boundary;

import java.io.IOException;
import java.util.ArrayList;

import control.ManagerController;
import entity.ConstantsAndGlobalVars;
import entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;

public class MembershipsListController {
	private ManagerController managerController;
	private ArrayList<User> result;
	
    @FXML
    private ImageView oblImg;

    @FXML
    private TableView membershipsTV;
    private ObservableList dataOBS;
    
    @FXML
    private TableColumn userIDTC;

    @FXML
    private TableColumn firstNameTC;

    @FXML
    private TableColumn lastNameTC;

    @FXML
    private TableColumn phoneNumberTC;

    @FXML
    private TableColumn emailTC;

    @FXML
    private TableColumn membershipNumberTC;

    @FXML
    private TableColumn passwordTC;

    @FXML
    private TableColumn strikesTC;

    @FXML
    private TableColumn statusTC;

    @FXML
    void initialize() {
    	managerController = ManagerController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	Image img = new Image("/pictures/oblLogo.gif");
    	oblImg.setImage(img);
    	uploadMembersList();
    }
    
    /*****************************************************
     * 
     * private methods
     * **************************************************/
    private void uploadMembersList() {
    	//this.result =new ArrayList<>();
    	
    	try {
			this.result = managerController.getAllMembers();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.result = null;
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(oblImg.getScene().getWindow());
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Members List");
			alert.setHeaderText(null);
			alert.setContentText("there is no members.");
			alert.showAndWait();
		}

		dataOBS = FXCollections.observableArrayList(this.result);
		membershipsTV.setItems(dataOBS);
		userIDTC.setCellValueFactory(new PropertyValueFactory("userID"));
		firstNameTC.setCellValueFactory(new PropertyValueFactory("firstName"));
		lastNameTC.setCellValueFactory(new PropertyValueFactory("lastName"));
		phoneNumberTC.setCellValueFactory(new PropertyValueFactory("phoneNumber"));
		emailTC.setCellValueFactory(new PropertyValueFactory("email"));
		membershipNumberTC.setCellValueFactory(new PropertyValueFactory("membershipNumber"));
		passwordTC.setCellValueFactory(new PropertyValueFactory("password"));
		strikesTC.setCellValueFactory(new PropertyValueFactory("strikes"));
		statusTC.setCellValueFactory(new PropertyValueFactory("status"));
    }
}
