package boundary;

import java.util.ArrayList;

import control.BorrowsController;
import control.HistoryController;
import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.History;
import entity.History;
import entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
/**
 * @see boundary.UserBorrowsController very similar, only difference is it displays the history (inactive borrows and reservation).
 */
public class UserHistoryController {

	private HistoryController historyController;
	private ArrayList<History> res;
	private ObservableList<History> data;
	private User currentUser;

	@FXML
	private ImageView oblImg;

	@FXML
	private TableView table;

	@FXML
	private TableColumn userIDTC;

	@FXML
	private TableColumn barcodeTC;

	@FXML
	private TableColumn eventTC;

	@FXML
	private TableColumn eventDateTC;

	@FXML
	private Button closeBtn;


    @FXML
	void initialize(){
    	historyController=HistoryController.getInstance(ConstantsAndGlobalVars.ipAddress,
				ConstantsAndGlobalVars.DEFAULT_PORT);
    	Image img = new Image("/pictures/oblLogo.gif");
    	oblImg.setImage(img);
		
	}
    public void loadUser(User u) {
		this.currentUser = u;
		
		try {
			res = historyController.viewhistory(currentUser);
			//System.out.println(res);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		data = FXCollections.observableArrayList(res);
		table.setItems(data);
		userIDTC.setCellValueFactory(new PropertyValueFactory("userID"));
		barcodeTC.setCellValueFactory(new PropertyValueFactory("barcode"));
		eventTC.setCellValueFactory(new PropertyValueFactory("type"));
		eventDateTC.setCellValueFactory(new PropertyValueFactory("eventDate"));
	}
    
    @FXML
    void onClose(ActionEvent event) {
    	closeHistoryWindow();
    }
    
    public void closeHistoryWindow()
    {
    	if(res!=null)
    		res.clear();
    	Stage window = (Stage) closeBtn.getScene().getWindow();
    	window.close();
    }
    
}