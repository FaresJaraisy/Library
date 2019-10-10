package boundary;

import control.FaultsHistoryController;
import control.ManagerController;
import entity.ConstantsAndGlobalVars;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
/**
 * {@inheritDoc}
 * asks for confirmation concerning user lock messages (whether to lock or not).
 */


public class viewLockMessageController extends viewMessageController{
	private ManagerController managerController;
	private FaultsHistoryController faultsHistoryController;
	private boolean retVal;
	private String popUpMsg = "";
	
    @FXML
    private Button lockUserButton;
    
    @FXML
    void onClickLockButton(ActionEvent event) {
    	//lock account super.userID.
    	faultsHistoryController = FaultsHistoryController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	managerController = ManagerController.getInstance(ConstantsAndGlobalVars.ipAddress, ConstantsAndGlobalVars.DEFAULT_PORT);
    	try {
			retVal = managerController.changeMemberStatus(super.userID, "Locked");
			if(retVal) { 
				retVal = faultsHistoryController.addFault(enums.FaultDesc.Locked, super.userID);
				popUpMsg = "member status updated succssefully.";
				lockUserButton.setVisible(false);
			}
			else
				popUpMsg = "Can't change member status. try again !!!";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			popUpMsg = "Somthing went wrong with the sql !!!";
		}
    	
    	Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(lockUserButton.getScene().getWindow());
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setTitle("Updating status");
		alert.setHeaderText(null);
		if(popUpMsg.equals("")) popUpMsg = "Nothing changed";
		alert.setContentText(popUpMsg);
		alert.showAndWait();
    }



}
