package boundary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import control.EmailController;
import control.FaultsHistoryController;
//import control.EmailController;
import control.LibrarianController;
import entity.ConstantsAndGlobalVars;
import entity.User;
/**
 * This is the GUI controller responsible for member creations.
 */
public class CreateMemberController {
	/**
	 * instance variables:
	 * librarianController - an instance of LibrarianController for use when saving the newly created member in the DB.
	 */
	private LibrarianController librarianController;
	private FaultsHistoryController faultsHistoryController;
	private String emailSubject = "Verfication mail";
	private final String varNum = "BellaCiao";
	private String emailMsg = ("Your varfication number is : " + varNum);
	
	@FXML
	private TextField firstNameTF;
	
	@FXML
	private TextField lastNameTF;
	
	@FXML
	private TextField userIDTF;
	
	@FXML
	private TextField membershipNumberTF;

	@FXML
	private TextField emailTF;

	@FXML
	private Button createBtn;

	@FXML
	private TextField phoneNumberTF;

	@FXML
	private Button verifyBtn;
	
	@FXML
    private TextField verficationNumberTF;

	@FXML
	private TextField passwordTF;

	@FXML
	private Button cancelBtn;
	/**
	 * This method is called when the FXML file is loaded, it initialises the variables and some GUI elements.
	 */
	@FXML
	void initialize() {
		membershipNumberTF.setDisable(true);
		verficationNumberTF.setDisable(true);
		firstNameTF.textProperty().addListener((observable, oldValue, newValue) -> firstNameHandler());
		lastNameTF.textProperty().addListener((observable, oldValue, newValue) -> lastNameHandler());
		userIDTF.textProperty().addListener((observable, oldValue, newValue) -> userIDHandler());
		emailTF.textProperty().addListener((observable, oldValue, newValue) -> emailTFHandler());
		verficationNumberTF.textProperty().addListener((observable, oldValue, newValue) -> verficationNumberTFHandler());
		passwordTF.textProperty().addListener((observable, oldValue, newValue) -> passwordTFHandler());
		phoneNumberTF.textProperty().addListener((observable, oldValue, newValue) -> phoneNumberHandler());
	}
	/**
	 * checks if email is a valid one (by using REGEX).
	 * Simulates email sending (a unified verification code chosen from the start by us), 
	 * if verification code matches, user may continue.
	 * @param event - auto-generated by SceneBuilder.
	 */
	@FXML
	void verifyHandler(ActionEvent event)  {
		Pattern p = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+");
		Matcher m = p.matcher(emailTF.getText());
		if (m.find() && m.group().equals(emailTF.getText())) {
			EmailController emailController = EmailController.getInstance();
			emailController.sendVerficationMail(emailTF.getText(), emailSubject, emailMsg);
			//showPopUpEmailWindow();
			verficationNumberTF.setDisable(false);
		} else {
			verficationNumberTF.setDisable(true);
			emailTF.setStyle("-fx-border-color: red;");
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(verifyBtn.getScene().getWindow());
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.setTitle("Validate Email");
			alert.setHeaderText(null);
			alert.setContentText("Please Enter Valid Email");
			alert.showAndWait();
		}
	}
	/**
	 * closes the current window
	 * @param event - auto-generated by SceneBuilder.
	 */
	@FXML
	void cancelHandler(ActionEvent event) {
		((Stage) cancelBtn.getScene().getWindow()).close();
	}
	/**
	 * this method checks if all input is valid and commences in user creation, an alert is displayed with the result (success, failure)
	 * @param event - auto-generated by SceneBuilder.
	 */
	@FXML
	void createHandler(ActionEvent event) {
		boolean isOk = true;
		isOk = ((checkInputValidation(firstNameTF) && isOk) ? true : false);
		isOk = ((checkInputValidation(lastNameTF) && isOk) ? true : false);
		isOk = ((checkInputValidation(userIDTF) && isOk) ? true : false);
		isOk = ((checkInputValidation(emailTF) && isOk) ? true : false);
		isOk = ((checkInputValidation(verficationNumberTF) && isOk) ? true : false);
		isOk = ((checkInputValidation(passwordTF) && isOk) ? true : false);
		isOk = ((checkInputValidation(phoneNumberTF) && isOk) ? true : false);
		
		if(isOk) {
			//check if user already exists
			
			// add user to the table of users 
			User newUser = new User();
			newUser.setFirstName(firstNameTF.getText());
			newUser.setLastName(lastNameTF.getText());
			newUser.setUserID(userIDTF.getText());
			newUser.setMembershipNumber(membershipNumberTF.getText());
			newUser.setEmail(emailTF.getText());
			newUser.setPassword(passwordTF.getText());
			newUser.setPhoneNumber(phoneNumberTF.getText());
			newUser.setStatus(enums.UserStatus.Active);
			newUser.setStrikes(0);	
			
			librarianController = LibrarianController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
			faultsHistoryController = FaultsHistoryController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
			try {
				if(librarianController.addNewUser(newUser)) {
					isOk = faultsHistoryController.addFault(enums.FaultDesc.Active, userIDTF.getText());
					((Stage) createBtn.getScene().getWindow()).close();
					Alert alert = new Alert(AlertType.INFORMATION);
					//alert.initOwner(verifyBtn.getScene().getWindow());
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.setTitle("Add user");
					alert.setHeaderText(null);
					alert.setContentText("User successfully added");
					alert.showAndWait();
				}else {
					Alert alert = new Alert(AlertType.WARNING);
					alert.initOwner(verifyBtn.getScene().getWindow());
					alert.initModality(Modality.APPLICATION_MODAL);
					alert.setTitle("Add user");
					alert.setHeaderText(null);
					alert.setContentText("User already exists!!!");
					alert.showAndWait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * ************************************************************************
	 * start with private methods
	 * 
	 * first 2 methods for each one 
	 * the rest check before use
	 *  
	 *************************************************************************/
	/**
	 * given a textField as an argument, this method checks if the text in it is valid
	 * @param TF - the textField to check
	 * @return true if input is valid, false otherwise.
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
		
		if(TF == verficationNumberTF && retVal) {
			retVal = (verficationNumberTF.getText().equals(varNum)? true : false);
			if(!retVal) 
				drawField(TF,"red" , "red");
		}
		return retVal;
	}
	/**
	 * changes the style (text fill and border colour) of the passed textField.
	 * @param TF - the textField to change the style of.
	 * @param fillColor - the new text fill colour.
	 * @param borderColor - the new border colour.
	 */
	private void drawField(TextField TF, String fillColor, String borderColor) {
		TF.setStyle("-fx-text-fill: "+fillColor+";"+"-fx-border-color: "+borderColor+";");
		TF.setPromptText("Required field");
	}
	
	/**
	 * checks if "first name" textField contains legal input at any given instant. 
	 */
	private void firstNameHandler() {
		int length = firstNameTF.getText().length();
		if (length > 0) {
			drawField(firstNameTF, "black","transparent");
			firstNameTF.setPromptText("Fill in first name");
			char c = firstNameTF.getText().charAt(length - 1);
			if ((c < 'A' || (c > 'Z' && c < 'a') || c > 'z') && c != ' ') {
				firstNameTF.deleteNextChar();
				length = firstNameTF.getText().length();
			}
			if (length > 0) {
				String text = firstNameTF.getText();
				text = text.toLowerCase();
				StringBuilder sb = new StringBuilder(text);
				sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
				text = sb.toString();
				firstNameTF.setText(text);
			}
		}
	}
	/**
	 * checks if "last name" textField contains legal input at any given instant. 
	 */
	private void lastNameHandler() {
		int length = lastNameTF.getText().length();
		if (length > 0) {
			drawField(lastNameTF, "black","transparent");
			lastNameTF.setPromptText("Fill in last name");
			char c = lastNameTF.getText().charAt(length - 1);
			if ((c < 'A' || (c > 'Z' && c < 'a') || c > 'z') && c != ' ') {
				lastNameTF.deleteNextChar();
				length = lastNameTF.getText().length();
			}
			if (length > 0) {
				String text = lastNameTF.getText();
				text = text.toLowerCase();
				StringBuilder sb = new StringBuilder(text);
				sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
				text = sb.toString();
				lastNameTF.setText(text);
			}
		}
	}
	/**
	 * checks if "user ID" textField contains legal input at any given instant. 
	 */
	private void userIDHandler() {
		int length = userIDTF.getText().length();
		if (length > 0) {
			drawField(userIDTF, "black","transparent");
			userIDTF.setPromptText("Fill in user ID");
			drawField(membershipNumberTF, "black","transparent");
			membershipNumberTF.setPromptText("Generated automatically");
			char c = userIDTF.getText().charAt(length - 1);
			if (length < 9)
				membershipNumberTF.clear();
			if (c < '0' || c > '9' || length > 9) {
				userIDTF.deleteNextChar();
				length = userIDTF.getText().length();
			}
			if (length == 9) {
				membershipNumberTF.setText(userIDTF.getText(5, 9));
			}
		}
	}
	/**
	 * checks if "email" textField contains legal input at any given instant. 
	 */
	private void emailTFHandler() {
		int length = emailTF.getText().length();
		if(length > 0) {
			drawField(emailTF, "black","transparent");
			emailTF.setPromptText("Fill in email");
//			drawField(verficationNumberTF, "black","transparent");
//			verficationNumberTF.setPromptText("Fill in verfication number");
		}
	}
	/**
	 * checks if "verification number" textField contains legal input at any given instant. 
	 */
	private void verficationNumberTFHandler() {
		int length = verficationNumberTF.getText().length();
		if(length > 0) {
			drawField(verficationNumberTF, "black","transparent");
			verficationNumberTF.setPromptText("Fill in verfication number");
		}
	}
	/**
	 * checks if "password" textField contains legal input at any given instant. 
	 */
	private void passwordTFHandler() {
		int length = passwordTF.getText().length();
		if(length > 0) {
			drawField(passwordTF, "black","transparent");
			passwordTF.setPromptText("Fill in email");
		}
	}
	/**
	 * checks if "phone number" textField contains legal input at any given instant. 
	 */
	private void phoneNumberHandler() {
		int length = phoneNumberTF.getText().length();
		if (length > 0) {
			drawField(phoneNumberTF, "black","transparent");
			phoneNumberTF.setPromptText("Fill in phone number");
			char c = phoneNumberTF.getText().charAt(length - 1);
			if (c < '0' || c > '9' || length > 10) {
				phoneNumberTF.deleteNextChar();
			}
		}
	}
	/**
	 * shows a simulation of an email.
	 */
	private void showPopUpEmailWindow() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(verifyBtn.getScene().getWindow());
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle("Verfication mail");
		alert.setHeaderText(null);
		alert.setContentText("Your verfication password is: "+varNum);
		alert.showAndWait();
	}

	
	
	
}
