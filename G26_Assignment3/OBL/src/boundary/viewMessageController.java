package boundary;

import java.net.URL;
import java.util.ResourceBundle;

import entity.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
/**
 * views messages.
 */
public class viewMessageController {

    @FXML
    protected Button closeButton;

    @FXML
    protected Label titleLabel;

    @FXML
    protected Label IDLabel;
    
    @FXML
    protected TextArea textAreaMessage;
    
    protected String userID;

    @FXML
    void onCloseHandler(ActionEvent event) {
    	Stage window = (Stage) closeButton.getScene().getWindow();
    	window.close();
    }

    @FXML
    void initialize() {
    	
    }
    
    public void loadMessage(Message message)
    {
    	userID= message.getUser();
    	titleLabel.setText(message.getTitle());
    	IDLabel.setText(message.getUser());
    	textAreaMessage.setText(message.getMsg());
    }
}
