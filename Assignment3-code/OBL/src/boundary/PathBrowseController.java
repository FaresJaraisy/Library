package boundary;

import java.io.File;

import control.BorrowedReportController;
import control.ClientMain;
import control.LateReturnReportController;
import entity.ConstantsAndGlobalVars;
import entity.Report;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PathBrowseController {
	private LateReturnReportController lateReturnReportController;
	private BorrowedReportController borrowedReportController ;
	private String reportType;
	private boolean retVal;

    @FXML
    private ImageView oblImg;

    @FXML
    private TextField dirPathTF;

    @FXML
    private Button browseBtn;

    @FXML
    private Button viewBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    void browseHandler(ActionEvent event) {
    	String msg = "";
		// change textField style to default.
		dirPathTF.setStyle("-fx-text-fill: black; -fx-border-color: transparent;");
		dirPathTF.setPromptText("Choose download path");
		// open a directory chooser to choose download path
		Stage stage = (Stage) browseBtn.getScene().getWindow();
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Choose Download Destination");
		File selectedDirectory = chooser.showDialog(stage);
		if (selectedDirectory != null) {
			String dest = selectedDirectory.getAbsolutePath();
			dirPathTF.setText(dest);
		}
		// if user didn't choose, provide feedback and display error message
		if (dirPathTF.getText().isEmpty()) {
			dirPathTF.setStyle("-fx-text-fill: red; -fx-border-color: red;");
			dirPathTF.setText("Please provide download path");
			return;
		}
		
		viewBtn.setVisible(true);
		Report report = new Report();
		report.setPath(dirPathTF.getText());
		if(reportType.equals("BorrowedReport")) {
			report.setName("BorrowedReport");
			retVal = borrowedReportController.createReport(report);
		}
		
		if(reportType.equals("LateReturnedReport")) {
			report.setName("LateReturnedReport");
			retVal = lateReturnReportController.createReport(report);
		}
		
		viewBtn.setVisible(true);
		
		if(retVal) msg = "Report Created Succssefully";
		else msg = "Can't Create Report";
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(viewBtn.getScene().getWindow());
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle("Report Status");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
		
    }

    @FXML
    void cancelHandler(ActionEvent event) {
    	((Stage)cancelBtn.getScene().getWindow()).close();
    }

    @FXML
    void viewHandler(ActionEvent event) {
		// open the file
		File file = new File(dirPathTF.getText() + "\\"+reportType+".pdf");
		// view it using host services
		HostServices hostServices = ClientMain.myGetHostServices();
		hostServices.showDocument(file.getAbsolutePath());
		((Stage) viewBtn.getScene().getWindow()).close();
    }

    @FXML
    void initialize() {
    	borrowedReportController = BorrowedReportController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	lateReturnReportController = LateReturnReportController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	Image img0 = new Image("/pictures/oblLogo.gif");
    	oblImg.setImage(img0);
    	
    	viewBtn.setVisible(false);
    }
    
    /*
     * *************************************************
     * private methods
     * ************************************************/
    public void setReportType(String type) {
    	this.reportType = type;
    }
    
}
