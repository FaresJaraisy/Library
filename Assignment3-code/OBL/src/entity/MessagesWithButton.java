package entity;

import java.util.Date;

import enums.MessageType;
import javafx.scene.control.Button;
/**
 * a special entity class that stores messages with the addition of a button, 
 * it's use is to display in a tableView.
 * the reason for creating this class is because JavaFX Button class is not {@code Serializable}.
 */
public class MessagesWithButton extends Message {
	private Button view;
	private Button delete;
	/**
	 * {@inheritDoc}
	 * @param messageType
	 * @param title
	 * @param mess
	 * @param belong
	 * @param messageDate
	 * @param user
	 */
	public MessagesWithButton(MessageType messageType, String title, String mess, String belong, Date messageDate,
			String user) {
		super(messageType, title, mess, belong, messageDate, user);
		// TODO Auto-generated constructor stub
		view= new Button("view");
		delete= new Button("delete");
	}
	/**
	 * 
	 * @return
	 */
	public Button getDelete() {
		return delete;
	}


	/**
	 * 
	 * @return
	 */
	public Button getView() {
		return view;
	}

}	
	