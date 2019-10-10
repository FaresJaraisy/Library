package boundary;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import control.LockCardUponGraduation;
import control.Server;
import entity.MsgParser;
import entity.User;
import enums.UserStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ExternalGuiController {
	String ID;
    @FXML
    private Button lockUserButton;
    
    @FXML
    private TextField IDTF;
    
    @FXML
    void lockUserHandler(ActionEvent event) {

    	Timer timer = new Timer();
    	Date currentDate = new Date();
	    Calendar c = Calendar.getInstance();
	    c.setTime(currentDate);
	    // c.add(Calendar.HOUR, 48);
	    c.add(Calendar.SECOND, 1);
		// convert calendar to date
	    Date currentDatePlus1 = c.getTime();
	    timer.schedule(new LockCardUponGraduation(IDTF.getText()),currentDatePlus1);
    	
    	Stage window= (Stage) lockUserButton.getScene().getWindow();
    	window.close();
    	
    }


}
