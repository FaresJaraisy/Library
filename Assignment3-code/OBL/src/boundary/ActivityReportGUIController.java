package boundary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.Path;
import com.itextpdf.text.pdf.parser.clipper.Paths;
import com.sun.org.apache.bcel.internal.generic.FRETURN;
import com.itextpdf.text.pdf.*;

import control.ActivityReportController;
import control.ClientMain;
import control.FaultsHistoryController;
import control.ManagerController;
import entity.ActivityReport;
import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.IReport;
import entity.MyFile;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ActivityReportGUIController {
	private ActivityReportController activityReportController;
	private MyFile reportFile;
	private String popUpMsg = "";
	int currentYear;
	int currentMonth;
	int fromYear;
	int toYear;
	int fromMonth;
	int toMonth;
	
	@FXML
    private ImageView oblImg;

    @FXML
    private TextField fromYearTF;

    @FXML
    private ComboBox fromMonthCB;

    @FXML
    private Button createBtn;

    @FXML
    private TextField toYearTF;

    @FXML
    private ComboBox toMonthCB;
    private ObservableList<Integer> cbOBS;
    
    @FXML
    private AnchorPane fileAP;

    @FXML
    private TextField dirPathTF;

    @FXML
    private Button browseBtn;

    @FXML
    private Button cancelBtn;
    
    @FXML
    private Button viewBtn;

    @FXML
    void initialize() {
    	activityReportController = ActivityReportController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);

    	fileAP.setVisible(false);
    	Image img = new Image("/pictures/oblLogo.gif");
    	oblImg.setImage(img);
    	fromYearTF.textProperty().addListener((observable, oldValue, newValue) -> yearTFHandler(fromYearTF));
    	toYearTF.textProperty().addListener((observable, oldValue, newValue) -> yearTFHandler(toYearTF));
    	
    	ArrayList<Integer> arr = new ArrayList<>();
    	for(int i =1 ; i < 13 ; i++) {
    			arr.add(i);
    	}
		cbOBS = FXCollections.observableArrayList(arr);
		fromMonthCB.setItems(cbOBS);
		toMonthCB.setItems(cbOBS);
		fromMonthCB.getSelectionModel().select(0);
		toMonthCB.getSelectionModel().select(0);
    }
    
    @FXML
    void createHandler(ActionEvent event) {
    	boolean isOk;
    	
    	isOk = checkInputValidation();
    	if(isOk) {
    		fileAP.setVisible(true);
    		viewBtn.setVisible(false);
    	}
    }
    
    @FXML
    void browseHandler(ActionEvent event) {
    	boolean fileExists ;
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
    	
		ActivityReport report = new ActivityReport();

		String str = fromYear + "-" + fromMonth + "-" + 1;
		java.sql.Date fromDate = java.sql.Date.valueOf(str);
		report.setFromDate(fromDate);
		if(toMonth == 12) str = (toYear+1) + "-" + 1 +"-"+ 1;
		else str = toYear + "-" + (toMonth+1) + "-" + 1 ;
		java.sql.Date toDate = java.sql.Date.valueOf(str);
		
		report.setToDate(toDate);
		report.setName(fromDate.toString() + "TO" + toDate.toString());
		report.setPath(dirPathTF.getText());
		reportFile = new MyFile(report.getPath()+"\\"+report.getName());
		fileExists = activityReportController.createReport(report);
		
		if(fileExists) msg="the report already exists, its downloaded from DB";
		else msg="the report not exists, created new one";
		
		viewBtn.setVisible(true);
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(createBtn.getScene().getWindow());
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle("Report Status");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
		
    }
    
    @FXML
    void viewHandler(ActionEvent event) {
		// open the file
		File file = new File(reportFile.getFileName()+".pdf");
		// view it using host services
		HostServices hostServices = ClientMain.myGetHostServices();
		hostServices.showDocument(file.getAbsolutePath());
		((Stage) viewBtn.getScene().getWindow()).close();
    }

    @FXML
    void cancelHandler(ActionEvent event) {
    	((Stage) cancelBtn.getScene().getWindow()).close();
    }
    
	/*************************************************************************
	 * start with private methods
	 * 
	 * first 2 methods for each one 
	 * the rest check before use
	 *  
	 *************************************************************************/
    
    private boolean checkInputValidation() {
		boolean retVal = true;
		Calendar now = Calendar.getInstance();
		currentYear = now.get(Calendar.YEAR);
		currentMonth = now.get(Calendar.MONTH);
		fromYear = Integer.parseInt(0 + fromYearTF.getText());
		toYear = Integer.parseInt(0 + toYearTF.getText());
		fromMonth = (int) fromMonthCB.getSelectionModel().getSelectedItem();
		toMonth = (int) toMonthCB.getSelectionModel().getSelectedItem();
		
		if(fromYearTF.getText().isEmpty() || fromYearTF.getText().length() < 4) {
			drawField(fromYearTF,"red","red");
			retVal = false;
		}
		
		if(toYearTF.getText().isEmpty() || toYearTF.getText().length() < 4) {
			drawField(toYearTF,"red","red");
			retVal = false;
		}
		
		if (fromYear > toYear || ((fromYear == toYear) && (fromMonth > toMonth)) || fromYear > currentYear || toYear > currentYear || (currentYear == fromYear && fromMonth > currentMonth) || (currentYear == toYear && toMonth > (currentMonth+1)) ) {
			retVal = false;
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.initOwner(createBtn.getScene().getWindow());
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Wrong input");
			alert.setHeaderText(null);
			alert.setContentText("Range date invalid !!!");
			alert.showAndWait();
		} 
		
		
		return retVal;
	}
	
	private void drawField(TextField TF, String fillColor, String borderColor) {
		TF.setStyle("-fx-text-fill: "+fillColor+";"+"-fx-border-color: "+borderColor+";");
		TF.setPromptText("Required field");
	}
	
	private void yearTFHandler(TextField TF) {
		int length = TF.getText().length();
		if (length > 0) {
			drawField(TF, "black","transparent");
			TF.setPromptText("Insert year number");
			char c = TF.getText().charAt(length - 1);
			if (c < '0' || c > '9' || length > 4) {
				TF.deleteNextChar();
				length = TF.getText().length();
			}
		}
	}
	

}
