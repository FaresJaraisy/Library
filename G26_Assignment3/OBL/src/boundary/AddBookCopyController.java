package boundary;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.log.SysoCounter;

import control.BookController;
import control.BookCopyController;
import entity.BookCopies;
import entity.ConstantsAndGlobalVars;
import enums.BookType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
/**
 * <h1><b>AddBookCopyController</b></h1>
 * This is the GUI controller responsible for adding book copies to the inventory - part of the inventory management functionality.
 */
public class AddBookCopyController {
	/**
	 * instance variables:
	 * bookCopyController - an instance of BookCopyController to use when adding the copy.
	 * bookController - an instances of BookController to use when checking the existence of a book. 
	 */
	private BookCopyController bookCopyController;
	private BookController bookController;
	@FXML
	private TextField catalogNumberTF;

	@FXML
	private TextField barcodeTF;

	@FXML
	private DatePicker purchaseDateDP;

	@FXML
	private ComboBox statusCB;
	private ObservableList<String> statusList;

	@FXML
	private Button addCopyBtn;

	@FXML
	private Button cancelBtn;

	@FXML
	private ImageView oblImg;

	@FXML
	private Label resultLabel;

	/**
	 * This method handles the event where the user has finished inputting all fields and clicks add.
	 * It validates all fields are not empty, if one is empty it displays an alert.
	 * <br></br> If all fields are not empty it calls the {@link control.BookController#getBook(String)} method from the {@link control.BookController} class.
	 * checks if the book exists, if yes it goes on to call the {@link control.BookCopyController#addCopy(BookCopies)} method from the {@link control.BookCopyController} class.
	 * displays feedback depending on return result.
	 * @param event auto-generated by SceneBuilder.
	 */
	@FXML
	void addCopyHandler(ActionEvent event) {
		boolean isOk = true;
		if (catalogNumberTF.getText().isEmpty())
			isOk = false;
		if(purchaseDateDP.getValue() == null)
			isOk = false;
		try {
			enums.BookCopyStatus
			.valueOf(statusList.get(statusCB.getSelectionModel().getSelectedIndex()));
		}catch(ArrayIndexOutOfBoundsException e) {
			isOk = false;
		}
		if(!isOk) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("OOPS!");
			alert.setHeaderText("One or more of the fields is empty.");
			ButtonType buttonTypeCancel = new ButtonType("Ok", ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(buttonTypeCancel);
			alert.showAndWait();
			return;
		}
		String catalogNumber = catalogNumberTF.getText();
		System.out.println(catalogNumber);
		System.out.println(bookController.getBook(catalogNumber)) ;
		if (bookController.getBook(catalogNumber) == null) {
			System.out.println("fdffyffyfyfytf");
			resultLabel.setText("No such book!");
			resultLabel.setTextFill(Color.RED);
			return;
		}
		String barcode = barcodeTF.getText();
		LocalDate purchaseDate_ld = purchaseDateDP.getValue();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date purchaseDate = Date.from(purchaseDate_ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
		format.format(purchaseDate);
		enums.BookCopyStatus status = enums.BookCopyStatus
				.valueOf(statusList.get(statusCB.getSelectionModel().getSelectedIndex()));
		BookCopies bookCopy = new BookCopies(barcode, catalogNumber, purchaseDate, status);
		int result = bookCopyController.addCopy(bookCopy);
		System.out.println("ymaaa");
		if( result == 1) {
			resultLabel.setText("Copy Added Successfully");
			resultLabel.setTextFill(Color.GREEN);
		}else if(result == 2){
			resultLabel.setText("Copy already exists");
			resultLabel.setTextFill(Color.RED);
		}else {
			resultLabel.setText("You can't add more copies");
			resultLabel.setTextFill(Color.RED);
		}
	}
	/**
	 * This method closes the current window and goes back to the previous window.
	 * @param event auto-generated by SceneBuilder.
	 * @throws IOException thrown if an error occurs when opening the FXML file.
	 */
	@FXML
	void cancelHandler(ActionEvent event) throws IOException {
		Stage primaryStage = (Stage) cancelBtn.getScene().getWindow();
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/boundary/InventoryManagementGUI.fxml").openStream());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Inventory Management");
		primaryStage.show();
	}
	/**
	 * This method is called when the FXML file is loaded, it initialises the variables and some GUI elements.
	 */
	@FXML
	void initialize() {
		bookCopyController = BookCopyController.getInstance(ConstantsAndGlobalVars.ipAddress,
				ConstantsAndGlobalVars.DEFAULT_PORT);
		bookController = BookController.getInstance(ConstantsAndGlobalVars.ipAddress,
				ConstantsAndGlobalVars.DEFAULT_PORT);
		fillComboBox();
	}
	/**
	 * This {@code private} method is called upon initialisation, it fills the comboBox with values.
	 */
	private void fillComboBox() {
		ArrayList<String> arr = new ArrayList<>();
		for (enums.BookCopyStatus b : enums.BookCopyStatus.values())
			arr.add(b.name());
		statusList = FXCollections.observableArrayList(arr);
		statusCB.setItems(statusList);
		statusCB.getSelectionModel().select(0);
	}
	/**
	 * This method is called when the user moves from book addition to copy addition it loads the catalogue number of the book just added to facilitate the operation for the user.
	 * @param catalogNumber - The book catalogue number to which the user wants to add copies to
	 */
	public void loadCN(String catalogNumber) {
		this.catalogNumberTF.setText(catalogNumber);
	}
}