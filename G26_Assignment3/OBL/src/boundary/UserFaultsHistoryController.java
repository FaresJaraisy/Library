package boundary;

import java.util.ArrayList;

import control.BorrowsController;
import control.FaultsHistoryController;
import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.FaultsHistory;
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
 * 
 * @see boundary.UserBorrowsController very similar, only difference is it displays the fault hsitory (account freezes, locks, late returns, etc.).
 */
public class UserFaultsHistoryController {
	private FaultsHistoryController faultsHistoryController;
	private ArrayList<FaultsHistory> res;
	private ObservableList<FaultsHistory> data;
	private User currentUser;
	
	@FXML
    private ImageView oblImg;

    @FXML
    private TableView table;

    @FXML
    private TableColumn userIDTC;

    @FXML
    private TableColumn faultsDescriptionTC;

    @FXML
    private TableColumn dateTC;

    @FXML
    private Button closeBtn;

    @FXML
    void onClose(ActionEvent event) {
    	closeFaultsHistoryWindow();
    }
    
    public void closeFaultsHistoryWindow()
    {
    	if(res != null)
    		res.clear();
    	Stage window = (Stage) closeBtn.getScene().getWindow();
    	window.close();
    }
	/**
	 * called upon loading the FXML file, initializes some GUI elements and instance variables.
	 */
    @FXML
    void initialize() {
    	faultsHistoryController = FaultsHistoryController.getInstance(ConstantsAndGlobalVars.ipAddress,
				ConstantsAndGlobalVars.DEFAULT_PORT);
		Image img = new Image("/pictures/oblLogo.gif");
		oblImg.setImage(img);
    }
    
    public void loadUser(User u) {
    	this.currentUser = u;
    	
    	try {
			res = faultsHistoryController.viewFaultsHistory(currentUser);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		data = FXCollections.observableArrayList(res);
		table.setItems(data);
		userIDTC.setCellValueFactory(new PropertyValueFactory("userID"));
		faultsDescriptionTC.setCellValueFactory(new PropertyValueFactory("fault"));
		dateTC.setCellValueFactory(new PropertyValueFactory("faultDate"));
    }
}
