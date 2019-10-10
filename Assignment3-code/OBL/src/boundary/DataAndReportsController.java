package boundary;

import java.io.IOException;

import control.ManagerController;
import entity.ConstantsAndGlobalVars;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DataAndReportsController {
	private ManagerController managerController;
	
    @FXML
    private ImageView oblImg;

    @FXML
    private Button membershipsBtn;

    @FXML
    private Button employeeBtn;
    
    @FXML
    private Button activityReportBtn;

    @FXML
    private Button borrowReportBtn;

    @FXML
    private Button lateReturnsBtn;
    
    @FXML
    private Button cancelBtn;

    @FXML
    void activityReportHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/ActivityReportGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Activity Report");
		primaryStage.show();
    }

    @FXML
    void borrowReportHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/PathBrowseGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Path For Browse");
		PathBrowseController pbc = loader.getController();
		pbc.setReportType("BorrowedReport");
		primaryStage.show();
    }

    @FXML
    void lateReturnsHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/PathBrowseGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Path For Browse");
		PathBrowseController pbc = loader.getController();
		pbc.setReportType("LateReturnedReport");
		primaryStage.show();
    }

    @FXML
    void employeeHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/EmployeesListGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Employees List");
		primaryStage.show();
    }

    @FXML
    void membershipsHandler(ActionEvent event) throws IOException {
    	Stage primaryStage = new Stage();
		primaryStage.setAlwaysOnTop(true);
		primaryStage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/MembershipsListGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Members List");
		primaryStage.show();
    }
    
    @FXML
    void cancelHandler(ActionEvent event) {
    	((Stage)cancelBtn.getScene().getWindow()).close();
    }

    @FXML
    void initialize() {
    	managerController = ManagerController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	Image img0 = new Image("/pictures/oblLogo.gif");
    	oblImg.setImage(img0);
    	String img = "/pictures/membershipForData.png";
    	membershipsBtn.setStyle("-fx-background-image: url('" + img + "'); " + "-fx-min-height: 117px; "
				+ "-fx-min-width: 209px;" + "-fx-background-size: 100px 100px;" + "-fx-background-repeat: no-repeat;"
				+ "-fx-background-position: center -5px;" + "-fx-border-color: #0090ff;" );
    	img = "/pictures/employeeForData.png";
    	employeeBtn.setStyle("-fx-background-image: url('" + img + "'); " + "-fx-min-height: 117px; "
				+ "-fx-min-width: 209px;" + "-fx-background-size: 80px 80px;" + "-fx-background-repeat: no-repeat;"
				+ "-fx-background-position: center 0px;" + "-fx-border-color: #0090ff;" );
    	img = "/pictures/activityReport.png";
    	activityReportBtn.setStyle("-fx-background-image: url('" + img + "'); " + "-fx-min-height: 76px; "
				+ "-fx-min-width: 209px;" + "-fx-background-size: 60px 60px;" + "-fx-background-repeat: no-repeat;"
				+ "-fx-background-position: center 0px;" + "-fx-border-color: #0090ff;" );
    	img = "/pictures/borrowReport.png";
    	borrowReportBtn.setStyle("-fx-background-image: url('" + img + "'); " + "-fx-min-height: 76px; "
				+ "-fx-min-width: 209px;" + "-fx-background-size: 60px 60px;" + "-fx-background-repeat: no-repeat;"
				+ "-fx-background-position: center 0px;" + "-fx-border-color: #0090ff;" );
    	img = "/pictures/lateReturnReport.png";
    	lateReturnsBtn.setStyle("-fx-background-image: url('" + img + "'); " + "-fx-min-height: 76px; "
				+ "-fx-min-width: 209px;" + "-fx-background-size: 60px 60px;" + "-fx-background-repeat: no-repeat;"
				+ "-fx-background-position: center 0px;" + "-fx-border-color: #0090ff;" );
    }

}
