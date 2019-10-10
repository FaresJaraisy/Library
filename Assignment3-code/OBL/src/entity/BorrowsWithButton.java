package entity;

import java.util.Date;

import javafx.scene.control.Button;
/**
 * a special entity class that stores borrows with the addition of a button, 
 * it's use is to display in a tableView.
 * the reason for creating this class is because JavaFX Button class is not {@code Serializable}.
 */
public class BorrowsWithButton extends Borrows {
  private Button DelayBorrowBtn;

/**
 * {@inheritDoc}
 * @param userID
 * @param barcode
 * @param librarianID
 * @param borrowDate
 * @param returnDate
 * @param actualReturnDate
 * @param status
 */
public BorrowsWithButton(String userID,String barcode,String librarianID, Date borrowDate,Date returnDate,Date actualReturnDate,enums.BorrowStatus status) {
	super(userID,barcode,librarianID,borrowDate,returnDate,actualReturnDate,status);
	this.DelayBorrowBtn=new Button("Delay Borrow");
}

/**
 * 
 * @return
 */
public Button getDelayBorrowBtn() {
	return DelayBorrowBtn;
}

/**
 * 
 * @param delayBorrowBtn
 */
public void setDelayBorrowBtn(Button delayBorrowBtn) {
	DelayBorrowBtn = delayBorrowBtn;
}

}