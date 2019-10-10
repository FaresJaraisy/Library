package boundary;

import java.util.ArrayList;

import control.ManagerController;
import entity.ConstantsAndGlobalVars;
import entity.Librarian;
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

public class EmployeesListController {
	private ManagerController managerController;
	private ArrayList<Librarian> result;
	
    @FXML
    private ImageView oblImg;

    @FXML
    private TableView membershipsTV;
    private ObservableList dataOBS;

    @FXML
    private TableColumn librarianIDTC;

    @FXML
    private TableColumn firstNameTC;

    @FXML
    private TableColumn lastNameTC;

    @FXML
    private TableColumn phoneNumberTC;

    @FXML
    private TableColumn emailTC;

    @FXML
    private TableColumn employeeNumberTC;

    @FXML
    private TableColumn passwordTC;

    @FXML
    private TableColumn departmentNameTC;

    @FXML
    private TableColumn roleTC;

    @FXML
    void initialize() {
    	managerController = ManagerController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	Image img = new Image("/pictures/oblLogo.gif");
    	oblImg.setImage(img);
    	uploadEmployeesList();
    }
    
    /*****************************************************
     * 
     * private methods
     * **************************************************/
    private void uploadEmployeesList() {
    	//this.result =new ArrayList<>();
    	
    	try {
			this.result = managerController.getAllEmployees();
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
		librarianIDTC.setCellValueFactory(new PropertyValueFactory("userID"));
		firstNameTC.setCellValueFactory(new PropertyValueFactory("firstName"));
		lastNameTC.setCellValueFactory(new PropertyValueFactory("lastName"));
		phoneNumberTC.setCellValueFactory(new PropertyValueFactory("phoneNumber"));
		emailTC.setCellValueFactory(new PropertyValueFactory("email"));
		employeeNumberTC.setCellValueFactory(new PropertyValueFactory("employeeNumber"));
		passwordTC.setCellValueFactory(new PropertyValueFactory("password"));
		departmentNameTC.setCellValueFactory(new PropertyValueFactory("departmentName"));
		roleTC.setCellValueFactory(new PropertyValueFactory("role"));
    }

}
