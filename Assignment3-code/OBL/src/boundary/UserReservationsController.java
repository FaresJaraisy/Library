package boundary;

import java.util.ArrayList;

import control.BorrowsController;
import control.ReservationsController;
import entity.Borrows;
import entity.ConstantsAndGlobalVars;
import entity.Reservations;
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
 * @see boundary.UserBorrowsController very similar, only difference is it displays pending reservations.
 */
public class UserReservationsController {
	private ReservationsController reservationsController;
	private ArrayList<Reservations> res;
	private ObservableList<Reservations> data;
	private User currentUser;


	@FXML
    private ImageView oblImg;

    @FXML
    private Button closeBtn;

    @FXML
    private TableView table;

    @FXML
    private TableColumn userIDTC;

    @FXML
    private TableColumn barcodeTC;

    @FXML
    private TableColumn reserveDateTC;

    @FXML
    private TableColumn reserveStatusTC;
    
    @FXML
   	void initialize() {
       	reservationsController=ReservationsController.getInstance(ConstantsAndGlobalVars.ipAddress,
   				ConstantsAndGlobalVars.DEFAULT_PORT);
       	Image img = new Image("/pictures/oblLogo.gif");
		oblImg.setImage(img);
   		
   	}
       public void loadUser(User u) {
   		this.currentUser = u;
   		
   		try {
   			res = reservationsController.viewreservations(currentUser);
   		} catch (InterruptedException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
   		data = FXCollections.observableArrayList(res);
   		table.setItems(data);
   		userIDTC.setCellValueFactory(new PropertyValueFactory("userID"));
		barcodeTC.setCellValueFactory(new PropertyValueFactory("barcode"));
		reserveDateTC.setCellValueFactory(new PropertyValueFactory("reserveDate"));
		reserveStatusTC.setCellValueFactory(new PropertyValueFactory("status"));
       }
   		
       @FXML
       void onClose(ActionEvent event) {
    	   closeReservationWindow();
       }

       public void closeReservationWindow()
       {
       	if(res!=null)
       		res.clear();
       	Stage window = (Stage) closeBtn.getScene().getWindow();
       	window.close();
       }
}